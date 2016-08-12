/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class TableInfo implements ModelInfo, Comparable<TableInfo> {

    public static final ProvidesKey<TableInfo> KEY_PROVIDER = new ProvidesKey<TableInfo>() {
      public Object getKey(TableInfo view) {
        return view == null ? null : view.getId();
      }
    };

    public enum TABLE_TYPE {
    	HASH_PARTITIONED, REPLICATED, UNKNOWN
    }
    
    private final long id;
    private final int schema;
    private final String name;
    
    private String comment;
    private TABLE_TYPE type;

    private boolean hasRowSecurity;
    private boolean forceRowSecurity;
    
    public TableInfo(int schema, long id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(TableInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof TableInfo) {
        return id == ((TableInfo) o).id;
      }
      return false;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setType(TABLE_TYPE type) {
		this.type = type;
	}

	public TABLE_TYPE getType() {
		return type;
	}

	public boolean hasRowSecurity() {
		return hasRowSecurity;
	}

	public void setHasRowSecurity(boolean hasRowSecurity) {
		this.hasRowSecurity = hasRowSecurity;
	}

	public boolean forceRowSecurity() {
		return forceRowSecurity;
	}

	public void setForceRowSecurity(boolean forceRowSecurity) {
		this.forceRowSecurity = forceRowSecurity;
	}

	@Override
	public String getFullName() {
		return getName();
	}

	@Override
	public ITEM_TYPE getItemType() {
		return ITEM_TYPE.TABLE;
	}

	@Override
	public int getSchema() {
        return schema;
	}

  }
