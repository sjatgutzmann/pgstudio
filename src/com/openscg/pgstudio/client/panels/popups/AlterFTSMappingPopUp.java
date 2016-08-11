package com.openscg.pgstudio.client.panels.popups;

import java.util.ArrayList;

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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.messages.DataTypesJsObject;
import com.openscg.pgstudio.client.messages.FTSConfigurationJsObject;
import com.openscg.pgstudio.client.models.DataTypeInfo;
import com.openscg.pgstudio.client.models.FTSConfigurationInfo;
import com.openscg.pgstudio.client.providers.FTSConfigurationDetailsDataProvider;
import com.openscg.pgstudio.client.providers.ListProvider;

public class AlterFTSMappingPopUp extends AddFTSMappingPopUp implements StudioPopUp {

	protected final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	protected SingleSelectionModel<FTSConfigurationInfo> selectionModel = null;
	protected FTSConfigurationDetailsDataProvider dataProvider = null;

	protected ArrayList<String> ftsDictionariesList = new ArrayList<String>();
	private FTSConfigurationInfo ftsConfigInfo;
	private String configName;
	private String currentDict;

	final DialogBox dialogBox = new DialogBox();
	protected TextBox mappingName;
	protected ListBox ftsDictionaries;


	public AlterFTSMappingPopUp() {

	}

	public AlterFTSMappingPopUp(FTSConfigurationInfo ftsConfigInfo) {
		this.ftsConfigInfo = ftsConfigInfo;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getCurrentDict() {
		return currentDict;
	}

	public void setCurrentDict(String currentDict) {
		this.currentDict = currentDict;
	}

	@Override
	public DialogBox getDialogBox() throws PopUpException {

		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		if (item == null)
			throw new PopUpException("Record is not selected for alter");
		dialogBox.setWidget(getPanel());
		populateFTSDictionaries();
		setCurrentData();
		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}


	@Override
	public void setSelectionModel(SingleSelectionModel model) {
		this.selectionModel = model;
	}

	@Override
	public void setDataProvider(ListProvider provider) {
		this.dataProvider = (FTSConfigurationDetailsDataProvider) provider;

	}

	public String getMappingName() {
		return mappingName.getText();
	}

	public void setMappingName(String mappingName) {
		this.mappingName.setText(mappingName);
	}



	protected VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		//VerticalPanel info = new VerticalPanel();
		//info.setSpacing(10);

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lbl.setText("Alter");

		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblName.setText("Mapping");

		mappingName = new TextBox();
		mappingName.setWidth("155px");

		HorizontalPanel mappingPanel = new HorizontalPanel();
		mappingPanel.setSpacing(10);
		mappingPanel.add(lblName);
		mappingPanel.add(mappingName);
		mappingPanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);



		Label lblDictionaries = new Label();
		lblDictionaries.setStyleName("StudioPopup-Msg");
		lblDictionaries.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblDictionaries.setText("Dictionary");

		ftsDictionaries = new ListBox();
		ftsDictionaries.setWidth("200px");

		HorizontalPanel dictionariesPanel = new HorizontalPanel();
		dictionariesPanel.setSpacing(10);
		dictionariesPanel.add(lblDictionaries);
		dictionariesPanel.add(ftsDictionaries);
		dictionariesPanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);


		panel.add(lbl);
		panel.add(mappingPanel);
		panel.add(dictionariesPanel);
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
				if(mappingName.getText() == null || mappingName.getText().equals("")) {
					Window.alert("Mapping name can not be blank");
				}  
				UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
				ac.setAutoRefresh(true);
				ac.setShowResultOutput(true);
				try {
					//Window.alert("item.getConfigName()::::"+item.getConfigName()+ " item.getToken():::::"+item.getToken()+"  item.getDictionaries()::: "+item.getDictionaries()+"  ftsDictionaries.getValue(ftsDictionaries.getSelectedIndex())"+ftsDictionaries.getValue(ftsDictionaries.getSelectedIndex()));
					studioService.alterFTSMapping(PgStudio.getToken(), PgStudio.getSelectedSchema().getName(), item.getConfigName(), item.getToken(), item.getDictionaries(), ftsDictionaries.getValue(ftsDictionaries.getSelectedIndex()) , ac);
				} catch(Exception ex) {
					Window.alert("Error.."+ex.getMessage());
				} 

			} 
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mappingName.setText("");
				refresh();
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}

	protected void refresh() {
		dataProvider.setItem(item);
	}

	protected DataTypeInfo msgToDataTypeInfo(DataTypesJsObject msg) {
		DataTypeInfo type = new DataTypeInfo(0, Integer.parseInt(msg.getId()), msg.getTypeName());
		type.setUsageCount(Integer.parseInt(msg.getUsageCount()));
		if (msg.getHasLength().equalsIgnoreCase("true")) {
			type.setHasLength(true);
		} else {
			type.setHasLength(false);
		}
		return type;
	}

	protected void populateFTSDictionaries() {
		try {
			studioService.getFTSDictionariesList(PgStudio.getToken(), new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					ftsDictionaries.clear();
					ftsDictionariesList.clear();
					Window.alert(caught.getMessage());
				}

				public void onSuccess(String result) {
					ftsDictionaries.clear();
					ftsDictionariesList.clear();
					String dictName = "";
					JsArray<FTSConfigurationJsObject> jsonArr = json2Messages(result);
					if(jsonArr != null) {
						for(int i = 0 ; i < jsonArr.length(); i++) {
							FTSConfigurationJsObject jsonObj = jsonArr.get(i);
							dictName = jsonObj.getName();
							ftsDictionaries.addItem(dictName);
							if(dictName.equals(item.getDictionaries())) {
								ftsDictionaries.setItemSelected(i, true);
							}

						}
					} 
				}

			});
		} catch(Exception ex) {
			Window.alert(ex.getMessage());
		}

	}

	private void setCurrentData() {
		mappingName.setText(item.getToken());
	}


	private static final native JsArray<FTSConfigurationJsObject> json2Messages(
			String json)
			/*-{
return eval(json);
}-*/;
}

