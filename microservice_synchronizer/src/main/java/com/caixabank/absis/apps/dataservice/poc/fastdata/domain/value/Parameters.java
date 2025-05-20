package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Parameters {

    private String scale;
    private String connectDecimalPrecision;
    
    public void setScale(String scale) {
         this.scale = scale;
     }
    
     public String getScale() {
         return scale;
     }

    public void setConnectDecimalPrecision(String connectDecimalPrecision) {
         this.connectDecimalPrecision = connectDecimalPrecision;
     }
    
    @JsonProperty("connect.decimal.precision")
     public String getConnectDecimalPrecision() {
         return connectDecimalPrecision;
     }
    
	@Override
	public String toString() {
		return String.format("Parameters [scale=%s, connectDecimalPrecision=%s]", scale, connectDecimalPrecision);
	}
}
