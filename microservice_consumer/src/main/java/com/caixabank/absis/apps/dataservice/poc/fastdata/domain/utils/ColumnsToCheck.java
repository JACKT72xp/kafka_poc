package com.caixabank.absis.apps.dataservice.poc.fastdata.domain.utils;

public enum ColumnsToCheck {
	EXPEDIENTES("expedientes", "id_expediente", 0), FONDOS_ORIGEN("fondos", "id_fondo_origen", 1), FONDOS_DESTINO("fondos", "id_fondo_destino", 2);

	private String tableName;
	private String columnName;
	private int position;
	
	private ColumnsToCheck(String tableName, String columnName, int position) {
		this.tableName = tableName;
		this.setColumnName(columnName);
		this.setPosition(position);
	}
	
	public String getTableName() {
		return tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
