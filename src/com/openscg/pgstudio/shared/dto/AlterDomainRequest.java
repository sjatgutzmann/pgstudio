package com.openscg.pgstudio.shared.dto;

import java.io.Serializable;

public class AlterDomainRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7560652636309079517L;
	private String schema;
	private String name;
	private Boolean notNull;
	private String defaultValue;

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getNotNull() {
		return notNull;
	}

	public void setNotNull(Boolean notNull) {
		this.notNull = notNull;
	}

}
