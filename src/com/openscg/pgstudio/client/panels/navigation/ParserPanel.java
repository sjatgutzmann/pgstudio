/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.navigation;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.ParserInfo;
import com.openscg.pgstudio.client.providers.ParserListDataProvider;

public class ParserPanel extends Composite implements MenuPanel {

	private PgStudio main;
	private DatabaseObjectInfo schema;
	private ParserListDataProvider dataProvider = new ParserListDataProvider();
	private DataGrid<ParserInfo> dataGrid;
	private VerticalPanel mainPanel;
	private final SingleSelectionModel<ParserInfo> selectionModel = 
		    	new SingleSelectionModel<ParserInfo>(ParserInfo.KEY_PROVIDER);
	
	private boolean isFirst = true;
		    
	private static interface GetValue<C> {
		C getValue(ParserInfo object);
	}

	public ParserPanel(PgStudio main) {
		this.main = main;
		mainPanel = new VerticalPanel();
		mainPanel.setWidth("95%");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.add(getButtonBar());
		
		dataGrid = new DataGrid<ParserInfo>(PgStudio.MAX_PANEL_ITEMS, ParserInfo.KEY_PROVIDER);
		dataGrid.setHeight(PgStudio.LEFT_PANEL_HEIGHT);
		
//		mainPanel.add(getParserList());
//		
//		dataGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<ParserInfo>() {
//			@Override
//			public void onCellPreview(CellPreviewEvent<ParserInfo> event) {
//				if (BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {
//					if (dataGrid.getRowCount() == 1) {
//						ParserInfo i = dataProvider.getList().get(0);
//
//						if (dataGrid.getSelectionModel().isSelected(i)) {
//							selectFirst();
//						}
//					}
//				}
//			}
//		});

		initWidget(mainPanel);
	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
		//dataProvider.setSchema(schema);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		PushButton refresh = getRefreshButton();
		bar.add(refresh);
		return bar.asWidget();
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

	private Widget getParserList() {
//		dataGrid = new DataGrid<ParserInfo>(PgStudio.MAX_PANEL_ITEMS, ParserInfo.KEY_PROVIDER);
//		dataGrid.setHeight(PgStudio.LEFT_PANEL_HEIGHT);

		Column<ParserInfo, ImageResource> icon = addColumn(new ImageResourceCell(), "", new GetValue<ImageResource>() {
			public ImageResource getValue(ParserInfo column) {

				return PgStudio.Images.parser();
			}
		}, null);

		addColumn(new TextCell(), "", new GetValue<String>() {
			public String getValue(ParserInfo column) {
				return column.getName();
			}
		}, null);

		dataGrid.setColumnWidth(icon, "35px");
		icon.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		dataProvider.addDataDisplay(dataGrid);

		dataGrid.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler((Handler) main.getSelectionChangeHandler());

		return dataGrid.asWidget();
	}
	
	private <C> Column<ParserInfo, C> addColumn(Cell<C> cell, String headerText,
		      final GetValue<C> getter, FieldUpdater<ParserInfo, C> fieldUpdater) {
		    Column<ParserInfo, C> column = new Column<ParserInfo, C>(cell) {
		      @Override
		      public C getValue(ParserInfo object) {
		        return getter.getValue(object);
		      }
		    };
		    column.setFieldUpdater(fieldUpdater);

		    dataGrid.addColumn(column, headerText);
		    return column;
	}
	
	@Override
	public void refresh() {
		dataProvider.setSchema(schema);
		
		if(isFirst){
			isFirst = false;
			mainPanel.add(getParserList());
			
			dataGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<ParserInfo>() {
				@Override
				public void onCellPreview(CellPreviewEvent<ParserInfo> event) {
					if (BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {
						if (dataGrid.getRowCount() == 1) {
							ParserInfo i = dataProvider.getList().get(0);

							if (dataGrid.getSelectionModel().isSelected(i)) {
								selectFirst();
							}
						}
					}
				}
			});
		}
		
		selectFirst();
	}

	@Override
	public Boolean selectFirst() {
		if (dataProvider != null) {
			if (!dataProvider.getList().isEmpty()) {
				ParserInfo i = dataProvider.getList().get(0);
				dataGrid.getSelectionModel().setSelected(i, true);
				main.setSelectedItem(i);
				return true;
			}
		}

		return false;
	}
}
