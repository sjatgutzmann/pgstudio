/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class TypeInfo implements ModelInfo, Comparable<TypeInfo> {

	public enum TYPE_KIND {
		COMPOSITE, DOMAIN, ENUM, RANGE, UNKNOWN
	}
	
    public static final ProvidesKey<TypeInfo> KEY_PROVIDER = new ProvidesKey<TypeInfo>() {
      public Object getKey(TypeInfo type) {
        return type == null ? null : type.getId();
      }
    };

    private final long id;
    private final int schema;
    private final String name;
    
    private TYPE_KIND kind;
    
    public TypeInfo(int schema, long id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(TypeInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof TypeInfo) {
        return id == ((TypeInfo) o).id;
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

	public void setKind(TYPE_KIND kind) {
		this.kind = kind;
	}

	public TYPE_KIND getKind() {
		return kind;
	}

	@Override
	public String getFullName() {
		return getName();
	}

	@Override
	public ITEM_TYPE getItemType() {
		return ITEM_TYPE.TYPE;
	}

	@Override
	public String getComment() {
		return "";
	}

	@Override
	public int getSchema() {
		return schema;
	}

  }
