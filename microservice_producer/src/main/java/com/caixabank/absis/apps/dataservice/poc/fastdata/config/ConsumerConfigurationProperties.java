package com.caixabank.absis.apps.dataservice.poc.fastdata.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("input-topic")
@Component
public class ConsumerConfigurationProperties {
	private String prefix;
	private List<String> tables = new ArrayList<>();

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<String> getTables() {
		return this.tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

	public String[] getKafkaTopics() {
		List<String> topics = new ArrayList<>();
		for (String table : tables) {
			topics.add(prefix.replace("(.*)", table));
		}
		return topics.toArray(new String[0]);
	}

}
