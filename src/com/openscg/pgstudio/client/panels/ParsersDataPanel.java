/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import org.gwt.advanced.client.ui.widget.SimpleGrid;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.models.ParserInfo;

public class ParsersDataPanel extends Composite implements DetailsPanel {

	private ModelInfo item;
	private VerticalPanel mainPanel;
	private SimpleGrid grid;
	private Label detailsLbl = new Label("Details");
	
	public ParsersDataPanel() {
		mainPanel = new VerticalPanel();
		//mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());
		initWidget(mainPanel);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		
		PushButton refresh = getRefreshButton();

		bar.add(refresh);
		bar.setCellHorizontalAlignment(refresh, HasHorizontalAlignment.ALIGN_CENTER);

		return bar.asWidget();
	}
	private Widget getMainPanel() { 
		VerticalPanel panel = new VerticalPanel();
		grid = new SimpleGrid();
		grid.setWidth("100%");
		grid.setStyleName("StudioOverflow main-data");
		detailsLbl.setStyleName("main-data label");
		
		grid.setWidget(1, 0, new Label("Name"));
		grid.setWidget(2, 0, new Label("Schema"));
		grid.setWidget(3, 0, new Label("Comments"));
		panel.setSpacing(10);
		
		panel.add(detailsLbl);
		panel.add(grid);
		
		
		
		return panel.asWidget();
	}
	private PushButton getRefreshButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.refresh()));
		button.setTitle("Refresh");
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
		return button;
	}

	@Override
	public void refresh() {
		setItem(item);
	}

	@Override
	public void setItem(ModelInfo selectedItem) {
		this.item = selectedItem;
		
		ParserInfo parserInfo = (ParserInfo) item;
		
		grid.setWidget(1, 1, new Label(parserInfo.getName()));
		grid.setWidget(2, 1, new Label(parserInfo.getSchemaName()));
		grid.setWidget(3, 1, new Label(parserInfo.getComments()));
		
	}

	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{ 
	return eval(json); 
	}-*/;
}
