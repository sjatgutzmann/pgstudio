/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.navigation;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.Resources;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.FTSConfigurationInfo;
import com.openscg.pgstudio.client.panels.popups.AddFTSConfigPopUp;
import com.openscg.pgstudio.client.panels.popups.AlterFTSConfigPopUp;
import com.openscg.pgstudio.client.panels.popups.DropFTSConfigPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.providers.FTSMenuDataProvider;


public class FTSMenuPanel extends Composite implements MenuPanel {

	private static final Resources Images =  GWT.create(Resources.class);

	private DatabaseObjectInfo schema = null;
	private FTSMenuDataProvider dataProvider = new FTSMenuDataProvider();


	private final SingleSelectionModel<FTSConfigurationInfo> selectionModel = new SingleSelectionModel<FTSConfigurationInfo>(FTSConfigurationInfo.KEY_PROVIDER);
	private DataGrid<FTSConfigurationInfo> dataGrid;
	private final PgStudio main;

	private static interface GetValue<C> {
		C getValue(FTSConfigurationInfo object);
	}

	VerticalPanel panel = new VerticalPanel();
	VerticalPanel ftsPanel = new VerticalPanel();


	public FTSMenuPanel(PgStudio main) {
		this.main = main;
		panel.setWidth("95%");
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(getButtonBar());
		panel.add(getFTSTabPanel());
		initWidget(panel);
	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
		dataProvider.setSchema(schema);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		PushButton refresh = getRefreshButton();
		PushButton create = getCreateButton();
		PushButton drop = getDropButton();
		PushButton alter = getAlterButton();
		bar.add(refresh);
		bar.add(alter);
		bar.add(drop);
		bar.add(create);
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

	private PushButton getCreateButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.create()));
		button.setTitle("Create");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AddFTSConfigPopUp pop = new AddFTSConfigPopUp();
				pop.setSelectionModel(selectionModel);
				pop.setDataProvider(dataProvider);
				try {
					pop.getDialogBox();
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});

		return button;
	}

	private PushButton getDropButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.drop()));
		button.setTitle("Drop");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DropFTSConfigPopUp pop = new DropFTSConfigPopUp();
				pop.setSelectionModel(selectionModel);
				pop.setItem(selectionModel.getSelectedObject());
				pop.setSchema(PgStudio.getSelectedSchema());
				pop.setConfigName(selectionModel.getSelectedObject().getConfigName());
				try {
					pop.getDialogBox();
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});
		return button;
	}

	private PushButton getAlterButton() {
		PushButton button = new PushButton(new Image(Images.edit()));
		button.setTitle("Alter");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlterFTSConfigPopUp pop = new AlterFTSConfigPopUp();
				pop.setSelectionModel(selectionModel);
				pop.setDataProvider(dataProvider);
				pop.setItem(selectionModel.getSelectedObject());
				try {
					pop.getDialogBox();
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});
		return button;
	}

	private Widget getFTSTabPanel() {
		dataGrid = new DataGrid<FTSConfigurationInfo>(PgStudio.MAX_PANEL_ITEMS, FTSConfigurationInfo.KEY_PROVIDER);
		dataGrid.setHeight(PgStudio.LEFT_PANEL_HEIGHT);
		dataGrid.setLoadingIndicator(new Image(Images.spinner()));

		Column<FTSConfigurationInfo, ImageResource> icon = addColumn(new ImageResourceCell(), "", new GetValue<ImageResource>() {
			public ImageResource getValue(FTSConfigurationInfo column) {
				return Images.ftsconf();
			}
		}, null);

		addColumn(new TextCell(), "", new GetValue<String>() {
			public String getValue(FTSConfigurationInfo column) {
				return column.getConfigName();
			}
		}, null);
		dataGrid.setColumnWidth(icon, "35px");
		icon.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dataProvider.addDataDisplay(dataGrid);
		dataGrid.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler((Handler) main.getSelectionChangeHandler());
		return dataGrid.asWidget();
	}

	private <C> Column<FTSConfigurationInfo, C> addColumn(Cell<C> cell, String headerText,
			final GetValue<C> getter, FieldUpdater<FTSConfigurationInfo, C> fieldUpdater) {
		Column<FTSConfigurationInfo, C> column = new Column<FTSConfigurationInfo, C>(cell) {
			@Override
			public C getValue(FTSConfigurationInfo object) {
				return getter.getValue(object);
			}
		};
		column.setFieldUpdater(fieldUpdater);
		dataGrid.addColumn(column, headerText);
		return column;
	}

	public void refresh() {
		dataProvider.setSchema(schema);	
		selectFirst();
	}

	@Override
	public Boolean selectFirst() {
		if (dataProvider != null) {
			if (!dataProvider.getFTSConfigurationList().isEmpty()) {
				FTSConfigurationInfo t = dataProvider.getFTSConfigurationList().get(0);
				dataGrid.getSelectionModel().setSelected(t, true);
				main.setSelectedItem(t);
				return true;
			}
		}
		return false;
	}
}
