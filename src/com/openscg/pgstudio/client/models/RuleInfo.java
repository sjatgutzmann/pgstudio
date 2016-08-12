/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class RuleInfo implements ModelInfo,Comparable<RuleInfo> {

    public static final ProvidesKey<RuleInfo> KEY_PROVIDER = new ProvidesKey<RuleInfo>() {
      public Object getKey(RuleInfo rule) {
        return rule == null ? null : rule.getId();
      }
    };

    private final long id;
    private final int schema;
    private final String name;
    
    private String type;
    private boolean enabled;
    private boolean instead;
    private String definition;
    
    
    public RuleInfo(int schema, long id, String name) {
    	this.schema = schema;
        this.id = id;
        this.name = name;
    }

    public int compareTo(RuleInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof RuleInfo) {
        return id == ((RuleInfo) o).id;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInstead() {
		return instead;
	}

	public void setInstead(boolean instead) {
		this.instead = instead;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
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
