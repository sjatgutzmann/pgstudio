/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.handlers;

import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.models.ModelInfo;

public class SelectionChangeHandler implements SelectionChangeEvent.Handler {

	private final PgStudio main;

	public SelectionChangeHandler(PgStudio main) {
    	this.main = main;
    }
	
    public void onSelectionChange(SelectionChangeEvent event) {
    	PgStudio.showWaitCursor();    	
    	Object sender = event.getSource();
    	
    	if (sender instanceof SingleSelectionModel) {
    		SingleSelectionModel<?> selectionModel = (SingleSelectionModel<?>) sender;
        	ModelInfo selected = (ModelInfo) selectionModel.getSelectedObject();
  	    	main.setSelectedItem(selected);            

    	}
  }
}
