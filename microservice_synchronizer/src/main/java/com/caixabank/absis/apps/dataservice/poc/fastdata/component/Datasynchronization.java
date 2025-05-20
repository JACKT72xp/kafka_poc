package com.caixabank.absis.apps.dataservice.poc.fastdata.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.kafka.streams.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.caixabank.absis.apps.dataservice.poc.fastdata.config.RuleConfigProperties;
import com.caixabank.absis.apps.dataservice.poc.fastdata.config.RuleConfigProperties.Rule;
import com.caixabank.absis.apps.dataservice.poc.fastdata.constants.PocFastDataConstants;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key.KeySchema;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.utils.EventToLaunch;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.ValueSchema;

@Component
public class Datasynchronization {

	private static final Logger log = LoggerFactory.getLogger(Datasynchronization.class);

	private HashMap<String, HashMap<Integer, KeyValue<KeySchema, ValueSchema>>> eventsMap = new HashMap<>();

	private HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> valuesToCheck = new HashMap<>();

	@Autowired
	private RuleConfigProperties ruleConfigProperties;

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Datasynchronization datasynchronizationSingleton() {
		return new Datasynchronization();
	}

	public void addEventfromTopic(String table, KeySchema key, ValueSchema data) {
		log.info(String.format("Table: %s, key: %s", table, key.getPayload()));
		KeyValue<KeySchema, ValueSchema> keyValue = new KeyValue<KeySchema, ValueSchema>(key, data);
		HashMap<Integer, KeyValue<KeySchema, ValueSchema>> eventsForTopic = eventsMap.get(data.getSchema().getName());
		if (eventsForTopic == null) {
			eventsForTopic = new HashMap<Integer, KeyValue<KeySchema, ValueSchema>>();
		}
		eventsForTopic.put(key.getPayload().asInt(), keyValue);
		eventsMap.put(data.getSchema().getName(), eventsForTopic);

		buildUpdatedCriteria(table, key, data, valuesToCheck);

	}

	private void buildUpdatedCriteria(String tableName, KeySchema key, ValueSchema data,
			HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> valuesToCheck) {
		HashMap<Integer, Integer> listValues;

		for (Rule column : ruleConfigProperties.getRules()) {
			HashMap<Integer, HashMap<Integer, Integer>> valuesToCheckPerTable;
			if (column.getChildTable().equals(tableName) && column.getType().equals(PocFastDataConstants.PARENT)) {
				valuesToCheckPerTable = valuesToCheck.get(tableName);
				if (valuesToCheckPerTable == null) {
					valuesToCheckPerTable = new HashMap<>();
					listValues = new HashMap<>();
				} else {
					listValues = valuesToCheckPerTable.get(data.getPayload().get(column.getColumnPk()).asInt());
					if (listValues == null) {
						listValues = new HashMap<>();
					}
				}
				int value = data.getPayload().get(column.getColumnName()).asInt();
				listValues.put(column.getPosition(), value);
				valuesToCheckPerTable.put(data.getPayload().get(column.getColumnPk()).asInt(), listValues);
				valuesToCheck.put(tableName, valuesToCheckPerTable);
			} else if (column.getChildTable().equals(tableName)
					&& column.getType().equals(PocFastDataConstants.CHILD)) {
				valuesToCheckPerTable = valuesToCheck.get(column.getParentTable());
				if (valuesToCheckPerTable == null) {
					valuesToCheckPerTable = new HashMap<>();
					listValues = new HashMap<>();
				} else {
					listValues = valuesToCheckPerTable.get(data.getPayload().get(column.getColumnPk()).asInt());
					if (listValues == null) {
						listValues = new HashMap<>();
					}
				}
				int value = data.getPayload().get(column.getColumnName()).asInt();
				listValues.put(column.getPosition(), value);
				valuesToCheckPerTable.put(data.getPayload().get(column.getColumnPk()).asInt(), listValues);
				valuesToCheck.put(column.getParentTable(), valuesToCheckPerTable);
			}
		}

	}

	public List<EventToLaunch> isAllEventsReceived(String tableName, KeySchema keyEvent) {
		Map<String, Integer> mainEvents = new HashMap<>();
		if (tableName.equals(ruleConfigProperties.getMainTable())) {
			mainEvents = createEventsForMainEvent(tableName, keyEvent.getPayload().asInt());
		} else {
			mainEvents = createEventsForOtherEvents(tableName, keyEvent.getPayload().asInt());
		}
		if (!mainEvents.isEmpty()) {
			List<EventToLaunch> createdEvents = convertEventKeyToEventValue(mainEvents);
			return createdEvents;
		}
		return null;
	}

	private List<EventToLaunch> convertEventKeyToEventValue(Map<String, Integer> eventsMap) {
		List<EventToLaunch> result = new ArrayList<>();
		for (Entry<String, Integer> entry : eventsMap.entrySet()) {
			List<EventToLaunch> createdEvents = createEvents(entry.getKey(), entry.getValue());
			result.addAll(createdEvents);
		}
		return result;
	}

	private Map<String, Integer> createEventsForMainEvent(String tableName, Integer key) {
		HashMap<String, Integer> result = new HashMap<>();
		HashMap<Integer, HashMap<Integer, Integer>> valuesToCheckPerTable = valuesToCheck.get(tableName);
		if (valuesToCheckPerTable != null) {
			HashMap<Integer, Integer> listValues = valuesToCheckPerTable.get(key);
			boolean isAllEventsReceived = true;
			for (Rule column : ruleConfigProperties.getRules()) {
				if (listValues.get(column.getPosition()) == null) {
					isAllEventsReceived = false;
					break;
				}
			}
			if (isAllEventsReceived) {
				result.put(tableName, key);
			}
		}
		return result;
	}

