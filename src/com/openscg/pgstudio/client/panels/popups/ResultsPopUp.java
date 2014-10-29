/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResultsPopUp implements StudioPopUp {
	
	final DialogBox dialogBox = new DialogBox();
	final String header;
	final String content;
	
	public ResultsPopUp(String header, String content) {
		this.header = header;
		this.content = content;
	}
	
	@Override
	public DialogBox getDialogBox() throws PopUpException {

		final VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		dialogBox.setText(header);
		panel.add(new Label(content));
		final Button buttonClose = new Button("Close", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				dialogBox.hide();
			}
		});

		final Label emptyLabel = new Label("");
		emptyLabel.setSize("auto", "25px");
		panel.add(emptyLabel);
		panel.add(emptyLabel);
		buttonClose.setWidth("90px");
		panel.add(buttonClose);
		panel.setCellHorizontalAlignment(buttonClose,
				HasHorizontalAlignment.ALIGN_RIGHT);
		dialogBox.add(panel);

		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;		
	}
}
