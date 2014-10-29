/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class SequencesJsObject extends JavaScriptObject {

	protected SequencesJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getName() /*-{ return this.name }-*/;
}
