/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.models.ActivityInfo;
import com.openscg.pgstudio.client.providers.ActivityListDataProvider;

public class MonitorPanel extends Composite {
	
	private static interface GetValue<C> {
	    C getValue(ActivityInfo column);
	}

	private DataGrid<ActivityInfo> dataGrid;
    private ActivityListDataProvider dataProvider = new ActivityListDataProvider();

	public static int ROWS_PER_PAGE = 5;
	public static int MAX_INDEXES = 1600;
	
	private int refreshRate = 5000;
	
	private String REFRESH_RATE = "Refresh Rate";

	private Timer t;
	
	public MonitorPanel() {
		
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("1200px");
		panel.setHeight("600px");


		HorizontalPanel bar = new HorizontalPanel();
		bar.setWidth("100%");
		Widget bb = getButtonBar();
		Widget rb = getRefreshRateBar();
		
		bar.add(bb);
		bar.add(rb);
		
		bar.setCellHorizontalAlignment(bb, HasHorizontalAlignment.ALIGN_LEFT);
		bar.setCellHorizontalAlignment(rb, HasHorizontalAlignment.ALIGN_RIGHT);
				
		panel.add(bar);
		panel.add(getMainPanel());
				
		initWidget(panel);
		
		t = new Timer() {
			@Override
			public void run() {
				dataProvider.refresh();
			}
			
		};
		
		t.scheduleRepeating(refreshRate);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		
		PushButton refresh = getRefreshButton();
		
		bar.add(refresh);
		
		return bar.asWidget();
	}
	
	private Widget getRefreshRateBar() {
		HorizontalPanel bar = new HorizontalPanel();

		Label lbl = new Label();
		lbl.setText(REFRESH_RATE);
		lbl.setStyleName("studio-Label-Small");

		final ListBox refreshRateBox = new ListBox();
		refreshRateBox.setStyleName("roundList");
		refreshRateBox.addItem("1");
		refreshRateBox.addItem("2");
		refreshRateBox.addItem("5");
		refreshRateBox.addItem("10");
		refreshRateBox.addItem("15");
		refreshRateBox.addItem("30");

		refreshRateBox.setSelectedIndex(2);

		refreshRateBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				refreshRate = Integer.parseInt(refreshRateBox
						.getItemText(refreshRateBox.getSelectedIndex())) * 1000;
				
				t.scheduleRepeating(refreshRate);
			}
		});

		bar.add(lbl);
		bar.add(refreshRateBox);

		bar.setCellVerticalAlignment(lbl, HasVerticalAlignment.ALIGN_MIDDLE);
		bar.setCellVerticalAlignment(refreshRateBox,
				HasVerticalAlignment.ALIGN_MIDDLE);

		return bar.asWidget();
	}

	private PushButton getRefreshButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.refresh()));
		button.setTitle("Refresh");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dataProvider.refresh();				
			}			
		});
		return button;
	}
	

	private Widget getMainPanel() {
		SimplePanel panel = new SimplePanel();
		panel.setWidth("100%");
		panel.setHeight("100%");

		
		dataGrid = new DataGrid<ActivityInfo>(MAX_INDEXES, ActivityInfo.KEY_PROVIDER);
		dataGrid.setHeight("500px");
		dataGrid.setWidth("1150px");
		
		Column<ActivityInfo, String> pid = addColumn(new TextCell(), "PID", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return Integer.toString(activity.getPid());
	        }
	      }, null);

		Column<ActivityInfo, String> databaseName = addColumn(new TextCell(), "Database Name", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return activity.getDatName();
	        }
	      }, null);

		Column<ActivityInfo, String> userName = addColumn(new TextCell(), "User Name", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return activity.getUserName();
	        }
	      }, null);

		Column<ActivityInfo, String> state = addColumn(new TextCell(), "State", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return activity.getState();
	        }
	      }, null);

		Column<ActivityInfo, String> clientAddr = addColumn(new TextCell(), "Client Address", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return activity.getClientAddr();
	        }
	      }, null);

		Column<ActivityInfo, String> backendStart = addColumn(new TextCell(), "Backend Start", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return activity.getBackendStart();
	        }
	      }, null);

		Column<ActivityInfo, String> xactStart = addColumn(new TextCell(), "Transaction Start", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return activity.getXactStart();
	        }
	      }, null);

		Column<ActivityInfo, String> queryTime = addColumn(new TextCell(), "Query Time", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return activity.getQueryTime();
	        }
	      }, null);

		Column<ActivityInfo, String> query = addColumn(new TextCell(), "Query", new GetValue<String>() {
	        public String getValue(ActivityInfo activity) {
	          return activity.getQuery();
	        }
	      }, null);

		dataGrid.setColumnWidth(pid, "70px");
		dataGrid.setColumnWidth(databaseName, "120px");
		dataGrid.setColumnWidth(userName, "100px");
		dataGrid.setColumnWidth(clientAddr, "100px");
		dataGrid.setColumnWidth(state, "100px");
		dataGrid.setColumnWidth(backendStart, "200px");
		dataGrid.setColumnWidth(xactStart, "200px");
		dataGrid.setColumnWidth(queryTime, "150px");
		dataGrid.setColumnWidth(query, "2000px");
		

		dataGrid.setLoadingIndicator(new Image(PgStudio.Images.spinner()));

		dataProvider.addDataDisplay(dataGrid);

		
		panel.add(dataGrid);
		
		return panel.asWidget();
	}
	

	private <C> Column<ActivityInfo, C> addColumn(Cell<C> cell, String headerText,
		      final GetValue<C> getter, FieldUpdater<ActivityInfo, C> fieldUpdater) {
		    Column<ActivityInfo, C> column = new Column<ActivityInfo, C>(cell) {
		      @Override
		      public C getValue(ActivityInfo object) {
		        return getter.getValue(object);
		      }
		    };
		    column.setFieldUpdater(fieldUpdater);

		    dataGrid.addColumn(column, headerText);
		    return column;
	}

}
