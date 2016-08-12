/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.DATABASE_OBJECT_TYPE;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.messages.DataTypesJsObject;
import com.openscg.pgstudio.client.models.ColumnInfo;
import com.openscg.pgstudio.client.models.DataTypeInfo;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;
import com.openscg.pgstudio.shared.dto.AlterColumnRequest;

public class AlterColumnPopUp extends AddColumnPopUp implements StudioItemPopUp {

	private ColumnInfo columnInfo;
	
	public AlterColumnPopUp(ColumnInfo columnInfo, boolean hasParent) {
		this.columnInfo = columnInfo;
		this.hasParent = hasParent;		
	}

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (!hasParent) {
			if (selectionModel == null)
				throw new PopUpException("Selection Model is not set");

			if (dataProvider == null)
				throw new PopUpException("Data Provider is not set");

			if (item == null)
				throw new PopUpException("Table is not set");
		}
		
		dialogBox.setWidget(getPanel());
			setColumnName(columnInfo.getFullName());			
			setNotNull(columnInfo.isNullable());  
			setComments(columnInfo.getComment());
			setDefaultValue(columnInfo.getDefault());
		populateDataType();
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		return dialogBox;
	}

	@Override
	protected VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);
		
		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lbl.setText("Alter Column");

		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblName.setText("Column Name");

		columnName = new TextBox();
		columnName.setWidth("155px");
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(columnName);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		
		dataType = new ListBox();
		dataType.setWidth("200px");
		
		length = new TextBox();
		length.setWidth("35px");
		
		dataTypes = new ArrayList<DataTypeInfo>();

		dataType.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event)	{
				
				if (dataTypes.get(dataType.getSelectedIndex()).isHasLength()) 
					length.setEnabled(true);
				else
					length.setEnabled(false);
			}
		});

		Label lblLength = new Label();
		lblLength.setStyleName("StudioPopup-Msg");
		lblLength.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblLength.setText("Length");

		length.setEnabled(false);
		length.addKeyPressHandler(getLengthKeyPressHandler());

		HorizontalPanel lengthPanel = new HorizontalPanel();
		lengthPanel.add(lblLength);
		lengthPanel.add(length);
		lengthPanel.setWidth("120px");
		lengthPanel.setCellVerticalAlignment(lblLength, HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel dtPanel = new HorizontalPanel();
		dtPanel.add(dataType);
		dtPanel.add(lengthPanel);
		dtPanel.setCellVerticalAlignment(dataType, HasVerticalAlignment.ALIGN_MIDDLE);


		CaptionPanel dtCaptionPanel = new CaptionPanel("Data Type");
		dtCaptionPanel.setStyleName("StudioCaption");
		dtCaptionPanel.add(dtPanel);

		notNull = new CheckBox();
		notNull.setValue(false);
		
		Label lblNull = new Label();
		lblNull.setStyleName("StudioPopup-Msg");
		lblNull.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblNull.setText("Not Null");

		HorizontalPanel nullPanel = new HorizontalPanel();
		nullPanel.add(notNull);
		nullPanel.add(lblNull);
		
		Label lblDefaultValue = new Label();
		lblDefaultValue.setStyleName("StudioPopup-Msg");
		lblDefaultValue.setWidth("100px");
		lblDefaultValue.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblDefaultValue.setText("Default Value");

		defaultValue = new TextBox();
		defaultValue.setWidth("100px");

		HorizontalPanel defaultPanel = new HorizontalPanel();
		defaultPanel.add(lblDefaultValue);
		defaultPanel.add(defaultValue);
		defaultPanel.setCellVerticalAlignment(lblDefaultValue, HasVerticalAlignment.ALIGN_MIDDLE);
		defaultPanel.setCellHorizontalAlignment(defaultValue, HasHorizontalAlignment.ALIGN_RIGHT);

		HorizontalPanel detailPanel = new HorizontalPanel();
		detailPanel.setWidth("350px");
		detailPanel.add(nullPanel);
		detailPanel.add(defaultPanel);
		detailPanel.setCellHorizontalAlignment(nullPanel, HasHorizontalAlignment.ALIGN_CENTER);
		detailPanel.setCellVerticalAlignment(nullPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		detailPanel.setCellHorizontalAlignment(defaultPanel, HasHorizontalAlignment.ALIGN_CENTER);

		CaptionPanel dtCommentPanel = new CaptionPanel("Comment");
		dtCommentPanel.setStyleName("StudioCaption");
		comments = new TextArea();
		comments.setVisibleLines(2);
		comments.setWidth("300px");
		dtCommentPanel.add(comments);

		info.add(lbl);
		info.add(namePanel);
		info.add(dtCaptionPanel);
		info.add(detailPanel);
		info.add(dtCommentPanel);
		
		panel.add(info);
		panel.add(getButtonPanel());
		
		return panel;
	}
	
	protected Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		bar.setWidth("350px");
		
		Button alterButton = new Button("Alter");
		Button cancelButton = new Button("Cancel");
		
		bar.add(alterButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(alterButton, HasHorizontalAlignment.ALIGN_RIGHT);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_LEFT);

		alterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (hasParent) {
					if (columnName.getText() != null
							|| !columnName.getText().equals("")
							|| dataType.getValue(dataType.getSelectedIndex()) != null
							|| !dataType.getValue(dataType.getSelectedIndex())
									.equals("")) {

						UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
						ac.setAutoRefresh(true);
						ac.setShowResultOutput(true);
						
						AlterColumnRequest command = buildAlterCommand();
						try {				
							// Execute service							
							studioService.alterColumn(PgStudio.getToken(), item.getId(), command, ac);													
						} catch (DatabaseConnectionException | PostgreSQLException e) {
							Window.alert(e.getMessage());
						}
					
					} else {
						Window.alert("Name and Datatype are mandatory to create a column");
					}
				} else {
					dialogBox.hide(true);				
				}
			} 
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				columnName.setText("");
				refresh();
				dialogBox.hide(true);
			}
		});
		
		return bar.asWidget();
	}
	
	@Override
	protected void populateDataType() {
		studioService.getList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.DATA_TYPE, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				dataType.clear();
				dataTypes.clear();
				// Show the RPC error message to the user
				Window.alert(caught.getMessage());
			}

			public void onSuccess(String result) {
				dataType.clear();
				JsArray<DataTypesJsObject> types = DataTypeInfo.json2Messages(result);

				for (int i = 0; i < types.length(); i++) {
					DataTypeInfo info = msgToDataTypeInfo(types.get(i));
					dataType.addItem(info.getName());
					dataTypes.add(info);
					
					String columnDataType = extractDataTypeName(columnInfo.getDataType());
					if(columnDataType.equals(info.getName())) {
						dataType.setSelectedIndex(i);						
						if (info.isHasLength()) {
	            			length.setEnabled(true);
	            			columnInfo.setLength(extractLength(columnInfo.getDataType()));
	            			length.setValue(columnInfo.getLength());	            			
						}
	            		else {	            			
	            			length.setEnabled(false);
	            		}
					}					
				}
			}
		});

	}
		
	private String extractDataTypeName(String dataType) {
		if (dataType != null && !dataType.isEmpty() && dataType.contains("(")) {
			return dataType.substring(0, dataType.indexOf("("));			
		}
		return dataType;
	}
	
	private String extractLength(String dataType) {
		if(dataType != null && !dataType.isEmpty())
		{
			return dataType.substring(dataType.indexOf("(") + 1, dataType.indexOf(")"));
		}
		return null;
	}

	private AlterColumnRequest buildAlterCommand() {
		AlterColumnRequest command = new AlterColumnRequest();
		command.setColumnName(columnInfo.getFullName());
		
		if(columnInfo.getComment() != null && comments.getValue() != null && !columnInfo.getComment().equalsIgnoreCase(comments.getValue())) {
			command.setComments(comments.getValue());
		}
		String selectedDataType = dataType.getItemText(dataType.getSelectedIndex());
		
		if(!columnInfo.getDataType().equals(selectedDataType)) {
			command.setDataType(selectedDataType);
		}
		
		String oldLength = columnInfo.getLength();
		String newLength = length.getValue();
		
		// Both old and new values available
		if(oldLength != null && newLength != null && !oldLength.equals(newLength)) {
			command.setDataType(selectedDataType);
			command.setLength(length.getValue());
		}
		// Only new value available
		else if(newLength != null && !newLength.equals(oldLength)) {
			command.setDataType(selectedDataType);
			command.setLength(newLength);
		}
				
		// Both old and new values available
		if(columnInfo.getDefault() != null && defaultValue.getValue() != null && !columnInfo.getDefault().equals(defaultValue.getValue())) {
			command.setDefaultValue(defaultValue.getValue());
		}
		// Only new value available
		else if(defaultValue.getValue() != null && !defaultValue.getValue().equals(columnInfo.getDefault())) {
			command.setDefaultValue(defaultValue.getValue());
		}
		
		
		if(columnName.getValue() != null && !columnInfo.getFullName().equalsIgnoreCase(columnName.getValue())) {
			command.setNewColumnName(columnName.getValue());			
		}
		
		boolean nullable = columnInfo.isNullable();
		if(nullable != notNull.getValue() ) {		
			// Setting the reverse value as we need "is not null" data, and not "is null"
			command.setNullable(!notNull.getValue()); 
		}
		return command;
	}

}
