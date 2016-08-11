/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class ParserJsObject extends JavaScriptObject {

	protected ParserJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;
    public final native String getSchema() /*-{ return this.schema}-*/;
    public final native String getName() /*-{ return this.name }-*/;
    public final native String getComment() /*-{ return this.comment }-*/;
}
