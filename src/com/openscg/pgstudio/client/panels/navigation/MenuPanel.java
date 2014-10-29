/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.navigation;

import com.openscg.pgstudio.client.models.DatabaseObjectInfo;

public interface MenuPanel {

	public void setSchema(DatabaseObjectInfo schema);
	
	public void refresh();

	public Boolean selectFirst();
}
