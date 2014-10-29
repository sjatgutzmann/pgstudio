/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class ConstraintJsObject extends JavaScriptObject {

	protected ConstraintJsObject() {
    }


    public final native String getId() /*-{ return this.id}-*/;

    public final native String getConstraintName() /*-{ return this.name }-*/;
    public final native String getConstraintType() /*-{ return this.type }-*/;
    public final native String getDeferrable() /*-{ return this.deferrable }-*/;
    public final native String getDeferred() /*-{ return this.deferred }-*/;
    public final native String getIndexName() /*-{ return this.index_name }-*/;
    public final native String getIndexOwner() /*-{ return this.index_owner }-*/;    
    public final native String getUpdateAction() /*-{ return this.update_action }-*/;
    public final native String getDeleteAction() /*-{ return this.delete_action }-*/;    
    public final native String getDefinition() /*-{ return this.definition }-*/;

}
