/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class FunctionsJsObject extends JavaScriptObject {

	protected FunctionsJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getName() /*-{ return this.name }-*/;
    
    public final native String getIdentity() /*-{ return this.ident }-*/;

}
