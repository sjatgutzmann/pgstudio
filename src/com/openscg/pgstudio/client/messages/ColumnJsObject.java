/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.messages;

import com.google.gwt.core.client.JavaScriptObject;

public class ColumnJsObject extends JavaScriptObject {

	protected ColumnJsObject() {
    }


    public final native String getId() /*-{ return this.id}-*/;

    public final native String getColumnName() /*-{ return this.name }-*/;
    public final native String getDistributionKey() /*-{ return this.distribution_key }-*/;
    public final native String getPrimaryKey() /*-{ return this.primary_key }-*/;
    public final native String getDataType() /*-{ return this.data_type }-*/;
    public final native String getNullable() /*-{ return this.nullable }-*/;
    public final native String getDefaultValue() /*-{ return this.default_value }-*/;
    public final native String getComment() /*-{ return this.comment }-*/;

}
