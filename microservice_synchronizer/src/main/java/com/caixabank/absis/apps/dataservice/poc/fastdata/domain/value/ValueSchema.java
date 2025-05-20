/* Copyright 2025 freecodeformat.com */
package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

/* Time: 2025-04-15 10:40:11 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValueSchema {

    private Schema schema;
    private JsonNode payload;
    
    public void setSchema(Schema schema) {
         this.schema = schema;
     }
     public Schema getSchema() {
         return schema;
     }
	public JsonNode getPayload() {
		return payload;
	}
	public void setPayload(JsonNode payload) {
		this.payload = payload;
	}
	@Override
	public String toString() {
		return String.format("ValueSchema [schema=%s, payload=%s]", schema, payload);
	}    
}