/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class SequenceInfo implements ModelInfo, Comparable<SequenceInfo> {

    public static final ProvidesKey<SequenceInfo> KEY_PROVIDER = new ProvidesKey<SequenceInfo>() {
      public Object getKey(SequenceInfo seq) {
        return seq == null ? null : seq.getId();
      }
    };

    private final int id;
    private final int schema;
    private final String name;
        
    public SequenceInfo(int schema, int id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(SequenceInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof SequenceInfo) {
        return id == ((SequenceInfo) o).id;
      }
      return false;
    }

    @Override
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
		return getName();
	}

	@Override
	public ITEM_TYPE getItemType() {
		return ITEM_TYPE.SEQUENCE;
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
