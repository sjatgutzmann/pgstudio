/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.TypeInfo;
import com.openscg.pgstudio.client.providers.ModelListProvider;
import com.openscg.pgstudio.shared.dto.AlterDomainRequest;

public class AlterDomainPopUp implements StudioModelPopUp {
	
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();
    
	private Label name = new Label();
	private Label defaultLbl = new Label();
	private Label nullLabel = new Label();
	
	private CheckBox nullBox = new CheckBox();	
	
	private TextBox defaultTxtBx = new TextBox();
	
	private TypeInfo typeInfo;
	
	
	
	public AlterDomainPopUp() {
	}
	
	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (typeInfo == null)
			throw new PopUpException("TypeInfo is not set");

		dialogBox.setWidget(getPanel());
		
		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}

	@Override
	public void setSelectionModel(SingleSelectionModel model) {
		//this.selectionModel = model;
	}

	@Override
	public void setDataProvider(ModelListProvider provider) {
		//this.dataProvider = (TypeListDataProvider) provider;		
	}

	public void setSchema(DatabaseObjectInfo schema) {
		//this.schema = schema;
	}
	
	public void setTypeInfo(TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	private VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");
		
		VerticalPanel innerPanel = new VerticalPanel();
		innerPanel.setSpacing(10);
		
		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lbl.setText("Alter Domain");
		
		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblName.setText("Domain Name");
		
		name.setWidth("155px");
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(name);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);
		
		HorizontalPanel alterPanel = new HorizontalPanel();
		defaultLbl.setText("Default value");
		nullLabel.setText("Not Null");
		alterPanel.setSpacing(10);
		alterPanel.add(defaultLbl);
		alterPanel.add(defaultTxtBx);
		
		HorizontalPanel nullPanel = new HorizontalPanel();
		nullPanel.setSpacing(10);
		nullPanel.add(nullLabel);
		nullPanel.add(nullBox);
		
		innerPanel.add(lbl);
		innerPanel.add(namePanel);
		innerPanel.add(alterPanel);
		innerPanel.add(nullPanel);
		
		panel.add(innerPanel);
		panel.add(getButtonPanel());
		
		return panel;
	}

	private HorizontalPanel getButtonPanel() {
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setSpacing(10);
		Button alter = getAlterButton();
		Button cancel = getCancelButton();
		buttonsPanel.add(alter);
		buttonsPanel.add(cancel);
		return buttonsPanel;
	}
	
	private Button getAlterButton() {
		Button alter = new Button();
		alter.setText("Alter");
		alter.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent arg0) {
				AlterDomainRequest request = new AlterDomainRequest();
				request.setDefaultValue(defaultTxtBx.getValue());
				request.setName(typeInfo.getFullName());
				request.setNotNull(nullBox.getValue());
				
				try {
					PgStudio.studioService.alterDomain(PgStudio.getToken(), typeInfo.getSchema(), request, new AsyncCallback<String>(){
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
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return alter;
	}
	
	private Button getCancelButton() {
		Button cancel = new Button();
		cancel.setText("Cancel");
		cancel.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent arg0) {
				dialogBox.hide(true);
			};
		});
		return cancel;
	}
	
	public void setName(String name) {
		this.name.setText(name);
	}
	
	public void setNotNull(boolean notNull) {
		this.nullBox.setValue(notNull);
	}
	
	public void setDefaultValue(String value) {
		this.defaultTxtBx.setText(value);
	}
	
	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{
		return eval(json);
	}-*/;

}
