/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import java.util.ArrayList;
import java.util.List;

import org.gwt.advanced.client.ui.widget.SimpleGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.messages.ColumnJsObject;
import com.openscg.pgstudio.client.messages.ItemDataJsObject;
import com.openscg.pgstudio.client.messages.QueryMetaDataJsObject;
import com.openscg.pgstudio.client.models.ItemDataInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.panels.popups.ImportDataPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.providers.ItemDataListDataProvider;
import com.openscg.pgstudio.client.widgets.ExportDataEventHandler;
import com.openscg.pgstudio.client.widgets.ExportWidget;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;

public class ItemDataPanel extends Composite implements DetailsPanel {

	private SimpleGrid dataGrid;
	
	//private DialogBox waitingBox;
	
	private ItemDataListDataProvider dataProvider = new ItemDataListDataProvider();

    private final SingleSelectionModel<ItemDataInfo> selectionModel = 
        	new SingleSelectionModel<ItemDataInfo>(ItemDataInfo.KEY_PROVIDER);

    
    private final static String WARNING_MSG = 
			"Are you sure you want to continue?";
    
    private ModelInfo item = null;

    final DialogBox createDialogBox = new DialogBox();
    
    final DialogBox dropDialogBox = new DialogBox();
    
    //JsArray<QueryMetaDataJsObject> colMetadata = null;
    
    JsArray<ColumnJsObject> colMetadata = null;
    
	private static int PIX_PER_CHAR = 10;
	private static String MAIN_HEIGHT = "450px";
	private static int PAGE_SIZE = 14;
	
	private int activeColCount = 0;
	
	private TextBox[] columnName;
	
	private CheckBox[] notNullArr;
	
	private Label[] lblValueArr;
	
	private Label[] lblTypeLabelArr;
	
	private List<String> lblColumnNameArr;
	
	PushButton importData;
	
