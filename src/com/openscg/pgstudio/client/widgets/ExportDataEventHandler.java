package com.openscg.pgstudio.client.widgets;

import com.google.gwt.event.dom.client.ChangeEvent;
import static com.openscg.pgstudio.shared.Constants.*;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Class to handle the events related to the export data components. This
 * abstract class provides the common event handler that handles the drop down
 * selection of file type. The clients of this class should provide
 * implementation to the download button click as this download button might
 * need to provide a custom implementation like constructing request parameters
 * based on the screen where it is being used.
 * 
 * @author abhimanyu
 *
 */
public abstract class ExportDataEventHandler implements ChangeHandler, ClickHandler {

	private String fileType = CSV;

	@Override
	public void onChange(ChangeEvent event) {
		ListBox listBox = ((ListBox) event.getSource());
		fileType = listBox.getItemText(listBox.getSelectedIndex());
	}

	protected String getFileType() {
		return fileType;
	}
}
