/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.utils;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class TextFormat {

	 public static String getHeaderString(String text, ImageResource image) {
		    // Add the image and text to a horizontal panel
		    HorizontalPanel hPanel = new HorizontalPanel();
		    hPanel.setSpacing(0);
		    hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		    hPanel.add(new Image(image));
		    HTML headerText = new HTML(text);
		    headerText.setStyleName("cw-StackPanelHeader");
		    hPanel.add(headerText);

		    // Return the HTML string for the panel
		    return hPanel.getElement().getString();
	}

}
