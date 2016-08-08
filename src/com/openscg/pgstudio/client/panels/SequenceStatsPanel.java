/*
 * PostgreSQL Studio
 */
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.models.StatsInfo;
import com.openscg.pgstudio.client.panels.popups.AlterSequencePopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.client.panels.popups.ResultsPopUp;
import com.openscg.pgstudio.client.panels.popups.SequenceSetValuePopUp;
import com.openscg.pgstudio.client.providers.StatsListDataProvider;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;

public class SequenceStatsPanel extends Composite implements DetailsPanel {

	private static String MAIN_HEIGHT = "300px";
	
	public static enum TYPE implements IsSerializable {
		RESTART, RESET, INCREMENT
	}
	private final static String WARNING_MSG_RESTART = 
			"This will restart the sequence. Are you sure you want to continue?";
	
	private final static String WARNING_MSG_RESET = 
			"This will reset the sequence. Are you sure you want to continue?";
	
	private final static String WARNING_MSG_INCR = 
			"This will increment the sequence last value. Are you sure you want to continue?";

	private DataGrid<StatsInfo> dataGrid;
	private StatsListDataProvider dataProvider = new StatsListDataProvider();

	private ModelInfo item = null;
	
	final DialogBox dialogBox = new DialogBox();
	
	TYPE seqType = null;

	private static interface GetValue<C> {
		C getValue(StatsInfo column);
	}

	public void setItem(ModelInfo item) {
		this.item = item;
		dataProvider.setItem(item.getId(), item.getItemType());		
	}

	public SequenceStatsPanel() {

		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());

		initWidget(mainPanel);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();

		PushButton refresh = getRefreshButton();
		PushButton alter = getAlterButton();
		PushButton restart = getRestartButton();
		PushButton setValue = getSetValueButton();
		PushButton increment = getIncrementButton();
		PushButton reset = getResetButton();
		
		bar.add(refresh);
		bar.add(alter);
		bar.add(restart);
		bar.add(increment);
		bar.add(setValue);
		bar.add(reset);
		
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
	
