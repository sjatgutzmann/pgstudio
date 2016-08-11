package com.openscg.pgstudio.client.panels;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.FTSConfigurationDetailsInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.panels.popups.AddFTSMappingPopUp;
import com.openscg.pgstudio.client.panels.popups.AlterFTSMappingPopUp;
import com.openscg.pgstudio.client.panels.popups.DropFTSMappingPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.providers.FTSConfigurationDetailsDataProvider;

public class FTSConfigurationDetailsPanel extends Composite implements DetailsPanel {

	private static interface GetValue<C> {
		C getValue(FTSConfigurationDetailsInfo column);
	}

	private final PgStudio main;
	private VerticalPanel mainPanel = new VerticalPanel();
	private SimplePanel configMappingsDisplayPanel = new SimplePanel();
	private SimplePanel noConfigMappingsPanel = new SimplePanel();

	private DataGrid<FTSConfigurationDetailsInfo> dataGrid;

	private FTSConfigurationDetailsInfo item;
	private FTSConfigurationDetailsDataProvider dataProvider = new FTSConfigurationDetailsDataProvider();
	private final SingleSelectionModel<FTSConfigurationDetailsInfo> selectionModel = 
			new SingleSelectionModel<FTSConfigurationDetailsInfo>(FTSConfigurationDetailsInfo.KEY_PROVIDER);

	public static int ROWS_PER_PAGE = 10;

	public static int MAX_COLUMNS = 10;

	private String TEXT_WIDTH = "300px";

	private String MAIN_HEIGHT = "300px";

	@Override
	public void setItem(ModelInfo item) {
		if (item == null)
			return;
		FTSConfigurationDetailsInfo ftsItem  = new FTSConfigurationDetailsInfo(item.getName());
		this.item = ftsItem;
		dataProvider.setItem(ftsItem);
	}

	@Override
	public void refresh() {
		dataProvider.setItem(this.item);
	}

	public FTSConfigurationDetailsPanel(PgStudio main) {
		this.main = main;
		mainPanel = new VerticalPanel();
		mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());
		initWidget(mainPanel);
	}

	private Widget getButtonBar() {

		HorizontalPanel bar = new HorizontalPanel();
		PushButton refresh = getRefreshButton();
		PushButton alter = getAlterButton();
		PushButton drop = getDropButton();
		PushButton create = getCreateButton();
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
				AddFTSMappingPopUp pop = new AddFTSMappingPopUp();
				pop.setSelectionModel(selectionModel);
				pop.setDataProvider(dataProvider);
				pop.setItem(item);
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
				DropFTSMappingPopUp pop = new DropFTSMappingPopUp();
				pop.setSelectionModel(selectionModel);
				pop.setDataProvider(dataProvider);
				pop.setSchema(PgStudio.getSelectedSchema());
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

	private PushButton getAlterButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.edit()));
		button.setTitle("Alter");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlterFTSMappingPopUp pop = new AlterFTSMappingPopUp();
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

	private PushButton getConfigurationButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.refresh()));
		button.setTitle("Configurations");
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();				
			}
		});
		return button;
	}

	private PushButton getDictionaryButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.refresh()));
		button.setTitle("Dictionary");
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();				
			}
		});
		return button;
	}

	private PushButton getParserButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.refresh()));
		button.setTitle("Parser");
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();				
			}
		});
		return button;
	}

	private Widget getMainPanel() {
		configMappingsDisplayPanel = new SimplePanel();
		configMappingsDisplayPanel.setWidth("100%");
		configMappingsDisplayPanel.setHeight("100%");
		dataGrid = new DataGrid<FTSConfigurationDetailsInfo>(MAX_COLUMNS, FTSConfigurationDetailsInfo.KEY_PROVIDER);
		dataGrid.setHeight(MAIN_HEIGHT);

		Column<FTSConfigurationDetailsInfo, String> tokenNameCol = addColumn(new TextCell(), "Mapping", new GetValue<String>() {
			public String getValue(FTSConfigurationDetailsInfo column) {
				return column.getToken();
			}
		}, null);

		Column<FTSConfigurationDetailsInfo, String> dictNameCol = addColumn(new TextCell(), "Dictionaries", new GetValue<String>() {
			public String getValue(FTSConfigurationDetailsInfo column) {
				return column.getDictionaries();
			}
		}, null);

		Column<FTSConfigurationDetailsInfo, String> commentCol = addColumn(new TextCell(), "Comment", new GetValue<String>() {
			public String getValue(FTSConfigurationDetailsInfo column) {
				return column.getComment();
			}
		}, null);
		dataGrid.setColumnWidth(tokenNameCol, "100px");
		dataGrid.setColumnWidth(dictNameCol, "100px");
		dataGrid.setColumnWidth(commentCol, "70px");

		dataGrid.setLoadingIndicator(new Image(PgStudio.Images.spinner()));
		dataProvider.addDataDisplay(dataGrid);
		configMappingsDisplayPanel.add(dataGrid);
		dataGrid.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler((new 
				SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				FTSConfigurationDetailsInfo index = selectionModel.getSelectedObject();
				dataProvider.setItem(index);
			}		
		}));
		return configMappingsDisplayPanel.asWidget();
	}

	private <C> Column<FTSConfigurationDetailsInfo, C> addColumn(Cell<C> cell, String headerText,
			final GetValue<C> getter, FieldUpdater<FTSConfigurationDetailsInfo, C> fieldUpdater) {
		Column<FTSConfigurationDetailsInfo, C> column = new Column<FTSConfigurationDetailsInfo, C>(cell) {
			@Override
			public C getValue(FTSConfigurationDetailsInfo object) {
				return getter.getValue(object);
			}
		};
		column.setFieldUpdater(fieldUpdater);
		dataGrid.addColumn(column, headerText);
		return column;
	}

	private Widget getNoConfigMessage() {
		noConfigMappingsPanel = new SimplePanel();
		HTML msgContent = new HTML("<h4>Empty FTS configuration map.</h4>");
		noConfigMappingsPanel.add(msgContent); 
		return noConfigMappingsPanel.asWidget();
	}

	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{ 
	  	return eval(json); 
	}-*/;
}
