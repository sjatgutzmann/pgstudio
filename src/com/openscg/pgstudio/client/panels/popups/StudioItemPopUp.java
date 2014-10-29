/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.providers.ItemListProvider;

public interface StudioItemPopUp extends StudioPopUp {
	public void setSelectionModel(SingleSelectionModel<ModelInfo> model);	

	public void setDataProvider(ItemListProvider provider);		
	
	public void setItem(ModelInfo item);
}
