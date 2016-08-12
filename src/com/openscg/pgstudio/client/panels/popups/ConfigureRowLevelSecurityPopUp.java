/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
import com.openscg.pgstudio.client.models.TableInfo;
import com.openscg.pgstudio.client.providers.ModelListProvider;
import com.openscg.pgstudio.client.providers.TableListDataProvider;

public class ConfigureRowLevelSecurityPopUp implements StudioModelPopUp {

	private final static String WARNING_MSG = "This will enable Row Level Security for the table. Are you "
			+ "sure you would like to do this?";

	private final static String WARNING_MSG_2 = "This will disable Row Level Security for the table. Are you "
			+ "sure you would like to do this?";

	private final static String FORCE_WARNING = "Force policies to apply to table owners";

	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();

	private SingleSelectionModel<TableInfo> selectionModel = null;
	private TableListDataProvider dataProvider = null;

	private boolean hasRowSecurity = false;
	private boolean forceRowSecurity = false;
	private long itemId = 0;

	@SuppressWarnings("unused")
	private DatabaseObjectInfo schema = null;

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		dialogBox.setWidget(getPanel());

		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}

	private VerticalPanel getPanel() throws PopUpException {
		VerticalPanel panel = new VerticalPanel();

		try {
			panel.setStyleName("StudioPopup");

			VerticalPanel info = new VerticalPanel();
			info.setSpacing(10);

			Label lbl = new Label();
			lbl.setStyleName("StudioPopup-Msg");
			lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

			if (hasRowSecurity)
				lbl.setText(WARNING_MSG_2);
			else
				lbl.setText(WARNING_MSG);

			info.add(lbl);

			panel.add(info);

			CaptionPanel forcePanel = new CaptionPanel();
			forcePanel.setStyleName("StudioCaption");
			forcePanel.add(getCheckBox(FORCE_WARNING));

			panel.add(forcePanel);

			Widget buttonBar = getButtonPanel();
			panel.add(buttonBar);
			panel.setCellHorizontalAlignment(buttonBar, HasHorizontalAlignment.ALIGN_CENTER);
		} catch (Exception e) {
			throw new PopUpException(e.getMessage());
		}

		return panel;
	}

	private Widget getButtonPanel() {
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
				UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
				ac.setAutoRefresh(true);
				ac.setShowResultOutput(false);

				studioService.configureRowSecurity(PgStudio.getToken(), itemId, !hasRowSecurity, forceRowSecurity, ac);
			}
		});

		noButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}

	private Widget getCheckBox(final String label) {
		HorizontalPanel boxPanel = new HorizontalPanel();

		SimplePanel simpleBoxPanel = new SimplePanel();
		simpleBoxPanel.setStyleName("roundedCheck");

		final CheckBox checkbox = new CheckBox();

		checkbox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkbox.getValue()) {
					forceRowSecurity = true;
				} else {
					forceRowSecurity = false;
				}
			}

		});

		simpleBoxPanel.add(checkbox);

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg");
		lbl.setText(label);

		boxPanel.add(simpleBoxPanel);
		boxPanel.add(lbl);
		boxPanel.setCellHorizontalAlignment(simpleBoxPanel, HasHorizontalAlignment.ALIGN_LEFT);
		boxPanel.setCellHorizontalAlignment(lbl, HasHorizontalAlignment.ALIGN_RIGHT);

		boxPanel.setCellVerticalAlignment(simpleBoxPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		boxPanel.setCellVerticalAlignment(lbl, HasVerticalAlignment.ALIGN_MIDDLE);

		return boxPanel.asWidget();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectionModel(@SuppressWarnings("rawtypes") SingleSelectionModel model) {
		this.selectionModel = model;

		TableInfo table = (TableInfo) model.getSelectedObject();
		hasRowSecurity = table.hasRowSecurity();
		forceRowSecurity = table.forceRowSecurity();
		itemId = table.getId();
	}

	@Override
	public void setDataProvider(ModelListProvider provider) {
		this.dataProvider = (TableListDataProvider) provider;

	}

}
