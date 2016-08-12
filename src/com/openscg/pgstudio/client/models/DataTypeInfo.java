/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.messages.DataTypesJsObject;

public class DataTypeInfo implements ModelInfo, Comparable<DataTypeInfo> {

    public static final ProvidesKey<DataTypeInfo> KEY_PROVIDER = new ProvidesKey<DataTypeInfo>() {
      public Object getKey(DataTypeInfo type) {
        return type == null ? null : type.getId();
      }
    };


    private final long id;
    private final String name;
    
    private boolean hasLength;
    private int usageCount;
    
    public DataTypeInfo(int schema, long id, String name) {
        this.id = id;
        this.name = name;
    }

    public int compareTo(DataTypeInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof DataTypeInfo) {
        return id == ((DataTypeInfo) o).id;
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

	@Override
	public String getFullName() {
		return getName();
	}

	@Override
	public ITEM_TYPE getItemType() {
		return ITEM_TYPE.TYPE;
	}

	public boolean isHasLength() {
		return hasLength;
	}

	public void setHasLength(boolean hasLength) {
		this.hasLength = hasLength;
	}

	public int getUsageCount() {
		return usageCount;
	}

	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}

	public static final DataTypeInfo msgToInfo(DataTypesJsObject msg) {
		
		DataTypeInfo type = new DataTypeInfo(0, Integer.parseInt(msg.getId()), msg.getTypeName());

		type.setUsageCount(Integer.parseInt(msg.getUsageCount()));
		
		if (msg.getHasLength().equalsIgnoreCase("true")) {
			type.setHasLength(true);
		} else {
			type.setHasLength(false);
		}
		return type;
	}

	public static final native JsArray<DataTypesJsObject> json2Messages(
			String json)
	/*-{ 
	  	return eval(json); 
	}-*/;

	@Override
	public String getComment() {
		return "";
	}

	@Override
	public int getSchema() {
		return 0;
	}

	@Override
	public String toString() {
		return "DataTypeInfo [name=" + name + "]";
	}

  }
