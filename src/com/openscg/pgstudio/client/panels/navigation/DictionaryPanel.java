/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.navigation;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.BrowserEvents;
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
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.DictionaryInfo;
import com.openscg.pgstudio.client.panels.popups.AddDictionaryPopUp;
import com.openscg.pgstudio.client.panels.popups.DropItemPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.providers.DictionaryListDataProvider;

public class DictionaryPanel extends Composite implements MenuPanel {

	private PgStudio main;
	private DatabaseObjectInfo schema;
	private DictionaryListDataProvider dataProvider = new DictionaryListDataProvider();
	private DataGrid<DictionaryInfo> dataGrid;
	private VerticalPanel mainPanel;
	private final SingleSelectionModel<DictionaryInfo> selectionModel = 
		    	new SingleSelectionModel<DictionaryInfo>(DictionaryInfo.KEY_PROVIDER);
	
	private static interface GetValue<C> {
		C getValue(DictionaryInfo object);
	}

	public DictionaryPanel(PgStudio main) {
		this.main = main;
		mainPanel = new VerticalPanel();
		mainPanel.setWidth("95%");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.add(getButtonBar());
		mainPanel.add(getDictionaryList());
		
		dataGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<DictionaryInfo>() {
			@Override
			public void onCellPreview(CellPreviewEvent<DictionaryInfo> event) {
				if (BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {
					if (dataGrid.getRowCount() == 1) {
						DictionaryInfo i = dataProvider.getList().get(0);
						if (dataGrid.getSelectionModel().isSelected(i)) {
							selectFirst();
						}
					}
				}
			}
		});

		initWidget(mainPanel);
	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
		dataProvider.setSchema(schema);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		PushButton refresh = getRefreshButton();
		PushButton drop = getDropButton();
		PushButton add = getAddButton();
		bar.add(refresh);
		bar.add(drop);
		bar.add(add);
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

	private PushButton getAddButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.create()));
		button.setTitle("Create Dictionary");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AddDictionaryPopUp popUp = new AddDictionaryPopUp();
				popUp.setSelectionModel(selectionModel);
				popUp.setDataProvider(dataProvider);
				popUp.setSchema(schema);
				try {
					popUp.getDialogBox();
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});

		return button;
	}

	private PushButton getDropButton() {
		PushButton dropButton = new PushButton(new Image(PgStudio.Images.drop()));
		dropButton.setTitle("Drop");
		dropButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				if(selectionModel.getSelectedObject() != null && !"".equals(selectionModel.getSelectedObject().getName())){
					DropItemPopUp pop = new DropItemPopUp();
					pop.setSelectionModel(selectionModel);
					pop.setDataProvider(dataProvider);
					pop.setSchema(schema);
					pop.setItemType(ITEM_TYPE.DICTIONARY);
					pop.setItem(selectionModel.getSelectedObject().getName());
					try {
						pop.getDialogBox();
					} catch (PopUpException caught) {
						Window.alert(caught.getMessage());
					}
				}
			}
		});

		return dropButton;
	}

	private Widget getDictionaryList() {
		dataGrid = new DataGrid<DictionaryInfo>(PgStudio.MAX_PANEL_ITEMS, DictionaryInfo.KEY_PROVIDER);
		dataGrid.setHeight(PgStudio.LEFT_PANEL_HEIGHT);

		Column<DictionaryInfo, ImageResource> icon = addColumn(new ImageResourceCell(), "", new GetValue<ImageResource>() {
			public ImageResource getValue(DictionaryInfo column) {

				return PgStudio.Images.enumuration();
			}
		}, null);

		Column<DictionaryInfo, String> typeName = addColumn(new TextCell(), "", new GetValue<String>() {
			public String getValue(DictionaryInfo column) {
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
	
	private <C> Column<DictionaryInfo, C> addColumn(Cell<C> cell, String headerText,
		      final GetValue<C> getter, FieldUpdater<DictionaryInfo, C> fieldUpdater) {
		    Column<DictionaryInfo, C> column = new Column<DictionaryInfo, C>(cell) {
		      @Override
		      public C getValue(DictionaryInfo object) {
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
		selectFirst();
	}

	@Override
	public Boolean selectFirst() {
		if (dataProvider != null) {
			if (!dataProvider.getList().isEmpty()) {
				DictionaryInfo i = dataProvider.getList().get(0);
				dataGrid.getSelectionModel().setSelected(i, true);
				main.setSelectedItem(i);
				return true;
			}
		}

		return false;
	}

	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{ 
	return eval(json); 
	}-*/;
}
