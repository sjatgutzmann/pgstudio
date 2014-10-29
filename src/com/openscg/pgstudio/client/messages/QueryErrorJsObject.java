/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class QueryErrorJsObject extends JavaScriptObject {

	protected QueryErrorJsObject() {
    }

    public final native String getError() /*-{ return this.error }-*/;

    public final native int getErrorCode() /*-{ return this.error_code }-*/;
}
