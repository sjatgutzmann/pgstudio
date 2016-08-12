package com.openscg.pgstudio.shared.dto;

import java.io.Serializable;

public class AlterColumnRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4646410351606303547L;
	private String columnName;
	private String newColumnName;
	private String dataType;
	private Boolean nullable;
	private String length;
	private String comments;
	private String defaultValue;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getNewColumnName() {
		return newColumnName;
	}

	public void setNewColumnName(String newColumnName) {
		this.newColumnName = newColumnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "AlterCommand [columnName=" + columnName + ", newColumnName=" + newColumnName + ", dataType=" + dataType
				+ ", nullable=" + nullable + ", length=" + length + ", comments=" + comments + ", defaultValue="
				+ defaultValue + "]";
	}

}
