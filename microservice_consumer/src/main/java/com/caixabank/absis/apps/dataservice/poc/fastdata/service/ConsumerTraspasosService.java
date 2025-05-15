package com.caixabank.absis.apps.dataservice.poc.fastdata.service;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key.KeySchema;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.ValueSchema;

@Component
public class ConsumerTraspasosService {

	private static final Logger log = LoggerFactory.getLogger(ConsumerTraspasosService.class);

	@Value(value = "${input-topic.prefix}")
	private String inputTopic;
	
	
	//@KafkaListener(id = "${spring.kafka.application-id}", topics = "#{@consumerConfigurationProperties.kafkaTopics}", containerFactory = "kafkaListenerContainerFactory")
	@KafkaListener(id = "${spring.kafka.application-id}", topics = "test-topic", containerFactory = "kafkaListenerContainerFactory")
	public void receive(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(required = false, name = KafkaHeaders.RECEIVED_MESSAGE_KEY) KeySchema key,
			@Payload(required = false) ValueSchema payload) throws IOException {
		log.info("{}", payload);

		
	}
	
	
	
	
	
}