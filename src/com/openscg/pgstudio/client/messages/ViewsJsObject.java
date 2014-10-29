/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class ViewsJsObject extends JavaScriptObject {

	protected ViewsJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getName() /*-{ return this.name }-*/;
    public final native String getIsMaterialized() /*-{ return this.is_materialized }-*/;
    public final native String getComment() /*-{ return this.comment }-*/;

}
