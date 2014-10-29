/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class TablesJsObject extends JavaScriptObject {

	protected TablesJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getName() /*-{ return this.name }-*/;
    public final native String getTableType() /*-{ return this.table_type }-*/;
    public final native String getComment() /*-{ return this.comment }-*/;

}
