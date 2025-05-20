/* Copyright 2025 freecodeformat.com */
package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value;

import com.fasterxml.jackson.annotation.JsonInclude;

/* Time: 2025-04-15 10:40:11 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fields {

    private String type;
    private boolean optional;
    private String name;
    private Integer version;
    private String field;
    private Parameters parameters;
    
    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public void setOptional(boolean optional) {
         this.optional = optional;
     }
     public boolean getOptional() {
         return optional;
     }

    public void setField(String field) {
         this.field = field;
     }
     public String getField() {
         return field;
     }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public Parameters getParameters() {
		return parameters;
	}
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return String.format("Fields [type=%s, optional=%s, name=%s, version=%s, field=%s, parameters=%s]", type, optional, name,
				version, field, parameters);
	}
}