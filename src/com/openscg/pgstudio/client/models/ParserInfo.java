package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class ParserInfo implements ModelInfo {

	private final int schema;
	private final String schemaName;
	private final int id;
    private final String name;
    private String comments;
   
    public static final ProvidesKey<ParserInfo> KEY_PROVIDER = new ProvidesKey<ParserInfo>() {
        public Object getKey(ParserInfo parser) {
          return parser == null ? null : parser.getId();
        }
      };
    public ParserInfo(int schema,String schemaName, int id, String name, String comments) {
    	this.schema = schema;
    	this.schemaName = schemaName;
    	this.name = name;
    	this.id = id;
    	this.comments = comments;
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
		return ITEM_TYPE.PARSER;
	}

	@Override
	public String getComment() {
		return comments;
	}

	@Override
	public int getSchema() {
		return schema;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getSchemaName() {
		return schemaName;
	}

	@Override
	public String toString() {
		return "ParserInfo [schemaName=" + schemaName + ", id=" + id + ", name=" + name
				+ ", comments=" + comments + "]";
	}

}
