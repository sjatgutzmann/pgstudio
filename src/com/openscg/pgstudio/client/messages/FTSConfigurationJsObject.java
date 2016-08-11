package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class FTSConfigurationJsObject extends JavaScriptObject {

	protected FTSConfigurationJsObject() {
    }

    public final native String getId() /*-{ return this.id}-*/;

    public final native String getName() /*-{ return this.name }-*/;
    
    public final native String getComment() /*-{ return this.comment }-*/;
    
    public final native String getToken() /*-{ return this.token }-*/;
    
    public final native String getDictionaries() /*-{ return this.dictionaries }-*/;
}
