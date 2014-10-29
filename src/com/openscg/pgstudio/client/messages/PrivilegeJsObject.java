/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class PrivilegeJsObject extends JavaScriptObject {

	protected PrivilegeJsObject() {
    }


    public final native String getId() /*-{ return this.id}-*/;

    public final native String getGrantee() /*-{ return this.grantee }-*/;
    public final native String getType() /*-{ return this.type }-*/;
    public final native String getGrantable() /*-{ return this.grantable }-*/;
    public final native String getGrantor() /*-{ return this.grantor }-*/;

}
