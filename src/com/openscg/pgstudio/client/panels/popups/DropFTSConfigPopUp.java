package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.FTSConfigurationInfo;

public class DropFTSConfigPopUp extends AddFTSConfigPopUp implements StudioPopUp {

	//private final static String WARNING_MSG = "This will permanently delete this schema. Are you sure you want to continue?";

	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();

	private SingleSelectionModel<FTSConfigurationInfo> selectionModel = null;

	private DatabaseObjectInfo schema = null;

	private String configName = null;

	private CheckBox cascadeBox;

	private String WARNING_MSG = null;

	private PgStudio main =null;

	public void setMain(PgStudio main) {
		this.main = main;
	}

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		dialogBox.setWidget(getPanel());
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		return dialogBox;
	}

	public void setSelectionModel(SingleSelectionModel model) {
		this.selectionModel = model;
	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
		WARNING_MSG = "Are you sure you want to drop the FTS configuration "+configName + " ?";
	}

	public VerticalPanel getPanel(){
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

		HorizontalPanel cascadePanel = new HorizontalPanel();
		cascadePanel.setSpacing(5);

		SimplePanel cascadeBoxPanel = new SimplePanel();
		cascadeBoxPanel.setStyleName("roundedCheck");

		cascadeBox = new CheckBox();
		cascadeBoxPanel.add(cascadeBox);

		Label lblCascade = new Label();
		lblCascade.setStyleName("StudioPopup-Msg");
		lblCascade.setText("Cascade");

		cascadePanel.add(cascadeBoxPanel);
		cascadePanel.add(lblCascade);
		cascadePanel.setCellVerticalAlignment(cascadeBoxPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		cascadePanel.setCellVerticalAlignment(lblCascade, HasVerticalAlignment.ALIGN_MIDDLE);

		detailPanel.add(lblWarning);
		detailPanel.add(cascadePanel);

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

	public Widget getButtonPanel(){
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
				boolean cascade = cascadeBox.getValue();
				UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(
						dialogBox, dataProvider);
				ac.setAutoRefresh(true);
				ac.setShowResultOutput(true);
				try {
					studioService.dropFTSConfiguration(PgStudio.getToken(), schema.getName(), configName, cascade, ac);
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