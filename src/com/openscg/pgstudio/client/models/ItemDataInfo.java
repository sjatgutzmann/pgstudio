/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import java.util.ArrayList;

import com.google.gwt.view.client.ProvidesKey;

public class ItemDataInfo implements Comparable<ItemDataInfo> {

    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<ItemDataInfo> KEY_PROVIDER = new ProvidesKey<ItemDataInfo>() {
      public Integer getKey(ItemDataInfo item) {
        return item == null ? null : item.getId();
      }
    };

    private static int nextId = 0;

    private final int id;
        
    private ArrayList<String> items;
    
    public ItemDataInfo(int columnCount) {
      this.id = nextId;
      nextId++;
      
      items = new ArrayList<String>(columnCount);      
    }

    public int compareTo(ItemDataInfo o) {
      return (o == null || o.getColumnValue(0) == null) ? -1
          : 0;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof ItemDataInfo) {
        return id == ((ItemDataInfo) o).id;
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

    public void setValue(int columnIndex, String value) {
    	items.add(columnIndex, value);
    }
    
    public String getColumnValue(int columnIndex) {
    	return items.get(columnIndex);
    }
  }
