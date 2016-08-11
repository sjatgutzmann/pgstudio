package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class DictionaryInfo implements ModelInfo, Comparable<DictionaryInfo> {

	private final int schema;
	private final String schemaName;
	private final int id;
    private final String name;
    private String comments;
    private String options;
   
    public static final ProvidesKey<DictionaryInfo> KEY_PROVIDER = new ProvidesKey<DictionaryInfo>() {
        public Object getKey(DictionaryInfo dictionary) {
          return dictionary == null ? null : dictionary.getId();
        }
      };
    public DictionaryInfo(int schema,String schemaName, int id, String name, String comments, String options) {
    	this.schema = schema;
    	this.schemaName = schemaName;
    	this.name = name;
    	this.id = id;
    	this.comments = comments;
    	this.options = options;
	}
    
	@Override
	public String getFullName() {
		return getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public ITEM_TYPE getItemType() {
		return ITEM_TYPE.DICTIONARY;
	}

	@Override
	public String getComment() {
		return comments;
	}

	@Override
	public int getSchema() {
		return schema;
	}

	public void setComment(String comments) {
		this.comments = comments;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	
	@Override
	public int compareTo(DictionaryInfo o) {
		 return (o == null || o.name == null) ? -1
		          : -o.name.compareTo(name);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DictionaryInfo) {
			return id == ((DictionaryInfo) o).id;
		}
		return false;
	}
	 
	@Override
	public String toString() {
		return "DictionaryInfo [schema=" + schema + ", schemaName=" + schemaName + ", id=" + id + ", name=" + name
				+ ", comments=" + comments + ", options=" + options + "]";
	}

	

}
