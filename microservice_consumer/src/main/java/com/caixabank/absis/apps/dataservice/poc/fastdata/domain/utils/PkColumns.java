package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.utils;

public enum PkColumns {
	EXPEDIENTES("expedientes", "id"), FONDOS("fondos", "id");
	

	private String tableName;
	private String pkColumn;
	
	PkColumns(String string, String string2) {
		this.tableName = tableName;
		this.pkColumn = pkColumn;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPkColumn() {
		return pkColumn;
	}

	public void setPkColumn(String pkColumn) {
		this.pkColumn = pkColumn;
	}
}
