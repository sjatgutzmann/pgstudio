/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_OBJECT_TYPE;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.models.PolicyInfo;
import com.openscg.pgstudio.client.panels.popups.AddPolicyPopUp;
import com.openscg.pgstudio.client.panels.popups.DropItemObjectPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.panels.popups.RenameItemObjectPopUp;
import com.openscg.pgstudio.client.providers.PolicyListDataProvider;

public class PolicyPanel extends Composite implements DetailsPanel {

	private static interface GetValue<C> {
		C getValue(PolicyInfo column);
	}

	private ModelInfo item = null;

	private DataGrid<PolicyInfo> dataGrid;
	private PolicyListDataProvider dataProvider = new PolicyListDataProvider();

	private final SingleSelectionModel<PolicyInfo> selectionModel = new SingleSelectionModel<PolicyInfo>(PolicyInfo.KEY_PROVIDER);

	private String MAIN_HEIGHT = "400px";

	public static int ROWS_PER_PAGE = 5;
	public static int MAX_PRIVS = 1600;

	public void setItem(ModelInfo item) {
		this.item = item;

		dataProvider.setItem(item.getSchema(), item.getId(), item.getItemType());
	}

	public PolicyPanel() {

		VerticalPanel panel = new VerticalPanel();

		panel.add(getButtonBar());
		panel.add(getMainPanel());

		initWidget(panel);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();

		try {
			PushButton refresh = getRefreshButton();
			PushButton drop = getDropButton();
			;
			PushButton rename = getRenameButton();
			;
			PushButton create = getCreateButton();
			;

			bar.add(refresh);
			bar.add(drop);
			bar.add(rename);
			bar.add(create);
		} catch (Exception e) {
			Window.alert("Error: " + e.getMessage());
		}
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

	private PushButton getDropButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.drop()));
		button.setTitle("Drop Policy");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectionModel.getSelectedObject() != null && !"".equals(selectionModel.getSelectedObject().getName())) {

					DropItemObjectPopUp pop = new DropItemObjectPopUp();
					pop.setSelectionModel(selectionModel);
					pop.setDataProvider(dataProvider);
					pop.setItem(item);
					pop.setObject(selectionModel.getSelectedObject().getName());
					pop.setObjectType(ITEM_OBJECT_TYPE.POLICY);
					try {
						pop.getDialogBox();
					} catch (PopUpException caught) {
						Window.alert(caught.getMessage());
					}
				}
			}
		});
		return button;
	}

	private PushButton getRenameButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.rename()));
		button.setTitle("Rename Policy");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectionModel.getSelectedObject() != null && !"".equals(selectionModel.getSelectedObject().getName())) {

					RenameItemObjectPopUp pop = new RenameItemObjectPopUp();
					pop.setSelectionModel(selectionModel);
					pop.setDataProvider(dataProvider);
					pop.setItem(item);
					pop.setObjectType(ITEM_OBJECT_TYPE.POLICY);

					try {
						pop.getDialogBox();
					} catch (PopUpException caught) {
						Window.alert(caught.getMessage());
					}
				}
			}
		});
		return button;
	}

	private PushButton getCreateButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.create()));
		button.setTitle("Create Policy");

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					AddPolicyPopUp pop = new AddPolicyPopUp();
					pop.setSelectionModel(selectionModel);
					pop.setDataProvider(dataProvider);
					pop.setItem(item);
					pop.getDialogBox();
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});

		return button;
	}

	private Widget getMainPanel() {
		SimplePanel panel = new SimplePanel();
		panel.setWidth("100%");
		panel.setHeight("100%");

		dataGrid = new DataGrid<PolicyInfo>(MAX_PRIVS, PolicyInfo.KEY_PROVIDER);
		dataGrid.setWidth("100%");
		dataGrid.setHeight(MAIN_HEIGHT);

		Column<PolicyInfo, String> name = addColumn(new TextCell(), "Name", new GetValue<String>() {
			public String getValue(PolicyInfo info) {
				return info.getName();
			}
		}, null);

		Column<PolicyInfo, String> command = addColumn(new TextCell(), "Command", new GetValue<String>() {
			public String getValue(PolicyInfo info) {
				return info.getCommand();
			}
		}, null);

		Column<PolicyInfo, String> roles = addColumn(new TextCell(), "Roles", new GetValue<String>() {
			public String getValue(PolicyInfo info) {
				return info.getRoles();
			}
		}, null);

		Column<PolicyInfo, String> using = addColumn(new TextCell(), "Read Expression (Using)", new GetValue<String>() {
			public String getValue(PolicyInfo info) {
				return info.getUsing();
			}
		}, null);

		Column<PolicyInfo, String> withCheck = addColumn(new TextCell(), "Write Expression (With Check)", new GetValue<String>() {
			public String getValue(PolicyInfo info) {
				return info.getWithCheck();
			}
		}, null);

		dataGrid.setColumnWidth(name, "80px");
		dataGrid.setColumnWidth(command, "70px");
		dataGrid.setColumnWidth(roles, "90px");
		dataGrid.setColumnWidth(using, "180px");
		dataGrid.setColumnWidth(withCheck, "180px");

		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		command.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		roles.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		using.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		withCheck.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		dataGrid.setLoadingIndicator(new Image(PgStudio.Images.spinner()));

		dataProvider.addDataDisplay(dataGrid);

		dataGrid.setSelectionModel(selectionModel);

		panel.add(dataGrid);

		return panel.asWidget();
	}

	private <C> Column<PolicyInfo, C> addColumn(Cell<C> cell, String headerText, final GetValue<C> getter,
			FieldUpdater<PolicyInfo, C> fieldUpdater) {
		Column<PolicyInfo, C> column = new Column<PolicyInfo, C>(cell) {
			@Override
			public C getValue(PolicyInfo object) {
				return getter.getValue(object);
			}
		};
		column.setFieldUpdater(fieldUpdater);

		dataGrid.addColumn(column, headerText);
		return column;
	}

	@Override
	public void refresh() {
		dataProvider.setItem(item.getSchema(), item.getId(), item.getItemType());
	}
}
