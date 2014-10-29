/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class TriggerInfo implements ModelInfo, Comparable<TriggerInfo> {

    public static final ProvidesKey<TriggerInfo> KEY_PROVIDER = new ProvidesKey<TriggerInfo>() {
      public Object getKey(TriggerInfo trigger) {
        return trigger == null ? null : trigger.getId();
      }
    };

    private final int id;
    private final int schema;
    private final String name;
    
    private boolean deferrable;
    private boolean init_deferrable;
    private String definition;
    
    
    public TriggerInfo(int schema, int id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(TriggerInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof TriggerInfo) {
        return id == ((TriggerInfo) o).id;
      }
      return false;
    }

    /**
     * @return the unique ID of the contact
     */
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

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public boolean isDeferrable() {
		return deferrable;
	}

	public void setDeferrable(boolean deferrable) {
		this.deferrable = deferrable;
	}

	public boolean isInitDeferrable() {
		return init_deferrable;
	}

	public void setInitDeferrable(boolean init_deferrable) {
		this.init_deferrable = init_deferrable;
	}

	@Override
	public String getFullName() {
		return name;
	}

	@Override
	public ITEM_TYPE getItemType() {
		return null;
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
