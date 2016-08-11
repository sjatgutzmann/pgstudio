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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;

public class AlterDictionaryPopUp implements StudioPopUp {
	
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);
	private static final String WIDTH = "155px";
	
	final DialogBox dialogBox = new DialogBox();
       
    private DatabaseObjectInfo schema = null;
    
	private TextBox dictName = new TextBox();
	private TextBox opts = new TextBox();
	private TextBox comments = new TextBox();
	
	private Label lbl = new Label("Alter Dictionary");
	private Label dictNameLbl = new Label("Dictionary Name");
	private Label optsLbl = new Label("Options and values");
	private Label commentsLbl = new Label("Comments");
	
	private String oldName;
	public AlterDictionaryPopUp() {
		
	}
	
	@Override
	public DialogBox getDialogBox() throws PopUpException {
		
		dialogBox.setWidget(getPanel());
		
		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}
	
	public void setDictName(String name) {
		oldName = name; 
		dictName.setText(oldName);
	}
	
	public void setComments(String comments) {
		this.comments.setText(comments);
	}
	
	public void setOptions(String options) {
		this.opts.setText(options);
	}
	
	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}
	
	private VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);
		
		
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
				
		addCommonStyleToLabels(dictNameLbl);
		addCommonStyleToLabels(optsLbl);
		addCommonStyleToLabels(commentsLbl);
		
			
		dictName.setWidth(WIDTH);
		opts.setWidth(WIDTH);
		comments.setWidth(WIDTH);
		
		info.add(lbl);
		
		HorizontalPanel formPanel = new HorizontalPanel();
		formPanel.setSpacing(10);
		formPanel.add(dictNameLbl);
		formPanel.add(dictName);
		info.add(formPanel);
		
		formPanel = new HorizontalPanel();
		formPanel.setSpacing(10);
		formPanel.add(optsLbl);
		formPanel.add(opts);
		info.add(formPanel);
		
		formPanel = new HorizontalPanel();
		formPanel.setSpacing(10);
		formPanel.add(commentsLbl);
		formPanel.add(comments);
		info.add(formPanel);
		
		formPanel.setCellVerticalAlignment(dictNameLbl, HasVerticalAlignment.ALIGN_MIDDLE);		
		info.add(formPanel);		
		
		Widget buttonBar = getButtonPanel();
		
		panel.add(info);
		panel.add(buttonBar);
		panel.setCellHorizontalAlignment(buttonBar, HasHorizontalAlignment.ALIGN_CENTER);
		
		return panel;
	}
	
	private void addCommonStyleToLabels(Label lbl) {
		lbl.setStyleName("StudioPopup-Msg");
		lbl.setWidth(WIDTH);
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
	
	}
	private Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		
		Button alterButton = new Button("Alter");
		Button cancelButton = new Button("Cancel");
		
		bar.add(alterButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(alterButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		alterButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				execute();
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

	private void refresh() {
		//dataProvider.setSchema(schema);
	}
	
	private void execute() {			
		try {
			validate();
		} catch (Exception e1) {
			ResultsPopUp pop = new ResultsPopUp("Error" , "Please enter the valid data");
			try {
				pop.getDialogBox();
			} catch (PopUpException e) {
				e.printStackTrace();
			}
		}
		try {
			PgStudio.studioService.alterDictionary(PgStudio.getToken(), schema.getId(), oldName, dictName.getText(), opts.getText(), comments.getText(), 
					new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {		
							Window.alert(caught.getMessage());
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
							dialogBox.hide(true);
							PgStudio.msp.refreshCurrent();		
							PgStudio.dtp.refreshCurrent();
						
						}
				});
		} catch (Exception e) {
			e.printStackTrace();
			Window.alert("ERROR : " + e.getMessage());
		}
	}
	
	private void validate() throws Exception {
		if(dictName.getText() == null || dictName.getText().isEmpty()) {
			throw new Exception("Validation error");
		}
	}
	
	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{
		return eval(json);
	}-*/;
}
