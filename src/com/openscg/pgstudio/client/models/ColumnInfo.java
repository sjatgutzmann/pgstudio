/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class ColumnInfo implements ModelInfo,Comparable<ColumnInfo> {

    public static final ProvidesKey<ColumnInfo> KEY_PROVIDER = new ProvidesKey<ColumnInfo>() {
      public Object getKey(ColumnInfo COL) {
        return COL == null ? null : COL.getId();
      }
    };

    private final long id;
    private final int schema;
    private final String name;
    
    private String dataType;
    private String comment;
    private String Default;
    private boolean distributionKey;
    private boolean primaryKey;
    private boolean nullable;
    private String length;
    
    public ColumnInfo(int schema, long id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(ColumnInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof ColumnInfo) {
        return id == ((ColumnInfo) o).id;
      }
      return false;
    }

    public int getSchema() {
    	return schema;
    }
    
    public long getId() {
      return this.id;
    }

    @Override
    public int hashCode() {
      return (int) id;
    }

	public String getName() {
		return name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDefault() {
		return Default;
	}

	public void setDefault(String default1) {
		Default = default1;
	}

	public boolean isDistributionKey() {
		return distributionKey;
	}

	public void setDistributionKey(boolean distributionKey) {
		this.distributionKey = distributionKey;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public String getFullName() {
		return name;
	}

	@Override
	public ITEM_TYPE getItemType() {
		return null;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}
	
	

  }
