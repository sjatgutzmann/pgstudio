package com.openscg.pgstudio.shared.dto;

import java.io.Serializable;
import java.util.Arrays;

public class AlterFunctionRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -245224440141972046L;
	private String schema;
	private String functionName;
	private String newFunctionName;

	private String functionBody;
	private String newFunctionBody;
	private String[] paramsList;
	private String language;
	private String returns;
	
	private String objectFilePath;

	public String getObjectFilePath() {
		return objectFilePath;
	}

	public void setObjectFilePath(String objectFilePath) {
		this.objectFilePath = objectFilePath;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getNewFunctionName() {
		return newFunctionName;
	}

	public void setNewFunctionName(String newFunctionName) {
		this.newFunctionName = newFunctionName;
	}

	public String getFunctionBody() {
		return functionBody;
	}

	public void setFunctionBody(String functionBody) {
		this.functionBody = functionBody;
	}

	public String getNewFunctionBody() {
		return newFunctionBody;
	}

	public void setNewFunctionBody(String newFunctionBody) {
		this.newFunctionBody = newFunctionBody;
	}

	public String[] getParamsList() {
		return paramsList;
	}

	public void setParamsList(String[] paramsList) {
		this.paramsList = paramsList;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getReturns() {
		return returns;
	}

	public void setReturns(String returns) {
		this.returns = returns;
	}

	@Override
	public String toString() {
		return "AlterFunctionRequest [schema=" + schema + ", functionName=" + functionName + ", newFunctionName="
				+ newFunctionName + ", functionBody=" + functionBody + ", newFunctionBody=" + newFunctionBody
				+ ", paramsList=" + Arrays.toString(paramsList) + ", language=" + language + ", returns=" + returns
				+ "]";
	}

}