	private PushButton getIncrementButton(){
		PushButton button = new PushButton(new Image(PgStudio.Images.increment()));
		button.setTitle("Increment Value");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showConfirmationDialogBox(TYPE.INCREMENT);
			}			
		});
		return button;
	}
	
	private void incrementSequenceValue(){
		try {
			PgStudio.studioService.incrementValue(PgStudio.getToken(), item.getSchema(), item.getName(),  new AsyncCallback<String>() {
			    public void onFailure(Throwable caught) {
			    	dialogBox.hide(true);
			        Window.alert(caught.getMessage());
			    }

			    public void onSuccess(String result) {
			    	dialogBox.hide(true);
			    	JsArray<CommandJsObject> jArray = json2Messages(result);
					CommandJsObject res = jArray.get(0);
			    	ResultsPopUp pop = new ResultsPopUp("Results", res.getMessage());
					try {
						pop.getDialogBox();
						refresh();
					} catch (PopUpException caught) {
						Window.alert(caught.getMessage());
					}
			    }
			});
		} catch (DatabaseConnectionException | PostgreSQLException e) {
			Window.alert(e.getMessage());
		}
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

	private PushButton getAlterButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.edit()));
		button.setTitle("Alter Sequence");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(PgStudio.ITEM_TYPE.SEQUENCE.toString().equalsIgnoreCase(item.getItemType().toString())){
					AlterSequencePopUp pop = new AlterSequencePopUp();
					pop.setDataProvider(dataProvider);
					pop.setItem(item);
					pop.setStatsList(dataProvider.getStatsList());
					try {
						pop.getDialogBox();
					} catch (PopUpException e) {
						Window.alert(e.getMessage());
					}
				} 
			}
		});
		return button;
	}
	
	private DialogBox showConfirmationDialogBox(TYPE type){
		dialogBox.setWidget(getDialogBoxPanel(type));
		
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		
		return dialogBox;
		
	}
	
	private VerticalPanel getDialogBoxPanel(TYPE type){

		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);
		
		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	
		
		String title = type +" "+item.getName();
		lbl.setText(title);
		
		HorizontalPanel warningPanel = new HorizontalPanel();
		Image icon = new Image(PgStudio.Images.warning());
		icon.setWidth("110px");
		
		VerticalPanel detailPanel = new VerticalPanel();
		
		Label lblWarning = new Label();
		lblWarning.setStyleName("StudioPopup-Msg");
		
		switch(type) {
		case RESTART:
			lblWarning.setText(WARNING_MSG_RESTART);
			break;
		case RESET:
			lblWarning.setText(WARNING_MSG_RESET);
			break;
		case INCREMENT:
			lblWarning.setText(WARNING_MSG_INCR);
			break;
		default:
			break;
		}
		
		HorizontalPanel cascadePanel = new HorizontalPanel();
		cascadePanel.setSpacing(5);
		
		detailPanel.add(lblWarning);
		
		warningPanel.add(icon);
		warningPanel.add(detailPanel);
		
		info.add(lbl);
		info.add(warningPanel);
		
		panel.add(info);
		
		Widget buttonBar = getDialogBoxButtonPanel(type); 
		panel.add(buttonBar);
		panel.setCellHorizontalAlignment(buttonBar, HasHorizontalAlignment.ALIGN_CENTER);
		
		return panel;
	}
	
	private Widget getDialogBoxButtonPanel(TYPE type){
			seqType = type;
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
					switch(seqType) {
					case RESTART:
						restartSequence();
						break;
					case RESET:
						resetSequence();
						break;
					case INCREMENT:
						incrementSequenceValue();
						break;
					default:
						break;
					}
				}
			});

			noButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					refresh();
					dialogBox.hide(true);
				}
			});

			return bar.asWidget();
	}
	

	private PushButton getRestartButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.update()));
		button.setTitle("Restart Sequence");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showConfirmationDialogBox(TYPE.RESTART);
			}
		});
		return button;
	}
	
	private void restartSequence(){
		if(PgStudio.ITEM_TYPE.SEQUENCE.toString().equalsIgnoreCase(item.getItemType().toString())){
			try {
				PgStudio.studioService.restartSequence(PgStudio.getToken(), item.getSchema(), item.getName(), new AsyncCallback<String>(){
					@Override
					public void onFailure(Throwable caught) {
						dialogBox.hide(true);
						ResultsPopUp pop = new ResultsPopUp(
								"Error", caught.getMessage());
						try {
							pop.getDialogBox();
						} catch (PopUpException ex) {
							Window.alert(ex.getMessage());
						}	
					}
					public void onSuccess(String result) {
						dialogBox.hide(true);
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
								refresh();
							} catch (PopUpException caught) {
								Window.alert(caught.getMessage());
							}
						}
					}
				});
			} catch(Exception ex) {
				Window.alert(ex.getMessage());
			}
		}
	}
	
	private PushButton getResetButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.reset()));
		button.setTitle("Reset Sequence");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showConfirmationDialogBox(TYPE.RESET);
			}
		});
		return button;
	}
	
	private void resetSequence(){
		if(PgStudio.ITEM_TYPE.SEQUENCE.toString().equalsIgnoreCase(item.getItemType().toString())){
			try {
				PgStudio.studioService.resetSequence(PgStudio.getToken(), item.getSchema(), item.getName(), new AsyncCallback<String>(){
					@Override
					public void onFailure(Throwable caught) {
						dialogBox.hide(true);
						ResultsPopUp pop = new ResultsPopUp(
								"Error", caught.getMessage());
						try {
							pop.getDialogBox();
						} catch (PopUpException ex) {
							Window.alert(ex.getMessage());
						}	
					}
					public void onSuccess(String result) {
						dialogBox.hide(true);
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
								refresh();
							} catch (PopUpException caught) {
								Window.alert(caught.getMessage());
							}
						}
					}



				})	;
			} catch(Exception ex) {
				Window.alert(ex.getMessage());
			}

		}
	}

	private PushButton getSetValueButton() {
		PushButton button = new PushButton(new Image(PgStudio.Images.editValue()));
		button.setTitle("Set Value");
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				SequenceSetValuePopUp popUp = new SequenceSetValuePopUp();
				popUp.setSequenceName(item.getName());
				try {
					popUp.getDialogBox();
				} catch (PopUpException e) {
					e.printStackTrace();
				}
			}
		});
		return button;
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
		setItem(this.item);
	}
	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{ 
  	return eval(json); 
}-*/;


}