	private Map<String, Integer> createEventsForOtherEvents(String tableName, Integer key) {
		HashMap<String, Integer> result = new HashMap<>();
		for (Entry<String, HashMap<Integer, HashMap<Integer, Integer>>> entry : valuesToCheck.entrySet()) {
			HashMap<Integer, HashMap<Integer, Integer>> valuesToCheckPerTable = valuesToCheck.get(entry.getKey());
			for (Entry<Integer, HashMap<Integer, Integer>> entry2 : valuesToCheckPerTable.entrySet()) {
				HashMap<Integer, Integer> mapValues = valuesToCheckPerTable.get(entry2.getKey());
				for (Rule column : ruleConfigProperties.getRules()) {
					if (((column.getType().equals(PocFastDataConstants.PARENT)
							&& column.getParentTable().equals(tableName))
							|| (column.getType().equals(PocFastDataConstants.CHILD)
									&& column.getChildTable().equals(tableName)))
							&& mapValues.get(column.getPosition()) != null
							&& mapValues.get(column.getPosition()).equals(key)) {
						boolean isAllEventsReceived = true;
						for (Rule column2 : ruleConfigProperties.getRules()) {
							if (mapValues.get(column2.getPosition()) == null) {
								isAllEventsReceived = false;
								break;
							}
						}
						if (isAllEventsReceived) {
							result.put(entry.getKey(), entry2.getKey());
						}
					}
				}
			}
		}
		return result;
	}

	private List<EventToLaunch> createEvents(String tableName, Integer key) {
		List<EventToLaunch> eventsToLaunch = new ArrayList<>();
		EventToLaunch event;
		for (Rule column : ruleConfigProperties.getRules()) {
			if (column.getType().equals(PocFastDataConstants.PARENT)) {
				// Map<Integer, KeyValue<KeySchema, ValueSchema>> events =
				// eventsMap.get(column.getChildTable());
				Map<Integer, KeyValue<KeySchema, ValueSchema>> events = eventsMap.get(column.getParentTable());
				if (events == null) {
					log.warn(String.format("Events for table %s don't exist", column.getParentTable()));
					eventsToLaunch = new ArrayList<>();
					return eventsToLaunch;
				}
				HashMap<Integer, HashMap<Integer, Integer>> valuesToCheckPerTable = valuesToCheck.get(tableName);
				Integer eventKey = valuesToCheckPerTable.get(key).get(column.getPosition());
				KeyValue<KeySchema, ValueSchema> keyvalue = events.get(eventKey);
				if (keyvalue == null) {
					log.warn(String.format("TableName: %s, key: %s is null for Parent Table: %s:, key: %s", tableName,
							key, column.getParentTable(), eventKey));
					eventsToLaunch = new ArrayList<>();
					return eventsToLaunch;
				}

				event = new EventToLaunch(column.getParentTable(), keyvalue);
				eventsToLaunch.add(event);

			}
		}

		Map<Integer, KeyValue<KeySchema, ValueSchema>> eventMain = eventsMap.get(ruleConfigProperties.getMainTable());
		KeyValue<KeySchema, ValueSchema> keyvalueMain = eventMain.get(key);
		event = new EventToLaunch(ruleConfigProperties.getMainTable(), keyvalueMain);
		eventsToLaunch.add(event);

		for (Rule column : ruleConfigProperties.getRules()) {
			if (column.getType().equals(PocFastDataConstants.CHILD)) {
				Map<Integer, KeyValue<KeySchema, ValueSchema>> events = eventsMap.get(column.getChildTable());
				if (events == null) {
					log.warn(String.format("Events for table %s don't exist", column.getChildTable()));
					eventsToLaunch = new ArrayList<>();
					return eventsToLaunch;
				}
				HashMap<Integer, HashMap<Integer, Integer>> valuesToCheckPerTable = valuesToCheck.get(tableName);
				Integer eventKey = valuesToCheckPerTable.get(key).get(column.getPosition());
				KeyValue<KeySchema, ValueSchema> keyvalue = events.get(eventKey);

				if (keyvalue == null) {
					log.warn(String.format("TableName: %s, key: %s is null for Child Table: %s:, key: %s", tableName,
							key, column.getChildTable(), eventKey));
					eventsToLaunch = new ArrayList<>();
					return eventsToLaunch;
				}

				event = new EventToLaunch(column.getChildTable(), keyvalue);
				eventsToLaunch.add(event);
			}
		}

		return eventsToLaunch;
	}

	public boolean removeData(String objectName, Integer key) {
		boolean removeEvents = false;
		Map<Integer, KeyValue<KeySchema, ValueSchema>> events = eventsMap.get(objectName);
		if (events != null) {
			events.remove(key);
			log.info(String.format("Removed event for table: %s , key: %s", objectName, key));
			removeEvents = true;
		}
		boolean removeValues = false;
		HashMap<Integer, HashMap<Integer, Integer>> valuesMap = valuesToCheck.get(objectName);
		if (valuesMap != null) {
			valuesMap.remove(key);
			log.info(String.format("Removed check data for table: %s , key: %s", objectName, key));
			removeValues = true;
		}
		return removeEvents && removeValues;
	}
}
