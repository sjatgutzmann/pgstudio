/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.messages.DictionaryTemplateJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.TypeInfo;
import com.openscg.pgstudio.client.providers.DictionaryListDataProvider;
import com.openscg.pgstudio.client.providers.ModelListProvider;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;

public class AddDictionaryPopUp implements StudioModelPopUp {
	
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);
	private static final String WIDTH = "155px";
	
	final DialogBox dialogBox = new DialogBox();

    private SingleSelectionModel<TypeInfo> selectionModel = null;
    private DictionaryListDataProvider dataProvider = null;
    
    private DatabaseObjectInfo schema = null;
    
	private TextBox dictName = new TextBox();
	private ListBox templates = new ListBox();
	private TextBox opts = new TextBox();
	private TextBox comments = new TextBox();
	
	private Label lbl = new Label("Add Dictionary");
	private Label dictNameLbl = new Label("Dictionary Name");
	private Label templatesLbl = new Label("Template");
	private Label optsLbl = new Label("Options and values");
	private Label commentsLbl = new Label("Comments");
	
	public AddDictionaryPopUp() {
	}
	
	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		if (schema == null )
			throw new PopUpException("Schema is not set");
		
		dialogBox.setWidget(getPanel());
		
		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}

	@Override
	public void setSelectionModel(SingleSelectionModel model) {
		this.selectionModel = model;
	}

	@Override
	public void setDataProvider(ModelListProvider provider) {
		this.dataProvider = (DictionaryListDataProvider) provider;
		
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
		addCommonStyleToLabels(templatesLbl);
		addCommonStyleToLabels(optsLbl);
		addCommonStyleToLabels(commentsLbl);
		
			
		dictName.setWidth(WIDTH);
		templates.setWidth(WIDTH);
		opts.setWidth(WIDTH);
		comments.setWidth(WIDTH);
		
		info.add(lbl);
		
		HorizontalPanel formPanel = new HorizontalPanel();
		formPanel.setSpacing(10);
		formPanel.add(dictNameLbl);
		formPanel.add(dictName);
		info.add(formPanel);
		
		templates.addItem("");
		try {
			PgStudio.studioService.fetchDictionaryTemplates(PgStudio.getToken(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					JsArray<DictionaryTemplateJsObject> array = json2Messages(result);
					for(int i = 0; i< array.length(); i++) {
						templates.addItem(array.get(i).getFullName());
					}
				}
				
				@Override
				public void onFailure(Throwable arg0) {
					
				}
			});
		} catch (DatabaseConnectionException | PostgreSQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		formPanel = new HorizontalPanel();
		formPanel.setSpacing(10);
		formPanel.add(templatesLbl);
		formPanel.add(templates);
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
		
		Button addButton = new Button("Add");
		Button cancelButton = new Button("Cancel");
		
		bar.add(addButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		addButton.addClickHandler(new ClickHandler() {
			
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
		dataProvider.setSchema(schema);
	}
	
	private void execute() {			
		try {
			validate();
		} catch (Exception e1) {
			ResultsPopUp pop = new ResultsPopUp("Error" , "Please enter the valid data");
			try {
				pop.getDialogBox();
				return;
			} catch (PopUpException e) {
				e.printStackTrace();
			}
		}
		try {
			UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
			ac.setAutoRefresh(true);
			ac.setShowResultOutput(false);
			PgStudio.studioService.addDictionary(PgStudio.getToken(), 
					schema.getId(), dictName.getText(), 
					templates.getItemText(templates.getSelectedIndex()), 
					opts.getText(), comments.getText(), ac);
		} catch (DatabaseConnectionException | PostgreSQLException e) {
			e.printStackTrace();
		}
	}
	
	private void validate() throws Exception {
		if(dictName.getText() == null || dictName.getText().isEmpty() ||
		   templates.getItemText(templates.getSelectedIndex()).length() == 0 ||
		   opts.getText() == null || opts.getText().isEmpty()) {
			throw new Exception("Validation error");
		}
	}
	
	
	private static final native JsArray<DictionaryTemplateJsObject> json2Messages(String json)
	/*-{ 
	  	return eval(json); 
	}-*/;
}
