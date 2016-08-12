/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import java.util.Map;

import org.gwt.advanced.client.ui.widget.SimpleGrid;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.models.TypeInfo;
import com.openscg.pgstudio.client.models.TypeInfo.TYPE_KIND;
import com.openscg.pgstudio.client.panels.popups.AlterDomainPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.panels.popups.ResultsPopUp;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;
import com.openscg.pgstudio.shared.dto.DomainDetails;

public class TypeDataPanel extends Composite implements DetailsPanel {
	private final static String WARNING_MSG = 
			"This will permanently delete this object. Are you sure you want to continue?";
	private VerticalPanel mainPanel;
	//private VerticalPanel panel;
	private DomainDetails details;
	private SimpleGrid mainGrid;
	private SimpleGrid constraintsGrid;

	private ITEM_TYPE type = null;
	private ModelInfo item;

	final DialogBox createCheckDialogBox = new DialogBox();
	private TextBox checkName = null;
	private TextBox definition = null;
	
	private DialogBox confirmBox;
	
	PushButton addCheckButton;
	PushButton alterButton;
	
	Label detailsLbl = new Label("Details");
	Label constraintsLbl = new Label("Constraints");
	boolean isDomain;
	
	public void setItem(ModelInfo item) {
		this.item = item;
		TypeInfo typeInfo = (TypeInfo) item;
		isDomain = typeInfo.getKind() == TYPE_KIND.DOMAIN;
		
		try {
			PgStudio.studioService.fetchDomainDetails(PgStudio.getToken(), item.getId(),
					new AsyncCallback<DomainDetails>() {
						@Override
						public void onFailure(Throwable caught) {
							ResultsPopUp pop = new ResultsPopUp("Error", caught.getMessage());
							try {
								pop.getDialogBox();
							} catch (PopUpException ex) {
								Window.alert(ex.getMessage());
							}
						}

						@Override
						public void onSuccess(DomainDetails result) {							
							setupData(result);
							details = result;
						}

					});
		} catch (

		DatabaseConnectionException e) {
		} catch (PostgreSQLException e) {
		}
	}

	public TypeDataPanel() {

		mainPanel = new VerticalPanel();

		mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());

		initWidget(mainPanel);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();

		PushButton refresh = getRefreshButton();

		createAddCheckButton();

		createAlterButton();

		bar.add(refresh);
		bar.add(addCheckButton);
		bar.add(alterButton);

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

