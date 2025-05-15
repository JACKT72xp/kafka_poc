package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.key;

public class Schema {
    private String type;

    private boolean optional;

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setOptional(boolean optional){
        this.optional = optional;
    }
    public boolean getOptional(){
        return this.optional;
    }
	@Override
	public String toString() {
		return String.format("Schema [type=%s, optional=%s]", type, optional);
	}
}
