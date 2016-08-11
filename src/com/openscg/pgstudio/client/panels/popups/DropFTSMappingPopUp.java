package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.FTSConfigurationDetailsInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.providers.FTSConfigurationDetailsDataProvider;

public class DropFTSMappingPopUp implements StudioPopUp {

	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();

	private SingleSelectionModel<ModelInfo> selectionModel = null;

	private FTSConfigurationDetailsDataProvider dataProvider = null;

	private DatabaseObjectInfo schema = null;

	private String configName = null;

	private String WARNING_MSG = null;

	private PgStudio main =null;

	private FTSConfigurationDetailsInfo item = null;

	public void setMain(PgStudio main) {
		this.main = main;
	}

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if(item == null) {
			throw new PopUpException("Record is not selected for deletion.");
		}
		dialogBox.setWidget(getPanel());
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		return dialogBox;
	}

	public void setSelectionModel(SingleSelectionModel model) {
		this.selectionModel = model;
	}

	public void setDataProvider(FTSConfigurationDetailsDataProvider provider) {
		this.dataProvider =  provider;

	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}

	public void setConfigName(String configName) {
		this.configName = configName;

	}


	public FTSConfigurationDetailsInfo getItem() {
		return item;
	}

	public void setItem(FTSConfigurationDetailsInfo item) {
		this.item = item;
		WARNING_MSG = "Are you sure you want to drop mapping " + item.getToken()+ " of FTS configuration "+ item.getConfigName() +" ?";
	}

	private VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	

		String title = "DROP";
		lbl.setText(title);

		info.add(lbl);

		HorizontalPanel warningPanel = new HorizontalPanel();
		Image icon = new Image(PgStudio.Images.warning());
		icon.setWidth("110px");

		VerticalPanel detailPanel = new VerticalPanel();

		Label lblWarning = new Label();
		lblWarning.setStyleName("StudioPopup-Msg");

		lblWarning.setText(WARNING_MSG);

		detailPanel.add(lblWarning);

		warningPanel.add(icon);
		warningPanel.add(detailPanel);

		info.add(lbl);
		info.add(warningPanel);

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

		Button dropButton = new Button("Drop");
		Button cancelButton = new Button("Cancel");

		bar.add(dropButton);
		bar.add(cancelButton);

		bar.setCellHorizontalAlignment(dropButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		dropButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
				ac.setAutoRefresh(true);
				ac.setShowResultOutput(true);
				try {
					studioService.dropFTSMapping(PgStudio.getToken(), PgStudio.getSelectedSchema().getName(), item.getConfigName(), item.getToken(), ac);
				} catch(Exception ex) {
					Window.alert(ex.getMessage());
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
}