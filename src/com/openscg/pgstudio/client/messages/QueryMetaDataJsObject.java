/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class QueryMetaDataJsObject extends JavaScriptObject {

	protected QueryMetaDataJsObject() {
    }

    public final native String getName() /*-{ return this.name }-*/;

    public final native int getDataType() /*-{ return this.data_type }-*/;
}
