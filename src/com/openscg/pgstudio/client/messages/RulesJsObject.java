/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class RulesJsObject extends JavaScriptObject {

	protected RulesJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getRuleName() /*-{ return this.name }-*/;
    public final native String getRuleType() /*-{ return this.type }-*/;
    public final native String getEnabled() /*-{ return this.enabled }-*/;
    public final native String getInstead() /*-{ return this.instead }-*/;
    public final native String getDefinition() /*-{ return this.definition }-*/;

}
