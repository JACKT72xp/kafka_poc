package com.caixabank.absis.apps.dataservice.poc.fastdata.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.caixabank.absis.apps.dataservice.poc.fastdata.config.RuleConfigProperties;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key.KeySchema;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.ValueSchema;

@Component
public class ProducerService {
	
	private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

	@Autowired
	private RuleConfigProperties ruleConfigProperties;
	
    @Autowired
    private KafkaTemplate<KeySchema, ValueSchema> kafka;

	public void send(String objectName, KeySchema key, ValueSchema payload) {
		String topic = ruleConfigProperties.getOutputTopicPrefix().replace("(.*)", objectName);
		ProducerRecord<KeySchema, ValueSchema> pr = new ProducerRecord<> (topic, key, payload);
		log.info("Send event to topic={}, key= {}, payload={}", topic, key, payload);
		kafka.send(pr);	
	}
}
