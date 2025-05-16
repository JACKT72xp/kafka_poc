package com.caixabank.absis.apps.dataservice.poc.fastdata.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key.KeySchema;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.ValueSchema;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProducerTraspasosService implements CommandLineRunner{
	
	private static final Logger log = LoggerFactory.getLogger(ProducerTraspasosService.class);
	
    @Autowired
    private KafkaTemplate<KeySchema, ValueSchema> kafka;

	@Value(value = "${output-topic.prefix}")
	private String outTopicPrefix;
	

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Starting producer...");
		producirMensajes();
	}

	private void producirMensajes(){
		while (true) {
			//System.out.println("Producing messages...");
			try {
				Random random = new Random();
				int randomInt = random.nextInt(100);
				String message = "Hello World: " + randomInt;
				System.out.println(message);
				send(message);
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				log.error("Error in producer", e);
			}
		}
	}

	private void send(String message) {
		//String topic = outTopicPrefix.replace("(.*)", "test-topic");
		String topic = "test-topic";
		Map<String, Object> map = new HashMap<>();
		map.put("Message", message);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.valueToTree(map);

		ValueSchema schema = new ValueSchema();
		schema.setPayload(node);
		ProducerRecord<KeySchema, ValueSchema> pr = new ProducerRecord<KeySchema, ValueSchema>(topic, schema);
		kafka.send(pr);	
	}


    
}
