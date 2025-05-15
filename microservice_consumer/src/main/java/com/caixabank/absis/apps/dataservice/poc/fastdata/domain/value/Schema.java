/* Copyright 2025 freecodeformat.com */
package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schema {

    private String type;
    private List<Fields> fields;
    private boolean optional;
    private String name;
    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public void setFields(List<Fields> fields) {
         this.fields = fields;
     }
     public List<Fields> getFields() {
         return fields;
     }

    public void setOptional(boolean optional) {
         this.optional = optional;
     }
     public boolean getOptional() {
         return optional;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }
	@Override
	public String toString() {
		return String.format("Schema [type=%s, fields=%s, optional=%s, name=%s]", type, fields, optional, name);
	}

}