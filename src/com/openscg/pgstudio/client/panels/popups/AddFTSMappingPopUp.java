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
import com.openscg.pgstudio.client.messages.FTSConfigurationJsObject;
import com.openscg.pgstudio.client.models.FTSConfigurationDetailsInfo;
import com.openscg.pgstudio.client.models.FTSConfigurationInfo;
import com.openscg.pgstudio.client.providers.FTSConfigurationDetailsDataProvider;
import com.openscg.pgstudio.client.providers.ListProvider;

public class AddFTSMappingPopUp implements StudioPopUp {

	protected final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);
	protected SingleSelectionModel<FTSConfigurationInfo> selectionModel = null;
	protected FTSConfigurationDetailsDataProvider dataProvider = null;

	protected ArrayList<String> ftsTokensList = new ArrayList<String>();
	protected ArrayList<String> ftsDictionariesList = new ArrayList<String>();

	protected FTSConfigurationDetailsInfo item = null;
	final DialogBox dialogBox = new DialogBox();
	protected ListBox ftsTokens;
	protected ListBox ftsDictionaries;
	protected TextBox confName;

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		if (item == null)
			throw new PopUpException("Configuration name is not set");
		dialogBox.setWidget(getPanel());
		populateFTSTokens();
		populateFTSDictionaries();
		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}

	public void setSelectionModel(SingleSelectionModel model) {
		this.selectionModel = model;
	}

	public void setDataProvider(ListProvider provider) {
		this.dataProvider = (FTSConfigurationDetailsDataProvider) provider;

	}

	public void setItem(FTSConfigurationDetailsInfo item) {
		this.item = item;
	}

	public String getConfName() {
		return confName.getText();
	}

	public void setConfName(String confName) {
		this.confName.setText(confName);
	}

	protected VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");
		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lbl.setText("Alter");

		Label lblTokens = new Label();
		lblTokens.setStyleName("StudioPopup-Msg");
		lblTokens.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblTokens.setText("Mapping");

		ftsTokens = new ListBox();
		ftsTokens.setWidth("200px");

		HorizontalPanel tokensPanel = new HorizontalPanel();
		tokensPanel.setSpacing(10);
		tokensPanel.add(lblTokens);
		tokensPanel.add(ftsTokens);

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
		dictionariesPanel.setCellVerticalAlignment(lblTokens, HasVerticalAlignment.ALIGN_MIDDLE);

		panel.add(lbl);
		panel.add(tokensPanel);
		panel.add(dictionariesPanel);
		panel.add(getButtonPanel());
		return panel;
	}

	protected Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		bar.setWidth("350px");

		Button createButton = new Button("Create");
		Button cancelButton = new Button("Cancel");

		bar.add(createButton);
		bar.add(cancelButton);

		bar.setCellHorizontalAlignment(createButton, HasHorizontalAlignment.ALIGN_RIGHT);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_LEFT);

		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
				ac.setAutoRefresh(true);
				ac.setShowResultOutput(true);
				try {
					String token = ftsTokens.getValue(ftsTokens.getSelectedIndex());
					token = token.substring(0, token.indexOf("-") -1);
					studioService.createFTSMapping(PgStudio.getToken(), PgStudio.getSelectedSchema().getName(), item.getConfigName(), token, ftsDictionaries.getValue(ftsDictionaries.getSelectedIndex()), ac);
				} catch(Exception ex) {
					Window.alert("Error.."+ex.getMessage());
				}
			} 
		});
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
				dialogBox.hide(true);
			}
		});
		return bar.asWidget();
	}

	protected void refresh() {
		dataProvider.setItem(item);
	}

	protected void populateFTSTokens() {
		try {
			studioService.getFTSTokensList(PgStudio.getToken(), new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					ftsTokens.clear();
					ftsTokensList.clear();
					Window.alert(caught.getMessage());
				}

				public void onSuccess(String result) {
					ftsTokens.clear();
					ftsTokensList.clear();
					ftsTokens.addItem("");
					String tokenName = "";
					JsArray<FTSConfigurationJsObject> jsonArr = json2Messages(result);
					if(jsonArr != null) {
						for(int i = 0 ; i < jsonArr.length(); i++) {
							FTSConfigurationJsObject jsonObj = jsonArr.get(i);
							tokenName = jsonObj.getName();
							ftsTokens.addItem(tokenName);
						}
					} 
				}
			});
		} catch(Exception ex) {
			Window.alert(ex.getMessage());
		}
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
					ftsDictionaries.addItem("");
					String dictName = "";
					JsArray<FTSConfigurationJsObject> jsonArr = json2Messages(result);
					if(jsonArr != null) {
						for(int i = 0 ; i < jsonArr.length(); i++) {
							FTSConfigurationJsObject jsonObj =jsonArr.get(i);
							dictName = jsonObj.getName();
							ftsDictionaries.addItem(dictName);
						}
					}  
				}
			});

		}
		catch(Exception ex) {
			Window.alert(ex.getMessage());
		}
	}

	private static final native JsArray<FTSConfigurationJsObject> json2Messages(
			String json)
			/*-{
return eval(json);
}-*/;
}

