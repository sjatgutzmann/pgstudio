/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.handlers.SelectionChangeHandler;
import com.openscg.pgstudio.client.messages.ListJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.panels.DetailsTabPanel;
import com.openscg.pgstudio.client.panels.MonitorPanel;
import com.openscg.pgstudio.client.panels.SQLWorksheet;
import com.openscg.pgstudio.client.panels.navigation.MenuStackPanel;
import com.openscg.pgstudio.client.panels.popups.AddSchemaPopUp;
import com.openscg.pgstudio.client.panels.popups.DropSchemaPopUp;
import com.openscg.pgstudio.client.panels.popups.LogoutPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.panels.popups.RenameSchemaPopUp;
import com.openscg.pgstudio.client.utils.ExtendedDialogBox;
import com.openscg.pgstudio.client.utils.SessionManager;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PgStudio implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	public static final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);
	SessionManager activity;

	public static Widget filler = new HTML("&nbsp;&nbsp;&nbsp;");

	public static final Resources Images =  GWT.create(Resources.class);

	public static final String LEFT_PANEL_HEIGHT = "237px";
	public static final String RIGHT_HEIGHT = "500px";
	public static final String RIGHT_PANEL_HEIGHT = "385px";

	public static final int MAX_PANEL_ITEMS = 10000;

	private TextBox textBox = new TextBox();

	final ListBox schemas = new ListBox();
	final ListBox databases = new ListBox();
	private ArrayList<DatabaseObjectInfo> schemaList = null;

	DialogBox dialogBox = null;

	public static DialogBox sqlDialog;
	public static DialogBox monitorDialog;

	public static enum DATABASE_OBJECT_TYPE implements IsSerializable {
		DATA_TYPE, FOREIGN_SERVER, SCHEMA, LANGUAGE, ROLE, DATABASE
	}

	public static enum ITEM_TYPE implements IsSerializable {

		TABLE, VIEW, FOREIGN_TABLE, FUNCTION, SEQUENCE, TYPE, MATERIALIZED_VIEW, FULL_TEXT_SEARCH, DICTIONARY, PARSER
	}

	public static enum ITEM_OBJECT_TYPE implements IsSerializable {
		COLUMN, INDEX, CONSTRAINT, TRIGGER, RULE, GRANT, SOURCE, STATS, POLICY
	}

	public static enum INDEX_TYPE implements IsSerializable {
		BTREE, HASH, GIST, SPGIST, GIN, BRIN 
	}

	public static enum CONSTRAINT_TYPE implements IsSerializable {
		CHECK, FOREIGN_KEY, PRIMARY_KEY, UNIQUE, EXCLUSION 
	}

	public static enum TYPE_FORM implements IsSerializable {
		COMPOSITE, ENUM, DOMAIN, RANGE 
	}

	public static final MenuStackPanel msp = new MenuStackPanel(new PgStudio());
	public static final DetailsTabPanel dtp = new DetailsTabPanel(new PgStudio());
	private Label detailsInfo = new Label();

	private static DatabaseObjectInfo selectedSchema = null;
	private static DatabaseObjectInfo selectedDatabase = null;
	private static ModelInfo selectedItem = null;

	private SelectionChangeHandler selectionChangeHandler; 

	public static void showWaitCursor() {
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
	}

	public static void showDefaultCursor() {
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
	}
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		activity = new SessionManager();
		RootPanel rp = RootPanel.get("mainPanelContainer");
		rp.setVisible(true);
		rp.add(getMainPanel());

		NodeList<Element> divs = RootPanel.getBodyElement().getElementsByTagName("div");

		for (int i = 0; i < divs.getLength(); i++) {
			if (divs.getItem(i).getId().equals("loadingWrapper")) {
				divs.getItem(i).removeFromParent();
				break;
			}
		}

		Window.addWindowClosingHandler(new Window.ClosingHandler() {

			@Override
			public void onWindowClosing(ClosingEvent event) {
				event.setMessage(null);
			}
		});

		Window.addCloseHandler(new CloseHandler<Window>() {

			@Override
			public void onClose(CloseEvent<Window> event) {
				activity.logout("WINDOW_CLOSE");
			}
		});
	}

	public final native static String getToken() /*-{ return $wnd.dbToken}-*/;
	private final native static String getDbVersion() /*-{ return $wnd.dbVersion}-*/;

	private Widget getMainPanel() {
		VerticalPanel fullPanel = new VerticalPanel();
		fullPanel.setStyleName("full-panel-container");

		fullPanel.add(getHeaderWidget());

		HorizontalPanel mainPanel = new HorizontalPanel();
		mainPanel.setStyleName("main-panel-container");

		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.setWidth("100%");
		leftPanel.add(getSchemaWidget());

		Image separator = new Image(Images.HorizontalSeparatorLine());
		separator.setWidth("95%");
		leftPanel.add(separator);

		leftPanel.add(msp.asWidget());

		VerticalPanel rightPanel = new VerticalPanel();
		rightPanel.setWidth("100%");
		HorizontalPanel rightInfoPanel = new HorizontalPanel();

		detailsInfo.setText("");
		detailsInfo.setStyleName("studio-Label");

		rightInfoPanel.add(detailsInfo);	
		rightPanel.add(rightInfoPanel);

		rightPanel.add(dtp.asWidget());

		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);

		mainPanel.setCellWidth(leftPanel, "20%");
		mainPanel.setCellWidth(rightPanel, "80%");

		fullPanel.add(mainPanel);

		return fullPanel.asWidget();
	}

	private Widget getHeaderWidget() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("100%");

		Image logo = new Image(Images.logo());
		panel.add(logo);

		VerticalPanel connDetails = new VerticalPanel();
		connDetails.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		connDetails.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);

		final HTML connInfo = new HTML();
		connInfo.setHTML("");
		connInfo.setStyleName("studio-Label-Small");
		connInfo.setHeight("45px");
		connDetails.add(connInfo);

		connDetails.add(getHeaderButtonBar());

		panel.add(connDetails);
		panel.setCellHorizontalAlignment(connDetails, HasHorizontalAlignment.ALIGN_RIGHT);

		studioService.getConnectionInfoMessage(PgStudio.getToken(), new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				connInfo.setText("");
				// Show the RPC error message to the user
				Window.alert(caught.getMessage());
			}

			public void onSuccess(String result) {
				String msg = "Connected to " + result.replace(" as ", "<br/> as ");
				connInfo.setHTML("<div>" + msg + "</div>");
			}
		});

		return panel.asWidget();
	}

	private Widget getHeaderButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		PushButton disconnect = getDisconnectButton();
		PushButton sql = getSQLWorksheetButton();
		PushButton monitor = getMonitorPanelButton();
		PushButton drop = getDropButton();
		PushButton rename = getRenameButton();
		PushButton create = getCreateButton();

		Label spacer = new Label("");
		spacer.setWidth("30px");
		Label spacer2 = new Label("");
		spacer2.setWidth("30px");

		bar.add(sql);
		bar.add(monitor);
		bar.add(spacer);
		bar.add(rename);
		bar.add(drop);
		bar.add(create);
		//bar.add(spacer2);
		bar.add(disconnect);

		return bar.asWidget();
	}

	private PushButton getDisconnectButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.disconnect()));
		button.setTitle("Disconnect");

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LogoutPopUp pop = new LogoutPopUp(activity);
				try {
					pop.getDialogBox();
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});

		return button;
	}

	private PushButton getSQLWorksheetButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.sqlWorksheet()));
		button.setTitle("SQL Worksheet");

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				sqlDialog = new ExtendedDialogBox();
				sqlDialog.setTitle("SQL Worksheet");

				SQLWorksheet sql = new SQLWorksheet();

				sqlDialog.setWidget(sql.asWidget());
				sqlDialog.setGlassEnabled(true);

				int left = (RootPanel.getBodyElement().getClientWidth() - sql.getWidth()) / 2; // Trying to center align with respect to the page
				left = left < 0 ? 0 : left;
				sqlDialog.setPopupPosition(left, 30);

				sqlDialog.setText("SQL Worksheet");
				sqlDialog.show();		    	  
				sqlDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

					/*Fire the event on SQL Worksheet close event to connect back to selected database in main window.ss  */	  
					@Override
					public void onClose(CloseEvent<PopupPanel> arg0) {
						connectToSeletectedDatabase();

					}
				});
				sql.setupCodePanel();				

			}			
		});

		return button;
	}

	private PushButton getMonitorPanelButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.monitor()));
		button.setTitle("Monitor");

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				monitorDialog = new ExtendedDialogBox();
				monitorDialog.setTitle("Monitor");

				MonitorPanel monitor = new MonitorPanel();

				monitorDialog.setWidget(monitor.asWidget());
				monitorDialog.setGlassEnabled(true);
				monitorDialog.setPopupPosition(30, 30);
				monitorDialog.setText("Monitor");
				monitorDialog.show();
			}
		});

		return button;
	}	

	private PushButton getDropButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.drop()));
		button.setTitle("Drop Schema");

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DropSchemaPopUp pop = new DropSchemaPopUp();	
				pop.setSchema(selectedSchema);
				try {
					DialogBox box = pop.getDialogBox();
					box.addCloseHandler(getSchemaPopupCloseHandler());
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});

		return button;
	}

	private PushButton getCreateButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.create()));
		button.setTitle("Create Schema");

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AddSchemaPopUp pop = new AddSchemaPopUp();				
				try {
					DialogBox box = pop.getDialogBox();
					box.addCloseHandler(getSchemaPopupCloseHandler());
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});

		return button;
	}

	private PushButton getRenameButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.rename()));
		button.setTitle("Rename Schema");

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RenameSchemaPopUp pop = new RenameSchemaPopUp();	
				pop.setSchema(selectedSchema);
				try {
					DialogBox box = pop.getDialogBox();
					box.addCloseHandler(getSchemaPopupCloseHandler());
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});

		return button;
	}

	private CloseHandler<PopupPanel> getSchemaPopupCloseHandler() {
		CloseHandler<PopupPanel> handler = new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				updateSchemaList();			}

		};

		return handler;
	}

	private SimplePanel getTitlePanel(){
		SimplePanel inputPanel = new SimplePanel();
		inputPanel.add(textBox);
		return inputPanel;
	}

	private Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();

		PushButton renameButton = new PushButton("Rename");
		PushButton cancelButton = new PushButton("Cancel");

		bar.add(renameButton);
		bar.add(cancelButton);

		renameButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedSchema != null
						&& !"".equals(selectedSchema)) {
					if(textBox.getText() != null && !textBox.getText().equals("")) {
						studioService.renameSchema(PgStudio.getToken(), selectedSchema.getName(), textBox.getText(), new AsyncCallback<String>() {
							public void onSuccess(String result) {
								if(result != null && result.contains("ERROR")){
									Window.alert(result);
								} else {
									textBox.setText("");
									updateSchemaList();
									dialogBox.hide(true);
								}
							}

							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}
						});
					} else {
						Window.alert("Enter schema name");
					}
				}
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				textBox.setText("");
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}

	private void connectToSeletectedDatabase(){

		//selectedItem = null;
		//Window.alert("databases.getSelectedIndex()>>"+databases.getSelectedIndex());	
		String name = databases.getItemText(databases.getSelectedIndex());
		int id = Integer.parseInt(databases.getValue(databases
				.getSelectedIndex()));
		DatabaseObjectInfo info = new DatabaseObjectInfo(id, name);
		//Window.alert("id, name:::"+id+","+ name);
		studioService.connectToDatabase(PgStudio.getToken(), name, new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				Window.alert(caught.getMessage());
			}

			public void onSuccess(Void arg0) {
				//Window.alert("Successfully connected to database.. Updating schema list");
				//RootPanel.get("connInfoDiv").set
				updateSchemaList();
			}

		});

		selectedDatabase = info;
	}
	private Widget getSchemaWidget() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(5);

		Label lbl = new Label();
		lbl.setText("Schema");
		lbl.setStyleName("studio-Label");

		schemas.setVisibleItemCount(1);
		schemas.setWidth("115px");
		schemas.setStyleName("roundList");


		schemas.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				selectedItem = null;

				String name = schemas.getItemText(schemas.getSelectedIndex());
				int id = Integer.parseInt(schemas.getValue(schemas
						.getSelectedIndex()));
				DatabaseObjectInfo info = new DatabaseObjectInfo(id, name);
				selectedSchema = info;
				dtp.onSchemaChange();
				msp.setSchema(info);
			}
		});

		panel.add(lbl);
		panel.add(filler);
		panel.add(schemas);

		schemaList = new ArrayList<DatabaseObjectInfo>();

		studioService.getList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.SCHEMA, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				schemas.clear();
				// Show the RPC error message to the user
				Window.alert(caught.getMessage());
			}

			public void onSuccess(String result) {
				schemas.clear();

				JsArray<ListJsObject> objects = DatabaseObjectInfo.json2Messages(result);

				int publicSchemaIndex = 0;
				for (int i = 0; i < objects.length(); i++) {
					DatabaseObjectInfo info = DatabaseObjectInfo.msgToInfo(objects.get(i));

					if (info.getName().equalsIgnoreCase("public"))
						publicSchemaIndex = i;

					schemas.addItem(info.getName(), Integer.toString(info.getId()));
					schemaList.add(info);
				}

				if (objects.length() > 0) {
					schemas.setSelectedIndex(publicSchemaIndex);
					selectedSchema = schemaList.get(publicSchemaIndex);
					msp.setSchema(schemaList.get(publicSchemaIndex));
				}
			}
		});

		return panel.asWidget();
	}

	private void updateSchemaList(){
		studioService.getList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.SCHEMA, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				schemas.clear();
				// Show the RPC error message to the user
				Window.alert(caught.getMessage());
			}

			public void onSuccess(String result) {
				schemas.clear();

				JsArray<ListJsObject> objects = DatabaseObjectInfo.json2Messages(result);

				schemaList = new ArrayList<DatabaseObjectInfo>();	
				int selectedSchemaIndex = 0;
				for (int i = 0; i < objects.length(); i++) {
					DatabaseObjectInfo info = DatabaseObjectInfo.msgToInfo(objects.get(i));

					if (info.getName().equals(selectedSchema))
						selectedSchemaIndex = i;

					schemas.addItem(info.getName(), Integer.toString(info.getId()));
					schemaList.add(info);
				}

				if (objects.length() > 0) {
					schemas.setSelectedIndex(selectedSchemaIndex);
					selectedSchema = schemaList.get(selectedSchemaIndex);
					msp.setSchema(schemaList.get(selectedSchemaIndex));
				}              
			}
		});
	}

	public void setSelectedItem(ModelInfo selected) {
		selectedItem = selected;

		String prefix = "";
		switch (selected.getItemType()) {
		case TABLE:
			prefix = "Table";
			break;
		case VIEW:
			prefix = "View";
			break;
		case FOREIGN_TABLE:
			prefix = "Foreign Table";
			break;
		case FUNCTION:
			prefix = "Function";
			break;
		case SEQUENCE:
			prefix = "Sequence";
			break;
		case TYPE:
			prefix = "Type";
			break;
		case FULL_TEXT_SEARCH:
			prefix = "Full Text Search";
			break;
		case DICTIONARY:
			prefix = "Dictionary";
		default:
			prefix = "";
			break;
		}

		detailsInfo.setText(prefix + ": " + selectedSchema.getName() + "." + selected.getName());

		dtp.setSelectedItem(selected);
	}

	public SelectionChangeHandler getSelectionChangeHandler() {
		if (selectionChangeHandler == null) {
			selectionChangeHandler = new SelectionChangeHandler(this);
		}

		return selectionChangeHandler;
	}

	public static DatabaseObjectInfo getSelectedSchema() {
		return selectedSchema;
	}

	public ModelInfo getSelectedItem() {
		return selectedItem;
	}

	public int getDatabaseVersion() {
		if (getDbVersion() != null)
			return Integer.parseInt(getDbVersion());

		return 0;
	}

	public void clearTabs(){

		dtp.clearTabs();
	}


}
