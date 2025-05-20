package com.caixabank.absis.apps.dataservice.poc.fastdata.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableConfigurationProperties(value = RuleConfigProperties.class)
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
	@Primary
	public RuleConfigProperties getRuleConfigProperties() {
		return new RuleConfigProperties();
	}
	
	@Bean
	public Map<String, Object>  consumerConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);
		return props;
	}
	
    @Bean
    public ConsumerFactory<KeySchema, ValueSchema> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new JsonDeserializer<>(KeySchema.class), new JsonDeserializer<>(ValueSchema.class));
    }
    
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<KeySchema, ValueSchema>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<KeySchema, ValueSchema> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
    
    @Bean
    public Map<String, Object> producerConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		// TODO: No se si el ACK hay que activarlo
		//props.put(ProducerConfig.ACKS_CONFIG, "1");
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