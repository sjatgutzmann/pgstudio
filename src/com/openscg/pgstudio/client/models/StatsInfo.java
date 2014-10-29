/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;

public class StatsInfo implements Comparable<StatsInfo> {

    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<StatsInfo> KEY_PROVIDER = new ProvidesKey<StatsInfo>() {
      public Object getKey(StatsInfo stat) {
        return stat == null ? null : stat.getId();
      }
    };

    private static int nextId = 0;

    private final int id;
    
    private final String name;
    private final String value;
    
    public StatsInfo(String name, String value) {
      this.id = nextId;
      nextId++;
      this.name = name;
      this.value = value;
    }

    public int compareTo(StatsInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof StatsInfo) {
        return id == ((StatsInfo) o).id;
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

	public String getValue() {
		return value;
	}

  }
