/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class FunctionInfo implements ModelInfo, Comparable<FunctionInfo> {

    public static final ProvidesKey<FunctionInfo> KEY_PROVIDER = new ProvidesKey<FunctionInfo>() {
      public Object getKey(FunctionInfo func) {
        return func == null ? null : func.getId();
      }
    };

    private final int id;
    private final int schema;
    private final String name;
    
    private String identity;
    
    public FunctionInfo(int schema, int id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(FunctionInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof FunctionInfo) {
        return id == ((FunctionInfo) o).id;
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

	@Override
	public String getFullName() {
		return name + "(" + identity + ")";
	}

	@Override
	public ITEM_TYPE getItemType() {
		return ITEM_TYPE.FUNCTION;
	}

	@Override
	public String getComment() {
		return "";
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	@Override
	public int getSchema() {
	    return schema;
	}

  }
