/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class DataTypesJsObject extends JavaScriptObject {

	protected DataTypesJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getTypeName() /*-{ return this.name }-*/;
    public final native String getHasLength() /*-{ return this.has_length }-*/;
    public final native String getUsageCount() /*-{ return this.usage_count }-*/;
}
