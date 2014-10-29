/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class TypesJsObject extends JavaScriptObject {

	protected TypesJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getName() /*-{ return this.name }-*/;
    public final native String getTypeKind() /*-{ return this.type_kind }-*/;
}
