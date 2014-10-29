/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.messages.ForeignServersJsObject;

public class ForeignServerInfo implements Comparable<ForeignServerInfo> {

    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<ForeignServerInfo> KEY_PROVIDER = new ProvidesKey<ForeignServerInfo>() {
      public Object getKey(ForeignServerInfo view) {
        return view == null ? null : view.getId();
      }
    };

    private static int nextId = 0;

    private final int id;
    
    private String name;
    
    public ForeignServerInfo(String name) {
      this.id = nextId;
      nextId++;
      setName(name);
    }

    public int compareTo(ForeignServerInfo o) {
      return (o == null || o.name == null) ? -1
          : -o.name.compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof ForeignServerInfo) {
        return id == ((ForeignServerInfo) o).id;
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

	public void setName(String name) {
		this.name = name;
	}
	
	public static ForeignServerInfo msgToInfo(ForeignServersJsObject msg) {
		
		ForeignServerInfo server = new ForeignServerInfo(msg.getName());

		return server;
	}

	public static final native JsArray<ForeignServersJsObject> json2Messages(
			String json)
	/*-{ 
	  	return eval(json); 
	}-*/;

  }
