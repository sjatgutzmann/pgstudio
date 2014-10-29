/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.messages.ListJsObject;

public class DatabaseObjectInfo implements Comparable<DatabaseObjectInfo> {

    public static final ProvidesKey<DatabaseObjectInfo> KEY_PROVIDER = new ProvidesKey<DatabaseObjectInfo>() {
      public Object getKey(DatabaseObjectInfo view) {
        return view == null ? null : view.getId();
      }
    };

    private final int id;
    
    private String name;
    
    public DatabaseObjectInfo(int id, String name) {
      this.id = id;
      setName(name);
    }

    public int compareTo(DatabaseObjectInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof DatabaseObjectInfo) {
        return id == ((DatabaseObjectInfo) o).id;
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

	public void setName(String name) {
		this.name = name;
	}
	
	public static DatabaseObjectInfo msgToInfo(ListJsObject msg) {
		int id = Integer.parseInt(msg.getId());

		DatabaseObjectInfo lang = new DatabaseObjectInfo(id, msg.getLanguageName());

		return lang;
	}

	public static final native JsArray<ListJsObject> json2Messages(
			String json)
	/*-{ 
	  	return eval(json); 
	}-*/;

  }
