/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class ViewInfo implements ModelInfo, Comparable<ViewInfo> {

    public static final ProvidesKey<ViewInfo> KEY_PROVIDER = new ProvidesKey<ViewInfo>() {
      public Object getKey(ViewInfo view) {
        return view == null ? null : view.getId();
      }
    };

    private final long id;
    private final int schema;
    private final String name;
    
    private boolean materialized;
    private String comment;
    
    public ViewInfo(int schema, long id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(ViewInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof ViewInfo) {
        return id == ((ViewInfo) o).id;
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

	@Override
	public String getFullName() {
		return getName();
	}

	@Override
	public ITEM_TYPE getItemType() {
		if (materialized)
			return ITEM_TYPE.MATERIALIZED_VIEW;
		
		return ITEM_TYPE.VIEW;
	}

	public boolean isMaterialized() {
		return materialized;
	}

	public void setMaterialized(boolean materialized) {
		this.materialized = materialized;
	}

	@Override
	public int getSchema() {
		return schema;
	}

  }
