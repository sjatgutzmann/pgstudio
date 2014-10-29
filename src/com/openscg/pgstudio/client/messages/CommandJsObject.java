/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class CommandJsObject extends JavaScriptObject {

	protected CommandJsObject() {
    }

    public final native String getStatus() /*-{ return this.status }-*/;
    public final native String getMessage() /*-{ return this.message }-*/;

}
