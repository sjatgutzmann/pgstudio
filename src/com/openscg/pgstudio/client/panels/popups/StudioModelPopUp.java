/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.providers.ModelListProvider;

public interface StudioModelPopUp extends StudioPopUp {
	public void setSelectionModel(SingleSelectionModel<ModelInfo> model);	

	public void setDataProvider(ModelListProvider provider);
	
	public void setSchema(DatabaseObjectInfo schema);
}
