package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key;

import com.fasterxml.jackson.databind.JsonNode;

public class KeySchema {
	private Schema schema;

	private JsonNode payload;

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Schema getSchema() {
		return this.schema;
	}

	public JsonNode getPayload() {
		return payload;
	}
	public void setPayload(JsonNode payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return String.format("KeySchema [schema=%s, payload=%s]", schema, payload);
	}    
	
}
