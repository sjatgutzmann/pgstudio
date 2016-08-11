/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import org.gwt.advanced.client.ui.widget.SimpleGrid;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.messages.DictionaryJsObject;
import com.openscg.pgstudio.client.models.DictionaryInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.panels.popups.AlterDictionaryPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;

public class DictionariesDataPanel extends Composite implements DetailsPanel {

	private ModelInfo item;
	private VerticalPanel mainPanel;
	private SimpleGrid grid;
	private Label detailsLbl = new Label("Details");
	AlterDictionaryPopUp pop;
	public DictionariesDataPanel() {
		mainPanel = new VerticalPanel();
		mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());
		initWidget(mainPanel);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		
		PushButton refresh = getRefreshButton();
		PushButton alter = getAlterButton();
		
		bar.add(refresh);
		bar.add(alter);
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
		grid.setWidget(4, 0, new Label("Options"));
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

	private PushButton getAlterButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.edit()));
		button.setTitle("Alter Dictionary");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pop = new AlterDictionaryPopUp();
				DictionaryInfo dictInfo = (DictionaryInfo) item;
				pop.setDictName(dictInfo.getName());
				pop.setOptions(dictInfo.getOptions());
				pop.setComments(dictInfo.getComment());
				pop.setSchema(PgStudio.getSelectedSchema());
				try {
					pop.getDialogBox();
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
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
		
		try {
			
			PgStudio.studioService.fetchDictionaryDetails(PgStudio.getToken(), item.getSchema(), item.getId(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String json) {		
					JsArray<DictionaryJsObject> array = json2Messages(json);
					DictionaryInfo info = msgToTypeInfo(array.get(0));
					grid.setWidget(1, 1, new Label(info.getName()));
					grid.setWidget(2, 1, new Label(info.getSchemaName()));
					grid.setWidget(3, 1, new Label(info.getComment()));
					grid.setWidget(4, 1, new Label(info.getOptions()));
				}
				
				@Override
				public void onFailure(Throwable caught) {					
				}
			}) ;
		} catch (Exception e) {}
		
	
		
	}

	private DictionaryInfo msgToTypeInfo(DictionaryJsObject msg) {
		int id = Integer.parseInt(msg.getId());
		
		DictionaryInfo dict = new DictionaryInfo(item.getSchema(), msg.getSchema(), id, msg.getName(), msg.getComment(), msg.getOptions());
		return dict;
	}

	private static final native JsArray<DictionaryJsObject> json2Messages(String json)
	/*-{ 
	  	return eval(json); 
	}-*/;
}
