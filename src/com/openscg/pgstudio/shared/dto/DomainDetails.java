package com.openscg.pgstudio.shared.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DomainDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3758622711326841259L;
	private String name;
	private String defaultValue;
	private Boolean notNull;
	private Map<String, String> constraintMap;

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

	public Map<String, String> getConstraintMap() {
		return constraintMap;
	}

	public void setConstraintMap(Map<String, String> constraintMap) {
		this.constraintMap = constraintMap;
	}

}
