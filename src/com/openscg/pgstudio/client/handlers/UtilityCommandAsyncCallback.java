/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.handlers;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.panels.popups.ResultsPopUp;
import com.openscg.pgstudio.client.providers.ListProvider;

public class UtilityCommandAsyncCallback implements AsyncCallback<String> {

	private final DialogBox dialogBox;
	private final ListProvider dataProvider;
	private boolean autoRefresh = false;
	private boolean showResultOutput = false;
	
	public UtilityCommandAsyncCallback (DialogBox dialogBox, ListProvider dataProvider) {
		this.dialogBox = dialogBox;
		this.dataProvider = dataProvider;
	}
	
	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	public void setAutoRefresh(boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
	}

	public boolean isShowResultOutput() {
		return showResultOutput;
	}

	public void setShowResultOutput(boolean showResultOutput) {
		this.showResultOutput = showResultOutput;
	}

	@Override
	public void onFailure(Throwable caught) {
		ResultsPopUp pop = new ResultsPopUp(
				"Error", caught.getMessage());
		try {
			pop.getDialogBox();
		} catch (PopUpException ex) {
			Window.alert(ex.getMessage());
		}
		
		if (dialogBox != null)
			dialogBox.hide(true);
	}

	@Override
	public void onSuccess(String result) {
		JsArray<CommandJsObject> jArray = json2Messages(result);

		CommandJsObject res = jArray.get(0);
		if (res != null && res.getStatus() != null
				&& res.getStatus()
						.contains("ERROR")) {
			ResultsPopUp pop = new ResultsPopUp(
					"Error", res.getMessage());
			try {
				pop.getDialogBox();
			} catch (PopUpException caught) {
				Window.alert(caught.getMessage());
			}
		} else {
			if (showResultOutput) {
				ResultsPopUp pop = new ResultsPopUp("Results", res.getMessage());
				try {
					pop.getDialogBox();
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
			
			if (autoRefresh) {
				dataProvider.refresh();
				PgStudio.dtp.refreshCurrent();
			}
			
			if (dialogBox != null) {
				dialogBox.hide(true);
			}
		}
	}

	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{ 
	  	return eval(json); 
	}-*/;

}
