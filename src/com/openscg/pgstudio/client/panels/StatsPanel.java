/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.models.StatsInfo;
import com.openscg.pgstudio.client.providers.StatsListDataProvider;

public class StatsPanel extends Composite implements DetailsPanel {

	private static String MAIN_HEIGHT = "450px";

	private DataGrid<StatsInfo> dataGrid;
    private StatsListDataProvider dataProvider = new StatsListDataProvider();
 	private ModelInfo item;

	private static interface GetValue<C> {
	    C getValue(StatsInfo column);
	  }

	public void setItem(ModelInfo item) {
		this.item = item;
		dataProvider.setItem(item.getId(), item.getItemType());		
	}
	
	public StatsPanel() {
		
		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());
				
		initWidget(mainPanel);
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
	
	private Widget getMainPanel() {
		SimplePanel panel = new SimplePanel();
		panel.setWidth("100%");
		panel.setHeight(MAIN_HEIGHT);
		
		dataGrid = new DataGrid<StatsInfo>(25, StatsInfo.KEY_PROVIDER);
		dataGrid.setHeight(MAIN_HEIGHT);
		
		Column<StatsInfo, String> name = addNameColumn(new TextCell(), "Name", new GetValue<String>() {
	        public String getValue(StatsInfo stat) {
	          return stat.getName();
	        }
	      }, null);

		Column<StatsInfo, String> value = addNameColumn(new TextCell(), "Value", new GetValue<String>() {
	        public String getValue(StatsInfo stat) {
	          return stat.getValue();
	        }
	      }, null);

		dataGrid.setLoadingIndicator(new Image(PgStudio.Images.spinner()));
		dataProvider.addDataDisplay(dataGrid);

		panel.add(dataGrid);
		
		return panel.asWidget();
	}	
	
	private <C> Column<StatsInfo, C> addNameColumn(Cell<C> cell, String headerText,
		      final GetValue<C> getter, FieldUpdater<StatsInfo, C> fieldUpdater) {
		    Column<StatsInfo, C> column = new Column<StatsInfo, C>(cell) {
		      @Override
		      public C getValue(StatsInfo object) {
		        return getter.getValue(object);
		      }
		    };
		    column.setFieldUpdater(fieldUpdater);

		    dataGrid.addColumn(column, headerText);
		    return column;
	}

	@Override
	public void refresh() {
		setItem(item);		
	}

}
