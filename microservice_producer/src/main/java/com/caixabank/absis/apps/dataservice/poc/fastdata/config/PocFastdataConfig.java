package com.caixabank.absis.apps.dataservice.poc.fastdata.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key.KeySchema;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.ValueSchema;


@Configuration
@EnableKafka
@EntityScan("com.caixabank.absis.apps.dataservice.poc.fastdata.data.entity")
@ComponentScan("com.caixabank.absis.apps.dataservice.poc.fastdata.service")
public class PocFastdataConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Value(value = "${spring.kafka.application-id}")
	private String appId;

	@Value(value = "${spring.kafka.consumer.isolation-level}")
	private String isolationLevel;
	
	@Value(value = "${spring.kafka.consumer.group-id}")
	private String groupId;
	
	@Value(value = "${spring.json.value.default.type}")
	private String springJsonValueDefaultType;
    
    @Bean
    public Map<String, Object> producerConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}   
    
    @Bean
    public ProducerFactory<KeySchema, ValueSchema> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }
    
    @Bean
    public KafkaTemplate<KeySchema, ValueSchema> stringTemplate(ProducerFactory<KeySchema, ValueSchema> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}