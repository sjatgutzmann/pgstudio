package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class ActivityJsObject extends JavaScriptObject {

	protected ActivityJsObject() {
    }


    public final native String getDatName() /*-{ return this.datname}-*/;
    public final native String getState() /*-{ return this.state }-*/;
    public final native String getUserName() /*-{ return this.usename }-*/;    
    public final native String getPid() /*-{ return this.pid }-*/;
    public final native String getClientAddr() /*-{ return this.client_addr }-*/;
    public final native String getBackendStart() /*-{ return this.backend_start }-*/;
    public final native String getXactStart() /*-{ return this.xact_start }-*/;
    public final native String getQueryTime() /*-{ return this.query_time }-*/;
    public final native String getWaiting() /*-{ return this.waiting }-*/;
    public final native String getQuery() /*-{ return this.query }-*/;

}
