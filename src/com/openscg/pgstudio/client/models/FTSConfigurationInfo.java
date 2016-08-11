package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class FTSConfigurationInfo implements ModelInfo,Comparable<FTSConfigurationInfo> {

	public static final ProvidesKey<FTSConfigurationInfo> KEY_PROVIDER = new ProvidesKey<FTSConfigurationInfo>() {
		public Object getKey(FTSConfigurationInfo func) {
			return func == null ? null : func.getConfigName();
		}
	};

	private int id;
	private int schema;
	private String name;
	private String comment;
	private String token;
	private String dictionaries;
	private String configName;
	private ITEM_TYPE itemType;
	

	public FTSConfigurationInfo () {

	}

	public FTSConfigurationInfo (String name) {
		//this.schema = schema;
		//this.id = id;
		this.name = name;
		this.configName = name;
	}
	public int getId() {
		return id;
	}
	public int getSchema() {
		return schema;
	}
	public String getName() {
		return name;
	}
	public String getComment() {
		return comment;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDictionaries() {
		return dictionaries;
	}

	public void setDictionaries(String dict) {
		this.dictionaries = dict;
	}

	public int compareTo(FTSConfigurationInfo o) {
		return (o == null || o.configName == null) ? -1
				: -o.configName.compareTo(configName);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FTSConfigurationInfo) {
			if (o instanceof ColumnInfo) {
				return id == (((FTSConfigurationInfo) o).id);
		      }
			
		}
		return false;
	}

	@Override
	public ITEM_TYPE getItemType() {
	
		return ITEM_TYPE.FULL_TEXT_SEARCH;
		
	}
	
	public void setItemType(ITEM_TYPE itemType) {
		this.itemType = itemType;
	}



	@Override
	public String getFullName() {
		return name;
	}

	@Override
	public int hashCode() {
		return configName.hashCode();
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	@Override
	public String toString() {
		return "FTSConfigurationInfo [id=" + id + ", schema=" + schema
				+ ", name=" + name + ", comment=" + comment + ", token="
				+ token + ", dictionaries=" + dictionaries + ", configName="
				+ configName + ", itemType=" + itemType + "]";
	}

	

}
