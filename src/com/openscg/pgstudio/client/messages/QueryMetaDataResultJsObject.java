/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class QueryMetaDataResultJsObject extends JavaScriptObject {

	protected QueryMetaDataResultJsObject() {
    }

    public final native JsArray<QueryMetaDataJsObject> getMetaData() /*-{ return this.metadata }-*/;

    public final native JsArray<QueryErrorJsObject> getError() /*-{ return this.error }-*/;
    
    public final native String getQueryType() /*-{ return this.query_type }-*/;

}
