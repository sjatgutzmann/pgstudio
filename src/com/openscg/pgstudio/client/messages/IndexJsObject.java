/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class IndexJsObject extends JavaScriptObject {

	protected IndexJsObject() {
    }


    public final native String getId() /*-{ return this.id}-*/;

    public final native String getName() /*-{ return this.name }-*/;
    public final native String getOwner() /*-{ return this.owner }-*/;
    public final native String getAccessMethod() /*-{ return this.access_method }-*/;
    public final native String getPrimaryKey() /*-{ return this.primary_key }-*/;
    public final native String getUnique() /*-{ return this.unique }-*/;
    public final native String getExclusion() /*-{ return this.exclusion }-*/;
    public final native String getPartial() /*-{ return this.partial }-*/;
    public final native String getDefinition() /*-{ return this.definition }-*/;

}
