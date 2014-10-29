/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class TriggersJsObject extends JavaScriptObject {

	protected TriggersJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getTriggerName() /*-{ return this.name }-*/;
    public final native String getDeferrable() /*-{ return this.deferrable }-*/;
    public final native String getInitDeferrable() /*-{ return this.init_deferrable }-*/;
    public final native String getDefinition() /*-{ return this.definition }-*/;

}
