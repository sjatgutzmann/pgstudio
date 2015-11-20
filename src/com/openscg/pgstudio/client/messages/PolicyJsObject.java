/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class PolicyJsObject extends JavaScriptObject {

	protected PolicyJsObject() {
	}

	public final native String getId() /*-{ return this.id}-*/;

	public final native String getName() /*-{ return this.name }-*/;

	public final native String getCommand() /*-{ return this.command }-*/;

	public final native String getRoles() /*-{ return this.roles }-*/;

	public final native String getUsing() /*-{ return this.using }-*/;

	public final native String getWithCheck() /*-{ return this.withcheck }-*/;

}
