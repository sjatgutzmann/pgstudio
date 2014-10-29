/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class ListJsObject extends JavaScriptObject {

	protected ListJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getLanguageName() /*-{ return this.name }-*/;
}
