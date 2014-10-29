/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ItemDataJsObject extends JavaScriptObject {

	protected ItemDataJsObject() {
    }

    public final native JsArray<QueryMetaDataJsObject> getMetaData() /*-{ return this.metadata }-*/;

    public final native JsArray<?> getResultSet() /*-{ return this.resultset }-*/;
}
