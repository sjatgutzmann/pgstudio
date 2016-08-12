package com.openscg.pgstudio.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.openscg.pgstudio.client.PgStudio;
import static com.openscg.pgstudio.shared.Constants.*;

/**
 * Composite class that creates a drop down to select the file download type and a button to initiate the download.
 * @author abhimanyu
 *
 */
public class ExportWidget extends Composite {

	private ListBox listBox = new ListBox();
	private PushButton button = new PushButton(new Image(PgStudio.Images.download()));
	private ExportDataEventHandler handler;

	public ExportWidget(ExportDataEventHandler handler) {
		this.handler = handler;
		HorizontalPanel panel = new HorizontalPanel();
		initDownloadOptions();
		initDownloadButton();
		panel.add(listBox);
		panel.add(button);
		initWidget(panel);
	}

	private void initDownloadOptions() {
		listBox.addItem(CSV);
		listBox.addItem(EXCEL);		
		listBox.setVisibleItemCount(1);
		listBox.setStyleName("roundList");
		listBox.setStyleName("exportOptions");
		listBox.addChangeHandler(handler);
	}

	private void initDownloadButton() {
		button.setTitle("Download");
		button.addClickHandler(handler);
	}

	public PushButton getButton() {
		return button;
	}

	public ListBox getListBox() {
		return listBox;
	}
}
