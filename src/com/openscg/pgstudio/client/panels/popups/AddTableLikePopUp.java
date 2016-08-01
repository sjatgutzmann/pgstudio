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
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.messages.TablesJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.TableInfo;
import com.openscg.pgstudio.client.providers.ModelListProvider;
import com.openscg.pgstudio.client.providers.TableListDataProvider;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;

public class AddTableLikePopUp implements StudioModelPopUp {
	
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();
	
    private SingleSelectionModel<TableInfo> selectionModel = null;
    
    private TableListDataProvider dataProvider = null;
    
    private DatabaseObjectInfo schema = null;
    
	private TextBox tableName = new TextBox();
	    
    private ListBox tableList;
    
    private CheckBox defaultCheckbox;
    
    private CheckBox constraintCheckbox;
    
    private CheckBox indexCheckbox;
	
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
		this.dataProvider = (TableListDataProvider) provider;
		
	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}
	
	private VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);
		
		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lbl.setText("Add Table");
		
		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblName.setWidth("150px");
		lblName.setText("Table Name");

		tableName = new TextBox();
		tableName.setWidth("190px");
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.add(lblName);
		namePanel.add(tableName);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		tableList = new ListBox();
		tableList.setWidth("200px");
		
		if (schema != null) {
			studioService.getList(PgStudio.getToken(), schema.getId(),
					ITEM_TYPE.TABLE, new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							tableList.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							tableList.clear();

							JsArray<TablesJsObject> tables = json2Messages(result);

							if (tables != null) {
								tableList.clear();
								for (int i = 0; i < tables.length(); i++) {
									TablesJsObject table = tables.get(i);
									tableList.addItem(schema.getName()+"."+table.getName());
								}
							}
						}
					});
		}
		
		
		Label lblSrcName = new Label();
		lblSrcName.setStyleName("StudioPopup-Msg");
		lblSrcName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblSrcName.setWidth("150px");
		lblSrcName.setText("Source Table");
		
		HorizontalPanel tlPanel = new HorizontalPanel();
		tlPanel.add(lblSrcName);
		tlPanel.add(tableList);
		tlPanel.setCellVerticalAlignment(tableList, HasVerticalAlignment.ALIGN_MIDDLE);
		
		SimplePanel defaultBoxPanel = new SimplePanel();
		defaultBoxPanel.setStyleName("roundedCheck");
		
		defaultCheckbox = new CheckBox();
		defaultBoxPanel.add(defaultCheckbox);

		Label lbldefault = new Label();
		lbldefault.setStyleName("StudioPopup-Msg");
		lbldefault.setText("INCLUDE DEFAULTS");
		
		SimplePanel constraintBoxPanel = new SimplePanel();
		constraintBoxPanel.setStyleName("roundedCheck");
		
		constraintCheckbox = new CheckBox();
		constraintBoxPanel.add(constraintCheckbox);

		Label lblconstraint = new Label();
		lblconstraint.setStyleName("StudioPopup-Msg");
		lblconstraint.setText("INCLUDE CONSTRAINTS");
		
		SimplePanel indexBoxPanel = new SimplePanel();
		indexBoxPanel.setStyleName("roundedCheck");
		
		indexCheckbox = new CheckBox();
		indexBoxPanel.add(indexCheckbox);

		Label lblIndex = new Label();
		lblIndex.setStyleName("StudioPopup-Msg");
		lblIndex.setText("INCLUDE INDEXES");
		
		HorizontalPanel boxDPanel = new HorizontalPanel();
		boxDPanel.setSpacing(5);
		
		HorizontalPanel boxCPanel = new HorizontalPanel();
		boxCPanel.setSpacing(5);
		
		HorizontalPanel boxIPanel = new HorizontalPanel();
		boxIPanel.setSpacing(5);
		
		VerticalPanel boxPanel = new VerticalPanel();
		//boxPanel.setSpacing(5);
		
		boxDPanel.add(defaultBoxPanel);
		boxDPanel.add(lbldefault);
		boxDPanel.setCellVerticalAlignment(defaultBoxPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		boxDPanel.setCellVerticalAlignment(lbldefault, HasVerticalAlignment.ALIGN_MIDDLE);
		
		boxCPanel.add(constraintBoxPanel);
		boxCPanel.add(lblconstraint);
		boxCPanel.setCellVerticalAlignment(constraintBoxPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		boxCPanel.setCellVerticalAlignment(lblconstraint, HasVerticalAlignment.ALIGN_MIDDLE);
		
		boxIPanel.add(indexBoxPanel);
		boxIPanel.add(lblIndex);
		boxIPanel.setCellVerticalAlignment(indexBoxPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		boxIPanel.setCellVerticalAlignment(lblIndex, HasVerticalAlignment.ALIGN_MIDDLE);
		
		boxPanel.add(boxDPanel);
		boxPanel.add(boxCPanel);
		boxPanel.add(boxIPanel);
		
		CaptionPanel dtCaptionPanel = new CaptionPanel("Options");
		dtCaptionPanel.setStyleName("StudioCaption");
		dtCaptionPanel.add(boxPanel);
		
		info.add(lbl);
		info.add(namePanel);
		info.add(tlPanel);
		info.add(dtCaptionPanel);
		
		panel.add(info);
		
		Widget buttonBar = getButtonPanel(); 
		panel.add(buttonBar);
		panel.setCellHorizontalAlignment(buttonBar, HasHorizontalAlignment.ALIGN_CENTER);
		
		return panel;
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
				if (tableName.getText() != null
						&& !tableName.getText().equals("")) {
					
					String sourceTable = tableList.getValue(tableList.getSelectedIndex());
					
					UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(
							dialogBox, dataProvider);
					ac.setAutoRefresh(true);
					ac.setShowResultOutput(false);
					
					try {
						studioService.createTableLike(PgStudio.getToken(), schema.getId(), tableName.getText(), sourceTable,
								defaultCheckbox.getValue(), constraintCheckbox.getValue(), indexCheckbox.getValue(), ac);
					} catch (DatabaseConnectionException | PostgreSQLException e) {
						Window.alert(e.getMessage());
					}
				} else {
					Window.alert("Name is mandatory to create a table");
				}
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
	
	private static final native JsArray<TablesJsObject> json2Messages(
			String json)
	/*-{ 
	  	return eval(json); 
	}-*/;
}
