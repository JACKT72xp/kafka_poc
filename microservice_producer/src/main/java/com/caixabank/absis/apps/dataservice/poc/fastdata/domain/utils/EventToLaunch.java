package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.utils;

import org.apache.kafka.streams.KeyValue;

import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key.KeySchema;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.ValueSchema;

public class EventToLaunch {
	
	String objectName;
	KeyValue<KeySchema, ValueSchema> keyValue;
	
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public KeyValue<KeySchema, ValueSchema> getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(KeyValue<KeySchema, ValueSchema> keyValue) {
		this.keyValue = keyValue;
	}

	public EventToLaunch(String objectName, KeyValue<KeySchema, ValueSchema> keyValue) {
		super();
		this.objectName = objectName;
		this.keyValue = keyValue;
	}
	
	@Override
	public String toString() {
		return String.format("EventsToLaunch [objectName=%s, key=%s, value=%s]", objectName, keyValue.key, keyValue.value);
	}
	
}