	private void createAddCheckButton() {		
		addCheckButton = new PushButton(new Image(PgStudio.Images.create()));
		addCheckButton.setTitle("Add Check");
		addCheckButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createCheckDialogBox.setWidget(getCreateCheckPanel());

				createCheckDialogBox.setGlassEnabled(true);
				createCheckDialogBox.center();
			}
		});
	}

	private VerticalPanel getCreateCheckPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lbl.setText("Add Check");

		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblName.setText("Name");
		lblName.setWidth("63px");

		checkName = new TextBox();
		checkName.setWidth("155px");

		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(checkName);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		Label lblDef = new Label();
		lblDef.setStyleName("StudioPopup-Msg");
		lblDef.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		lblDef.setText("Definition");

		definition = new TextBox();
		definition.setWidth("155px");

		HorizontalPanel defPanel = new HorizontalPanel();
		defPanel.setSpacing(10);
		defPanel.add(lblDef);
		defPanel.add(definition);
		defPanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		info.add(lbl);
		info.add(namePanel);
		info.add(defPanel);

		panel.add(info);

		Widget buttonBar = getButtonPanel();

		panel.add(buttonBar);

		return panel;
	}

	private Widget getButtonPanel() {
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);

		Button addButton = new Button("Add");
		Button cancelButton = new Button("Cancel");

		bar.add(addButton);
		bar.add(cancelButton);

		bar.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (definition.getText() != null && !definition.getText().equals("")) {

					try {
						PgStudio.studioService.addCheck(PgStudio.getToken(), item.getSchema(), item.getName(),
								checkName.getText(), definition.getText(), new AsyncCallback<String>() {
									public void onFailure(Throwable caught) {
										ResultsPopUp pop = new ResultsPopUp("Error", caught.getMessage());
										try {
											pop.getDialogBox();
										} catch (PopUpException ex) {
											Window.alert(ex.getMessage());
										}
									}

									public void onSuccess(String result) {
										JsArray<CommandJsObject> jArray = json2Messages(result);

										CommandJsObject res = jArray.get(0);
										if (res != null && res.getStatus() != null
												&& res.getStatus()
														.contains("ERROR")) {
											
											ResultsPopUp pop = new ResultsPopUp(
													"Error", res.getMessage());
											try {
												pop.getDialogBox();
											} catch (PopUpException caught) {
												Window.alert(caught.getMessage());
											}
										}else {
											createCheckDialogBox.hide(true);
											refresh();
										}
									}
								});
					} catch (DatabaseConnectionException | PostgreSQLException e) {
						Window.alert(e.getMessage());
					}

				} else {
					ResultsPopUp pop = new ResultsPopUp("Error", "Check constraint needs a Definition.");
					try {
						pop.getDialogBox();
					} catch (PopUpException ex) {
						Window.alert(ex.getMessage());
					}
				}
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createCheckDialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}

	private void createAlterButton() {
		alterButton = new PushButton(new Image(PgStudio.Images.edit()));
		alterButton.setTitle("Alter");
		alterButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AlterDomainPopUp popUp = new AlterDomainPopUp();
				popUp.setTypeInfo((TypeInfo) item);
				popUp.setName(item.getFullName());
				popUp.setNotNull(details.getNotNull());
				popUp.setDefaultValue(details.getDefaultValue());
				try {
					popUp.getDialogBox();
				} catch (PopUpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private Widget getMainPanel() {
		VerticalPanel panel = new VerticalPanel();
		mainGrid = new SimpleGrid();
		constraintsGrid = new SimpleGrid();

		mainGrid.setWidth("100%");
		constraintsGrid.setWidth("100%");

		mainGrid.setWidget(1, 0, new Label("Name"));
		
		if(isDomain) {
			mainGrid.setWidget(2, 0, new Label("Not Null"));
			mainGrid.setWidget(3, 0, new Label("Default Value"));
		}
		panel.add(detailsLbl);
		panel.add(mainGrid);
		panel.add(constraintsLbl);
		panel.add(constraintsGrid);

		mainGrid.setStyleName("StudioOverflow main-data");
		constraintsGrid.setStyleName("StudioOverflow main-data");
		detailsLbl.setStyleName("main-data label");
		constraintsLbl.setStyleName("main-data label level2");
		panel.setSpacing(10);

		return panel.asWidget();
	}

	@Override
	public void refresh() {
		setItem(item);
	}

	private void setupData(DomainDetails details) {
		mainPanel.remove(1);
		mainPanel.add(getMainPanel());
		mainGrid.setWidget(1, 1, new Label(item.getName()));
		disableNonDomainView(isDomain);
		if (isDomain) {			
			mainGrid.setWidget(2, 1, new Label(String.valueOf(details.getNotNull())));
			mainGrid.setWidget(3, 1, new Label(details.getDefaultValue()));		
			
			Map<String, String> map = details.getConstraintMap();
			if (map != null && !map.isEmpty()) {
				int row = 0;
				for (String key : map.keySet()) {
					String value = map.get(key);
					constraintsGrid.setWidget(row, 0, new Label(key));
					constraintsGrid.setWidget(row, 1, new Label(value));
					PushButton drop = getDropButton();
					drop.getElement().setId(key);
					constraintsGrid.setWidget(row, 2, drop);
					row++;
				}
			}
			}
	}

	private PushButton getDropButton() {
		
		PushButton drop = new PushButton(new Image(PgStudio.Images.drop()));
		drop.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				confirmBox = new DialogBox();				
				String constraintName = ((PushButton) event.getSource()).getElement().getId();
				
				try {
					confirmBox.setWidget(getPanel(constraintName));
					confirmBox.setGlassEnabled(true);
					confirmBox.center();
					
				} catch (PopUpException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					Window.alert("Error " + e1.getMessage());
				}
				
			}
		});
		return drop;
	}
	private VerticalPanel getPanel(String constraintName) throws PopUpException{
		VerticalPanel panel = new VerticalPanel();
		
		try {
			panel.setStyleName("StudioPopup");
	
			VerticalPanel info = new VerticalPanel();
			info.setSpacing(10);
			
			Label lbl = new Label();
			lbl.setStyleName("StudioPopup-Msg-Strong");
			lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	
			
			String title = "DROP CONSTRAINT " + constraintName;
			lbl.setText(title);
			
			HorizontalPanel warningPanel = new HorizontalPanel();
			Image icon = new Image(PgStudio.Images.warning());
			icon.setWidth("110px");
			
			VerticalPanel detailPanel = new VerticalPanel();
			
			Label lblWarning = new Label();
			lblWarning.setStyleName("StudioPopup-Msg");
			lblWarning.setText(WARNING_MSG);
	
			detailPanel.add(lblWarning);
			
			warningPanel.add(icon);
			warningPanel.add(detailPanel);
			
			info.add(lbl);
			info.add(warningPanel);
			
			panel.add(info);
			
			Widget buttonBar = getConfirmButtonPanel(constraintName); 
			panel.add(buttonBar);
			panel.setCellHorizontalAlignment(buttonBar, HasHorizontalAlignment.ALIGN_CENTER);
		} catch (Exception e) {
			throw new PopUpException(e.getMessage());
		}
		return panel;
	}
	
	private Widget getConfirmButtonPanel(final String constraintName){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		
		bar.add(yesButton);
		bar.add(noButton);
		
		bar.setCellHorizontalAlignment(yesButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(noButton, HasHorizontalAlignment.ALIGN_CENTER);

		yesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					PgStudio.studioService.dropCheck(PgStudio.getToken(), item.getSchema(), item.getName(), constraintName,
							new AsyncCallback<String>() {
								@Override
								public void onFailure(Throwable caught) {
									ResultsPopUp pop = new ResultsPopUp("Error", caught.getMessage());
									try {
										pop.getDialogBox();
									} catch (PopUpException ex) {
										Window.alert(ex.getMessage());
									}

								}

								@Override
								public void onSuccess(String result) {
									JsArray<CommandJsObject> jArray = json2Messages(result);

									CommandJsObject res = jArray.get(0);
									if (res != null && res.getStatus() != null
											&& res.getStatus()
											.contains("ERROR")) {
										ResultsPopUp pop = new ResultsPopUp(
												"Error", res.getMessage());
										try {
											pop.getDialogBox();
											//refresh();
										} catch (PopUpException caught) {
											Window.alert(caught.getMessage());
										}
									} else {
										ResultsPopUp pop = new ResultsPopUp("Results", res.getMessage());
										try {
											pop.getDialogBox();
											confirmBox.hide();
											refresh();
										} catch (PopUpException caught) {
											Window.alert(caught.getMessage());
										}
									}
								}
							});
				} catch (DatabaseConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PostgreSQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		noButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				confirmBox.hide(true);				
			}
		});

		return bar.asWidget();
	}
	
	private void disableNonDomainView(boolean isDomain) {
		if(!isDomain) {
			addCheckButton.setVisible(false);
			alterButton.setVisible(false);
			constraintsLbl.setVisible(false);			
		} else {
			addCheckButton.setVisible(true);
			alterButton.setVisible(true);
			constraintsLbl.setVisible(true);						
		}
	}
	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{ 
  	return eval(json); 
	}-*/;
}
