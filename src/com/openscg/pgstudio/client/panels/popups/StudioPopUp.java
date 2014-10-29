/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.providers.ModelListProvider;

public interface StudioPopUp {
	public DialogBox getDialogBox() throws PopUpException;
}
