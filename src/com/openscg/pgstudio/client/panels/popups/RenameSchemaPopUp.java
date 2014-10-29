/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;

public class RenameSchemaPopUp implements StudioPopUp {

	private final PgStudioServiceAsync studioService = GWT
			.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();

	private DatabaseObjectInfo schema = null;

	private TextBox itemName;

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (schema == null)
			throw new PopUpException("Schema is not set");

		dialogBox.setWidget(getPanel());

		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}

	private VerticalPanel getPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		String label = "Rename Schema";

		lbl.setText(label);

		itemName = new TextBox();
		itemName.setWidth("155px");
		itemName.setText(schema.getName());

		info.add(lbl);
		info.add(itemName);

		panel.add(info);
		panel.add(getButtonPanel());

		return panel;
	}

	private Widget getButtonPanel() {
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);

		Button cancelButton = new Button("Cancel");
		Button renameButton = new Button("Rename");

		bar.add(renameButton);
		bar.add(cancelButton);

		bar.setCellHorizontalAlignment(renameButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton,
				HasHorizontalAlignment.ALIGN_CENTER);

		renameButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (itemName.getText() != null
						&& !itemName.getText().equals("")) {
					UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(
							dialogBox, null);
					ac.setAutoRefresh(false);
					ac.setShowResultOutput(false);

					studioService.renameSchema(PgStudio.getToken(), schema.getName(),
							itemName.getText(), ac);	
				}
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				itemName.setText("");
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}

}
