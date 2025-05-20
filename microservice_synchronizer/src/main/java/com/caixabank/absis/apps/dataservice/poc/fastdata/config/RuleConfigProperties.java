package com.caixabank.absis.apps.dataservice.poc.fastdata.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.caixabank.absis.apps.dataservice.poc.fastdata.constants.PocFastDataConstants;

@ConfigurationProperties(prefix = "rules-configuration")
@Component
public class RuleConfigProperties {

	private static final Logger log = LoggerFactory.getLogger(RuleConfigProperties.class);

	//@Value(value = "${input-topic-prefix}")
	private String inputTopicPrefix;
	
	//@Value(value = "${output-topic-prefix}")
	private String outputTopicPrefix;

	//@Value(value = "${main-table}")	
	private String mainTable;
	
    private List<Rule> rules;	
	
	/**
	 * @return the inputPrefix
	 */
	public String getInputTopicPrefix() {
		return inputTopicPrefix;
	}

	/**
	 * @param inputPrefix the inputPrefix to set
	 */
	public void setInputTopicPrefix(String inputTopicPrefix) {
		this.inputTopicPrefix = inputTopicPrefix;
	}

	/**
	 * @return the outputPrefix
	 */
	public String getOutputTopicPrefix() {
		return outputTopicPrefix;
	}

	/**
	 * @param outputPrefix the outputPrefix to set
	 */
	public void setOutputTopicPrefix(String outputTopicPrefix) {
		this.outputTopicPrefix = outputTopicPrefix;
	}

	public String getMainTable() {
		return mainTable;
	}

	public void setMainTable(String mainTable) {
		this.mainTable = mainTable;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public String[] getKafkaTopics() {
		List<String> topics = new ArrayList<>();
		for (Rule rule: rules) {
			if (rule.getType().equals(PocFastDataConstants.PARENT)) {
				topics.add(inputTopicPrefix.replace("(.*)", rule.getParentTable()));
			} else if (rule.getType().equals(PocFastDataConstants.CHILD)) {
				topics.add(inputTopicPrefix.replace("(.*)", rule.getChildTable()));
			} else {
				//TODO: Throws any exception
				log.error("Unable to create topic.");
			}
		}
		topics.add(inputTopicPrefix.replace("(.*)", getMainTable()));
		return topics.toArray(new String[0]);
	}

    public static class Rule  {
		private String name;
        private String childTable;
        private String parentTable;
        private String columnName;
        private String columnPk;
        private String type;
        private int position;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the childTable
		 */
		public String getChildTable() {
			return childTable;
		}
		/**
		 * @param childTable the childTable to set
		 */
		public void setChildTable(String childTable) {
			this.childTable = childTable;
		}
		/**
		 * @return the parentTable
		 */
		public String getParentTable() {
			return parentTable;
		}
		/**
		 * @param parentTable the parentTable to set
		 */
		public void setParentTable(String parentTable) {
			this.parentTable = parentTable;
		}
		/**
		 * @return the columnName
		 */
		public String getColumnName() {
			return columnName;
		}
		/**
		 * @param columnName the columnName to set
		 */
		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}
		/**
		 * @return the columnPk
		 */
		public String getColumnPk() {
			return columnPk;
		}
		/**
		 * @param columnPk the columnPk to set
		 */
		public void setColumnPk(String columnPk) {
			this.columnPk = columnPk;
		}
		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}
		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}
		/**
		 * @return the position
		 */
		public int getPosition() {
			return position;
		}
		/**
		 * @param position the position to set
		 */
		public void setPosition(int position) {
			this.position = position;
		}
		
		@Override
		public String toString() {
			return String.format(
					"Rule [name=%s, childTable=%s, parentTable=%s, columnName=%s, columnPk=%s, type=%s, position=%s]",
					name, childTable, parentTable, columnName, columnPk, type, position);
		}
    }


	@Override
	public String toString() {
		return String.format("RuleProperties [inputTopicPrefix=%s, outputTopicPrefix=%s, mainTable=%s, rules=%s]",
				inputTopicPrefix, outputTopicPrefix, mainTable, rules);
	}
    
}