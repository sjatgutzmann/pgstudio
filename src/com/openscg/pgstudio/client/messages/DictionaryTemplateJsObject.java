/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class DictionaryTemplateJsObject extends JavaScriptObject {

	protected DictionaryTemplateJsObject() {
    }

	public final native String getSchema() /*-{ return this.schema}-*/;
    public final native String getName() /*-{ return this.name }-*/;
    public final native String getComment() /*-{ return this.comment }-*/;
    public final native String getLexize() /*-{ return this.lexize }-*/;
    public final native String getInit() /*-{ return this.init }-*/;
    public final native String getFullName() /*-{ return this.fullName }-*/;
}
