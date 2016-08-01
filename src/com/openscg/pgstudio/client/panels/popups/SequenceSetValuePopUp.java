package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.providers.ModelListProvider;

public class SequenceSetValuePopUp implements StudioModelPopUp {
	
	final DialogBox dialogBox = new DialogBox();
	private Label label;
	private TextBox value;
	private String sequenceName;
	
	
	@Override
	public DialogBox getDialogBox() throws PopUpException {
		dialogBox.setWidget(getMainPanel());
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		return dialogBox;
	}

	private VerticalPanel getMainPanel() {
		VerticalPanel panel = new VerticalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		panel.setCellVerticalAlignment(hPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setStyleName("StudioPopup");
		
		label = new Label("Set Value");
		label.setStyleName("StudioPopup-Msg");
		label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	
		label.setWidth("75px");
		
		value = new TextBox();
		value.setWidth("100px");
		
		hPanel.add(label);
		hPanel.add(value);
		panel.add(hPanel);
		
		panel.add(getButtonPanel());
		return panel;
	}
	
	private Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		
		Button submitButton = new Button("Submit");
		Button cancelButton = new Button("Cancel");
		
		bar.add(submitButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(submitButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
					PgStudio.studioService.changeSequenceValue(PgStudio.getToken(), PgStudio.getSelectedSchema().getId(), sequenceName, value.getText(), new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							ResultsPopUp pop = new ResultsPopUp("Error", caught.getMessage());
							try {
								pop.getDialogBox();
							} catch (PopUpException ex) {
								Window.alert(ex.getMessage());
							}	
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
							} 
							dialogBox.hide();
							PgStudio.dtp.refreshCurrent();
						}
					});
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}
	@Override
	public void setSelectionModel(SingleSelectionModel<ModelInfo> model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataProvider(ModelListProvider provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSchema(DatabaseObjectInfo schema) {
		// TODO Auto-generated method stub
		
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{
		return eval(json);
	}-*/;
}
