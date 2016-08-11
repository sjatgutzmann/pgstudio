package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;

public class AlterFTSConfigPopUp extends AddFTSConfigPopUp implements StudioModelPopUp {

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		if (item == null)
			throw new PopUpException("Config is not set");
		dialogBox.setWidget(getPanel());
		setConfName(item.getName());	
		setComment(item.getComment());
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
		lbl.setText("Alter FTS Configuration");

		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblName.setText("Name");

		confName = new TextBox();
		confName.setWidth("155px");

		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(confName);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		CaptionPanel dtCommentPanel = new CaptionPanel("Comment");
		dtCommentPanel.setStyleName("StudioCaption");
		comment = new TextArea();
		comment.setVisibleLines(2);
		comment.setWidth("300px");
		dtCommentPanel.add(comment);

		info.add(lbl);
		info.add(namePanel);
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
				if (comment.getText() != null) {
					UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
					ac.setAutoRefresh(true);
					ac.setShowResultOutput(true);
					try {				
						studioService.alterFTSConfiguration(PgStudio.getToken(), PgStudio.getSelectedSchema().getName(), confName.getValue(), comment.getValue(), ac);	
					} catch (DatabaseConnectionException | PostgreSQLException e) {
						Window.alert(e.getMessage());
					}

				} else {
					Window.alert("Name and Datatype are mandatory to create a column");
				}
			} 
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				confName.setText("");
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}


}
