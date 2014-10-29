/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class StatsJsObject extends JavaScriptObject {

	protected StatsJsObject() {
    }

    public final native String getName() /*-{ return this.name }-*/;

    public final native String getValue() /*-{ return this.value }-*/;
}