	public void setItem(final ModelInfo item) {
		
		this.item = item;
		
		final int count = 25;
		dataGrid.clear(true);
		dataGrid.removeAllRows();
		
		if(item.getItemType() == ITEM_TYPE.TABLE) {
			importData.setEnabled(true);
			importData.setVisible(true);
		} else {
			importData.setEnabled(false);
			importData.setVisible(false);
		}
		
//		for (int i = 0; i < activeColCount; i++) {
//			dataGrid.removeHeaderWidget(0);
//		}

		final List<Integer> columnWidths = new ArrayList<Integer>();

		PgStudio.studioService.getItemObjectList(PgStudio.getToken(), item.getId(), item.getItemType(),
				ITEM_OBJECT_TYPE.COLUMN, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                // Show the RPC error message to the user
                Window.alert(caught.getMessage());
            }

            public void onSuccess(String result) {
            	/************************************************************/
                /*      Adjust the table for the Meta Data                  */            	
            	/************************************************************/

            	colMetadata = jsonMeta2Messages(result);

            	int numCols = 0;
            	if (colMetadata != null && colMetadata.length() > 0) {
            		numCols = colMetadata.length();
            		String name = null;
            		ColumnJsObject col = null;
					for (int i = 0; i < numCols; i++) {
						col = colMetadata.get(i);
						name = col.getColumnName();
	            		columnWidths.add(i, name.length());
	            		
	            		Label hdr = new Label(name);
	            		hdr.setStyleName("GPBYFDEIG GPBYFDEGG");
	            		
	            		hdr.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	            		dataGrid.setWidget(0, i, hdr.asWidget());
	            		
	            		dataGrid.setStyleName("StudioOverflow container");
					}
				}
            	
				activeColCount = numCols;


				/************************************************************/
                /*            Load the actual data                          */            	
            	/************************************************************/
        		PgStudio.studioService.getItemData(PgStudio.getToken(), item.getId(), item.getItemType(), count, new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {

                        // Show the RPC error message to the user
		                Window.alert(caught.getMessage());
                    }

                    public void onSuccess(String result) {
                    	int numCols = columnWidths.size();
                    			
            			JsArray<ItemDataJsObject> res = json2Messages(result);
            			
            			if (res.length() > 0) {
        	            	JSONValue results = JSONParser.parse(result);
        	            	JSONArray rows = results.isArray().get(0).isObject().get("resultset").isArray();
                	     
        	            	for (int i = 0; i < rows.size(); i++) {
        	            		JSONObject row = rows.get(i).isObject();
        	            		
        	            		for (int j = 1; j <= numCols; j++) {
        	            			String key = "c" + j;
        	            			String val = row.get(key).isString().stringValue();
        	            			
        	            			//dataGrid.setText(i, j-1, val);
        	            			
        	            			if (columnWidths.get(j-1) < val.length()) {
        	            				columnWidths.set(j-1, val.length());
        	            			}
        	            			
        	            			Label hdr = new Label(val!=null?val:"");
        		            		//hdr.setStyleName("studio-Label-Small");
        		            		hdr.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        		            		dataGrid.setWidget(i+1, j-1, hdr);
        	            			
        	            		}
        	            	}
                    	
        	    			// Update the column widths
        					for (int i = 0; i < numCols; i++) {
        						dataGrid.setColumnWidth(i, getColumnWidth(columnWidths, i));
        					}
        					
        	            }
            			
//        				waitingBox.setVisible(false);

                    }});		
            }});		

			//dataProvider.setItem(item, count);
		
		}
	
	public ItemDataPanel() {
		
		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());
		
		initWidget(mainPanel);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		
		PushButton refresh = getRefreshButton();
		PushButton create = getCreateButton();
		PushButton drop =  getDropDataButton();
		importData = getImportButton();
		
		bar.add(refresh);
		bar.add(getExportWidget());
		bar.add(create);
		bar.add(drop);
		
		
		bar.add(importData);
		
		return bar.asWidget();
	}
	
	private PushButton getRefreshButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.refresh()));
		button.setTitle("Refresh");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();				
			}			
		});
		return button;
	}
	
	/**
	 * Method to create the export widget.
	 */
	private ExportWidget getExportWidget() {
		 ExportDataEventHandler handler = initHandler();
		 ExportWidget exportWidget = new ExportWidget(handler);		
		 initDownloadButton(exportWidget.getButton());
		return exportWidget;
	}
	
	/**
	 * Method to initialize the handlers for the export widget
	 */
	private ExportDataEventHandler initHandler() {
		return new ExportDataEventHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String requestParams = "connectionToken=" + PgStudio.getToken() + "&item=" + item.getId() + "&fileType="
						+ getFileType() + "&type=" + item.getItemType().toString();
				String url = GWT.getModuleBaseURL() + "/downloadFile?" + requestParams;
				Window.open(url, "_blank", null);
			}
			
		};
	}
	
	/**
	 * Method to initialize the download button for the data export
	 */
	private void initDownloadButton(PushButton button) {
		button.addClickHandler(new ExportDataEventHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String requestParams = "connectionToken=" + PgStudio.getToken() + "&item=" + item.getId() + "&fileType="
						+ getFileType() + "&type=" + item.getItemType().toString();
				String url = GWT.getModuleBaseURL() + "/downloadFile?" + requestParams;
				Window.open(url, "_blank", null);
			}
		});
	}
	
	private PushButton getCreateButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.create()));
		button.setTitle("Insert Row");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					getCreateDialogBox();
				} catch (PopUpException caught) {
					Window.alert("Caught Expception");
					Window.alert(caught.getMessage());
				}
			}
		});

		return button; 
	}
	
	private PushButton getDropDataButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.delete()));
		button.setTitle("Delete Row(s)");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					getDropDialogBox();
				} catch (PopUpException caught) {
					Window.alert("Caught Expception");
					Window.alert(caught.getMessage());
				}
			}
		});

		return button; 
	}
	
	private Widget getMainPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("100%");
		panel.setHeight("100%");
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setWidth("100%");
		scroll.setHeight(MAIN_HEIGHT);
		
		dataGrid = new SimpleGrid();
		dataGrid.setWidth("100%");
		
		scroll.add(dataGrid);
		panel.add(scroll);

		return panel.asWidget();
	}

	private DialogBox getWaitingBox() {
		DialogBox box = new DialogBox();
		box.setAnimationEnabled(true);
		box.setGlassEnabled(true);

		VerticalPanel v = new VerticalPanel();
		v.add(new Image(PgStudio.Images.spinner()));
		
		Label msg = new Label();
		msg.setText("Loading Data...");
		msg.setStyleName("studio-Label-Small");
		v.add(msg);
		
		box.add(v);
		
		return box;
	}
	
	/**
	 * Method to create import data button
	 */
	private PushButton getImportButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.upload()));
		button.setTitle("Import Data");
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ImportDataPopUp popUp = new ImportDataPopUp();
				popUp.setItem(item);
				
				try{
					DialogBox box = popUp.getDialogBox();
					box.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> arg0) {
							refresh();							
						}
					});
				}catch(Exception e) {}
			}
		});
		return button;
	}
	
	@Override
	public void refresh() {
		setItem(item);
	}

	private static final native JsArray<ItemDataJsObject> json2Messages(
			String json)
	/*-{ 
	  	return eval(json); 
	}-*/;
	
	private static final native JsArray<ColumnJsObject> jsonMeta2Messages(
			String json)
	/*-{
		return eval(json);
	}-*/;

	private static final native JsArray<QueryMetaDataJsObject> json2MetaDataMessages(
			String json)
	/*-{ 
	  	return eval(json); 
	}-*/;

	private int getColumnWidth(List<Integer> columnWidths, int index) {
		int pad = 15;
		
		return (columnWidths.get(index) * PIX_PER_CHAR) + pad;
	}
	
	public DialogBox getCreateDialogBox() throws PopUpException {
		createDialogBox.setWidget(getCreatePanel());
		createDialogBox.setGlassEnabled(true);
		createDialogBox.center();

		return createDialogBox;
	}
	
	private PopupPanel getCreatePanel(){
		VerticalPanel panel = new VerticalPanel();

		ScrollPanel sp = new ScrollPanel();
		sp.setWidth("100%");
		sp.setHeight("100%");
		sp.setAlwaysShowScrollBars(false);
		
		sp.add(panel);
		
		PopupPanel pp = new PopupPanel();
        pp.add(sp);
        pp.setPixelSize(600, 500);
        pp.setStyleName("StudioPopup");
        pp.show();
		
		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);
		
		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lbl.setText("Add Row");

		Label lblCName = new Label();
		lblCName.setStyleName("StudioPopup-Msg-Strong");
		lblCName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lblCName.setText("Column Name");
		lblCName.setWidth("130px");

		Label lblNull = new Label();
		lblNull.setStyleName("StudioPopup-Msg-Strong");
		lblNull.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lblNull.setText("Nullable");
		lblNull.setWidth("80px");
		
		Label lblType = new Label();
		lblType.setStyleName("StudioPopup-Msg-Strong");
		lblType.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lblType.setText("Data Type");
		lblType.setWidth("100px");
		
		Label lblValue = new Label();
		lblValue.setStyleName("StudioPopup-Msg-Strong");
		lblValue.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);	 
		lblValue.setText("Value");
		lblValue.setWidth("100px");
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblCName);
		namePanel.add(lblNull);
		namePanel.add(lblType);
		namePanel.add(lblValue);
		namePanel.setCellVerticalAlignment(lblCName, HasVerticalAlignment.ALIGN_MIDDLE);

		info.add(lbl);
		info.add(namePanel);
		
		int numCols = colMetadata.length();
		columnName = new TextBox[numCols];
		notNullArr = new CheckBox[numCols];
		lblColumnNameArr = new ArrayList<String>();
		TextBox columnValue = null; 
		String name = null;
		CheckBox notNull = null;
		Label nullLbl = null;
		lblValueArr = new Label[numCols];
		Label lblValue1 = null;
		Label lblTypeLabel = null;
		lblTypeLabelArr = new Label[numCols];
		//QueryMetaDataJsObject col = null;
		ColumnJsObject col = null;
		HorizontalPanel namePanel1 = null;
		
		for (int i = 0; i < numCols; i++) {
    		col = colMetadata.get(i);
    		name = col.getColumnName();
    		
    		lblColumnNameArr.add(i, name);
    		
    		notNull = new CheckBox();
    		notNull.setStyleName("StudioPopup-Msg");
    		notNull.setEnabled(false);
    		notNull.setWidth("100px");
    		notNull.setValue(col.getNullable()=="true"?true:false);
    		
    		notNullArr[i] = notNull;
    		
    		Image irc = new Image(PgStudio.Images.nullable());
    		
    		nullLbl = new Label();
    		nullLbl.setWidth("16px");
    		nullLbl.setHeight("16px");
    		
    		columnValue = new TextBox();
    		columnValue.setWidth("155px");
    		
    		columnName[i] = columnValue;
    		
    		lblValue1 = new Label();
    		lblValue1.setStyleName("StudioPopup-Msg");
    		lblValue1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
    		lblValue1.setText(name);
    		lblValue1.setWidth("140px");
    		lblValueArr[i] = lblValue1;
    		
    		lblTypeLabel = new Label();
    		lblTypeLabel.setStyleName("StudioPopup-Msg");
    		lblTypeLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
    		lblTypeLabel.setText(col.getDataType());
    		lblTypeLabel.setWidth("200px");
    		lblTypeLabelArr[i] = lblTypeLabel;
    		
    		namePanel1 = new HorizontalPanel();
    		namePanel1.setSpacing(10);
    		namePanel1.add(lblValueArr[i]);
    		//namePanel1.add(notNullArr[i]);
    		if(!"true".equals(col.getNullable()))
    			namePanel1.add(irc);
    		else
    			namePanel1.add(nullLbl);
    		namePanel1.add(lblTypeLabelArr[i]);
    		namePanel1.add(columnName[i]);
    		namePanel1.setCellVerticalAlignment(lblCName, HasVerticalAlignment.ALIGN_MIDDLE);
    		
    		info.add(namePanel1);
    	}
		
		panel.add(info);
		panel.add(getAddCancelButtonPanel());
		return pp;
	}
	
	private Widget getAddCancelButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		bar.setWidth("350px");
		
		Button addButton = new Button("Insert");
		Button addAndRepeatButton = new Button("Insert And Repeat");
		Button cancelButton = new Button("Cancel");
		
		addAndRepeatButton.setWidth("160px");
		
		bar.add(addButton);
		bar.add(addAndRepeatButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_RIGHT);
		bar.setCellHorizontalAlignment(addAndRepeatButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_LEFT);

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean isRecordValid = true;
				if (null != columnName) {
					ArrayList<String> values = new ArrayList<String>();
					TextBox colName = null;
					for(int i = 0; i < columnName.length; i++){
						colName = columnName[i];
						if(notNullArr[i].getValue() && 
								(null == colName.getText() || colName.getText().trim().length() == 0)){
							Window.alert(lblValueArr[i].getText() +" is required to create a row");
							isRecordValid = false;
							break;
						}else if (colName.getText() != null
								&& !colName.getText().equals("")) {
							values.add(colName.getText());
						} else {
							values.add("");
						}
					}
				
					if(isRecordValid){
						try {
							List<ArrayList<String>> dataRows = new ArrayList<ArrayList<String>>();
							dataRows.add(values);
							PgStudio.studioService.importData(PgStudio.getToken(), lblColumnNameArr, dataRows, item.getSchema(), item.getId()+"", item.getName(), new AsyncCallback<String>() {
							    public void onFailure(Throwable caught) {
							        // Show the RPC error message to the user
							        Window.alert(caught.getMessage());
							    }
	
							    public void onSuccess(String result) {
							    	createDialogBox.hide(true);
							    	refresh();
							    }
							});
						} catch (IllegalArgumentException e) {
							Window.alert(e.getMessage());
						} catch (DatabaseConnectionException e) {
							Window.alert(e.getMessage());
						} catch (PostgreSQLException e) {
							Window.alert(e.getMessage());
						}
					}
				} else {
					createDialogBox.hide(true);				
				}
			} 
		});
		
		addAndRepeatButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean isRecordValid = true;
				if (null != columnName) {
					ArrayList<String> values = new ArrayList<>();
					TextBox colName = null;
					for(int i = 0; i < columnName.length; i++){
						colName = columnName[i];
						if(notNullArr[i].getValue() && 
								(null == colName.getText() || colName.getText().trim().length() == 0)){
							Window.alert(lblValueArr[i].getText() +" is required to create a row");
							isRecordValid = false;
						}else if (colName.getText() != null
								&& !colName.getText().equals("")) {
							values.add(colName.getText());
						} else {
							values.add("");
						}
					}
				
					if(isRecordValid){
						try {
							List<ArrayList<String>> dataRows = new ArrayList<ArrayList<String>>();
							dataRows.add(values);
							PgStudio.studioService.importData(PgStudio.getToken(), lblColumnNameArr, dataRows, item.getSchema(), item.getId()+"", item.getName(), new AsyncCallback<String>() {
							    public void onFailure(Throwable caught) {
							        // Show the RPC error message to the user
							        Window.alert(caught.getMessage());
							    }
	
							    public void onSuccess(String result) {
							    	createDialogBox.hide(true);
							    	try {
										getCreateDialogBox();
									} catch (PopUpException caught) {
										Window.alert("Caught Expception");
										Window.alert(caught.getMessage());
									}
							    	refresh();
							    }
							});
						} catch (IllegalArgumentException e) {
							Window.alert(e.getMessage());
						} catch (DatabaseConnectionException e) {
							Window.alert(e.getMessage());
						} catch (PostgreSQLException e) {
							Window.alert(e.getMessage());
						}
					}
				} else {
					createDialogBox.hide(true);				
				}
			} 
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createDialogBox.hide(true);
			}
		});
		
		return bar.asWidget();
	}
	
	public DialogBox getDropDialogBox() throws PopUpException {
		dropDialogBox.setWidget(getDropPanel());
		dropDialogBox.setGlassEnabled(true);
		dropDialogBox.center();
		return dropDialogBox;
	}
	
	private VerticalPanel getDropPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		String title = "This will permanently delete all the data from Table " + item.getName().toUpperCase();
		lbl.setText(title);

		HorizontalPanel warningPanel = new HorizontalPanel();
		Image icon = new Image(PgStudio.Images.warning());
		icon.setWidth("110px");

		VerticalPanel detailPanel = new VerticalPanel();

		Label lblWarning = new Label();
		lblWarning.setStyleName("StudioPopup-Msg");
		lblWarning.setText(WARNING_MSG);

		HorizontalPanel cascadePanel = new HorizontalPanel();
		cascadePanel.setSpacing(5);

		detailPanel.add(lblWarning);
		detailPanel.add(cascadePanel);

		warningPanel.add(icon);
		warningPanel.add(detailPanel);

		info.add(lbl);
		info.add(warningPanel);

		panel.add(info);

		Widget buttonBar = getDropButtonPanel();
		panel.add(buttonBar);
		panel.setCellHorizontalAlignment(buttonBar, HasHorizontalAlignment.ALIGN_CENTER);

		return panel;
	}
	
	private Widget getDropButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		
		bar.add(yesButton);
		bar.add(noButton);
		
		bar.setCellHorizontalAlignment(yesButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(noButton, HasHorizontalAlignment.ALIGN_CENTER);

		yesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					PgStudio.studioService.dropItemData(PgStudio.getToken(), item.getSchema(),item.getName(), new AsyncCallback<String>() {
					    public void onFailure(Throwable caught) {
					        // Show the RPC error message to the user
					        Window.alert(caught.getMessage());
					    }

					    public void onSuccess(String result) {
					    	dropDialogBox.hide(true);
					    	refresh();
					    }
					});
				} catch (IllegalArgumentException e) {
					Window.alert(e.getMessage());
				} catch (DatabaseConnectionException e) {
					Window.alert(e.getMessage());
				} catch (PostgreSQLException e) {
					Window.alert(e.getMessage());
				}			
			}
		});

		noButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dropDialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}
	
}
