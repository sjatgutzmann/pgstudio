/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import com.openscg.pgstudio.client.models.ModelInfo;

public interface DetailsPanel {

	public void setItem(ModelInfo selectedItem);
	
	public void refresh();
	
}
