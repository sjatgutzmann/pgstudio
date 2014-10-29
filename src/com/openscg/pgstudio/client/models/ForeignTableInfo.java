/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class ForeignTableInfo implements ModelInfo, Comparable<ForeignTableInfo> {

    public static final ProvidesKey<ForeignTableInfo> KEY_PROVIDER = new ProvidesKey<ForeignTableInfo>() {
      public Object getKey(ForeignTableInfo view) {
        return view == null ? null : view.getId();
      }
    };

    private final int id;
    private final int schema;
    private final String name;
    
    private String type;
    private String comment;
    
    public ForeignTableInfo(int schema, int id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(ForeignTableInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof ForeignTableInfo) {
        return id == ((ForeignTableInfo) o).id;
      }
      return false;
    }

    public int getId() {
      return this.id;
    }

    @Override
    public int hashCode() {
      return id;
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

	@Override
	public String getFullName() {
		return getName();
	}

	@Override
	public ITEM_TYPE getItemType() {
		return ITEM_TYPE.FOREIGN_TABLE;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isPostgreSQL() {
		return type.equals("postgres_fdw");
	}
	
	public boolean isHadoop() {
		return type.equals("hive_fdw") || type.equals("hadoop_fdw");
	}

	@Override
	public int getSchema() {
		return schema;
	}
	
  }
