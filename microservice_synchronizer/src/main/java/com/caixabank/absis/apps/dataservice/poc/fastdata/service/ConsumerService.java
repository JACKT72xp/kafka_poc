package com.caixabank.absis.apps.dataservice.poc.fastdata.service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.streams.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.caixabank.absis.apps.dataservice.poc.fastdata.component.Datasynchronization;
import com.caixabank.absis.apps.dataservice.poc.fastdata.config.RuleConfigProperties;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key.KeySchema;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.utils.EventToLaunch;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.ValueSchema;

@Component
public class ConsumerService {

	private static final Logger log = LoggerFactory.getLogger(ConsumerService.class);

	@Autowired
	private ProducerService producerOrdenesService;
	
	@Autowired
	private RuleConfigProperties ruleConfigProperties;
	
	@Autowired
	@Qualifier("datasynchronizationSingleton")
	private Datasynchronization dataSynchronization;
	
//	@KafkaListener(id = "${spring.kafka.application-id}", topics = "#{@consumerConfigurationProperties.kafkaTopics}", containerFactory = "kafkaListenerContainerFactory")
	@KafkaListener(id = "${spring.kafka.application-id}", topics = "#{@ruleConfigProperties.kafkaTopics}", containerFactory = "kafkaListenerContainerFactory")
	public void receive(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(required = false, name = KafkaHeaders.RECEIVED_MESSAGE_KEY) KeySchema key,
			@Payload(required = false) ValueSchema payload) throws IOException {
		log.info("Received event on topic={}, key= {}, payload={}", topic, key, payload);
		Pattern pattern = Pattern.compile(ruleConfigProperties.getInputTopicPrefix());
		Matcher matcher = pattern.matcher(topic);
		String objectName = "";
		if(matcher.find()) {
			objectName = matcher.group(1);
		}
		dataSynchronization.addEventfromTopic(objectName, key, payload);
		List<EventToLaunch> eventsToLaunch = dataSynchronization.isAllEventsReceived(objectName, key);
		if (eventsToLaunch != null) {
			for (EventToLaunch event: eventsToLaunch) {
				producerOrdenesService.send(event.getObjectName(), event.getKeyValue().key, event.getKeyValue().value);
				dataSynchronization.removeData(event.getObjectName(), event.getKeyValue().key.getPayload().intValue());
			}
		}
	}
}