/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.messages.StatsJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.models.StatsInfo;
import com.openscg.pgstudio.client.providers.ModelListProvider;
import com.openscg.pgstudio.client.providers.StatsListDataProvider;

public class AlterSequencePopUp implements StudioModelPopUp {
	
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();

    private StatsListDataProvider dataProvider = null;
    
    private DatabaseObjectInfo schema = null;
    
    private ModelInfo item = null;
    
    private List<StatsInfo> statsList = new ArrayList<StatsInfo>();
    
	private CheckBox cycleBox;

	private TextBox increment;
	private TextBox minValue;
	private TextBox maxValue;
	private TextBox start;
	private TextBox cache;
	
	public void setItem(ModelInfo item) {
		this.item = item;
	}
	
	public void setStatsList(List<StatsInfo> statsList) {
		this.statsList = statsList;
	}
	
	@Override
	public DialogBox getDialogBox() throws PopUpException {

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		dialogBox.setWidget(getPanel());
		
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		return dialogBox;
	}

	@Override
	public void setSelectionModel(SingleSelectionModel model) {
		//this.selectionModel = model;
	}

	@Override
	public void setDataProvider(ModelListProvider provider) {
		this.dataProvider = (StatsListDataProvider) provider;
		
	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}
	
	private StatsInfo msgToColumnInfo(StatsJsObject msg) {

		StatsInfo stat = new StatsInfo(msg.getName(), msg.getValue());

		return stat;
	}

	private static final native JsArray<StatsJsObject> json2Messages(String json)
	/*-{
		return eval(json);
	}-*/;
	
	private VerticalPanel getPanel(){
		BigInteger startvValue = new BigInteger(statsList.get(7).getValue());
		BigInteger minvValue = new BigInteger(statsList.get(1).getValue());
		BigInteger maxvValue = new BigInteger(statsList.get(2).getValue());
		int cachevValue = new Integer(statsList.get(3).getValue());
		BigInteger incrementvBy = new BigInteger(statsList.get(4).getValue());
		boolean isvCycled = new Boolean(statsList.get(5).getValue());
		//boolean isvCalled = new Boolean(statsList.get(6).getValue());
		
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);
		
		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lbl.setText("Alter Sequence");
		
		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblName.setText("Sequence Name");

		Label seqName = new Label();
		seqName.setStyleName("StudioPopup-Msg-Strong");
		seqName.setText(item.getName().toUpperCase());
		seqName.setWidth("155px");
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(seqName);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		info.add(lbl);
		info.add(namePanel);
		
		VerticalPanel checkPanel = new VerticalPanel();

		cycleBox = new CheckBox();
		cycleBox.setText("Cycle");
		cycleBox.setValue(isvCycled);
		
		checkPanel.add(cycleBox);

		Label lblStart = new Label();
		lblStart.setStyleName("StudioPopup-Msg");
		lblStart.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		lblStart.setWidth("100px");
		lblStart.setText("Starting Value");

		start = new TextBox();
		start.setValue(String.valueOf(startvValue));
		start.setWidth("50px");
		start.addKeyPressHandler(getOnlyNumbersKeyPressHandler());
		
		HorizontalPanel startPanel = new HorizontalPanel();
		startPanel.setSpacing(1);
		startPanel.add(lblStart);
		startPanel.add(start);
		startPanel.setCellVerticalAlignment(lblStart, HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel panel1 = new HorizontalPanel();
		panel1.setWidth("325px");
		panel1.setSpacing(10);
		panel1.add(checkPanel);
		panel1.add(startPanel);
		panel1.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);
		panel1.setCellHorizontalAlignment(checkPanel, HasHorizontalAlignment.ALIGN_CENTER);
		panel1.setCellHorizontalAlignment(startPanel, HasHorizontalAlignment.ALIGN_RIGHT);

		Label lblIncrement = new Label();
		lblIncrement.setStyleName("StudioPopup-Msg");
		lblIncrement.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblIncrement.setWidth("75px");
		lblIncrement.setText("Increment");

		increment = new TextBox();
		increment.setValue(String.valueOf(incrementvBy));
		increment.setWidth("50px");
		increment.addKeyPressHandler(getOnlyNumbersKeyPressHandler());
		
		HorizontalPanel incrementPanel = new HorizontalPanel();
		incrementPanel.setSpacing(1);
		incrementPanel.add(lblIncrement);
		incrementPanel.add(increment);
		incrementPanel.setCellVerticalAlignment(lblIncrement, HasVerticalAlignment.ALIGN_MIDDLE);

		Label lblCache = new Label();
		lblCache.setStyleName("StudioPopup-Msg");
		lblCache.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		lblCache.setWidth("75px");
		lblCache.setText("Cache");

		cache = new TextBox();
		cache.setValue(String.valueOf(cachevValue));
		cache.setWidth("50px");
		cache.addKeyPressHandler(getOnlyNumbersKeyPressHandler());
		
		HorizontalPanel cachePanel = new HorizontalPanel();
		cachePanel.setSpacing(1);
		cachePanel.add(lblCache);
		cachePanel.add(cache);
		cachePanel.setCellVerticalAlignment(lblCache, HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel panel2 = new HorizontalPanel();
		panel2.setWidth("325px");
		panel2.setSpacing(10);
		panel2.add(incrementPanel);
		panel2.add(cachePanel);
		panel2.setCellHorizontalAlignment(incrementPanel, HasHorizontalAlignment.ALIGN_LEFT);
		panel2.setCellHorizontalAlignment(cachePanel, HasHorizontalAlignment.ALIGN_RIGHT);

		Label lblMinValue = new Label();
		lblMinValue.setStyleName("StudioPopup-Msg");
		lblMinValue.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblMinValue.setWidth("75px");
		lblMinValue.setText("Min Value");

		minValue = new TextBox();
		minValue.setValue(String.valueOf(minvValue));
		minValue.setWidth("50px");
		minValue.addKeyPressHandler(getOnlyNumbersKeyPressHandler());
		
		HorizontalPanel minValuePanel = new HorizontalPanel();
		minValuePanel.setSpacing(1);
		minValuePanel.add(lblMinValue);
		minValuePanel.add(minValue);
		minValuePanel.setCellVerticalAlignment(lblMinValue, HasVerticalAlignment.ALIGN_MIDDLE);

		Label lblMaxValue = new Label();
		lblMaxValue.setStyleName("StudioPopup-Msg");
		lblMaxValue.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		lblMaxValue.setWidth("75px");
		lblMaxValue.setText("Max Value");

		maxValue = new TextBox();
		maxValue.setValue(String.valueOf(maxvValue));
		maxValue.setWidth("50px");
		maxValue.addKeyPressHandler(getOnlyNumbersKeyPressHandler());
		
		HorizontalPanel maxValuePanel = new HorizontalPanel();
		maxValuePanel.setSpacing(1);
		maxValuePanel.add(lblMaxValue);
		maxValuePanel.add(maxValue);
		maxValuePanel.setCellVerticalAlignment(lblMaxValue, HasVerticalAlignment.ALIGN_MIDDLE);
		
		HorizontalPanel panel3 = new HorizontalPanel();
		panel3.setWidth("325px");
		panel3.setSpacing(10);
		panel3.add(minValuePanel);
		panel3.add(maxValuePanel);
		panel3.setCellHorizontalAlignment(minValuePanel, HasHorizontalAlignment.ALIGN_LEFT);
		panel3.setCellHorizontalAlignment(maxValuePanel, HasHorizontalAlignment.ALIGN_RIGHT);
				
		VerticalPanel optionPanel = new VerticalPanel();
		optionPanel.add(panel1);
		optionPanel.add(panel2);
		optionPanel.add(panel3);
		
		panel.add(info);
		panel.add(optionPanel);
		
		Widget buttonBar = getButtonPanel(); 
		panel.add(buttonBar);
		panel.setCellHorizontalAlignment(buttonBar, HasHorizontalAlignment.ALIGN_CENTER);
		return panel;
	}
	
	private Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		
		Button addButton = new Button("Submit");
		Button cancelButton = new Button("Cancel");
		
		bar.add(addButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
					boolean cycle = cycleBox.getValue();
					
					String incr = null;
					String min = null;
					String max = null;
					String st = null;
					int ca = 0;

					if (!increment.getText().trim().equals(""))
					{
						incr = increment.getText();
					}

					if (!minValue.getText().trim().equals("")) {
						min = minValue.getText();
					}
					if (!maxValue.getText().trim().equals("")) {
						max = maxValue.getText();
					}
					if (!start.getText().trim().equals("")){ 
						st = start.getText();
					}
					if (!cache.getText().trim().equals("")){ 
						ca = Integer.parseInt(cache.getText());
					}
					UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
					ac.setAutoRefresh(true);
					ac.setShowResultOutput(false);
					
					studioService.alterSequence(PgStudio.getToken(), item.getSchema(), item.getName(), 
							incr, min, max, st, ca, cycle, ac);
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}
			
	private KeyPressHandler getOnlyNumbersKeyPressHandler() {
		KeyPressHandler kph = new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char keyCode = event.getCharCode();

				if ((!Character.isDigit(keyCode))
						&& (keyCode != (char) KeyCodes.KEY_TAB)
						&& (keyCode != (char) KeyCodes.KEY_BACKSPACE)
						&& (keyCode != (char) KeyCodes.KEY_DELETE)
						&& (keyCode != (char) KeyCodes.KEY_ENTER)
						&& (keyCode != (char) KeyCodes.KEY_HOME)
						&& (keyCode != (char) KeyCodes.KEY_END)
						&& (keyCode != (char) KeyCodes.KEY_LEFT)
						&& (keyCode != (char) KeyCodes.KEY_UP)
						&& (keyCode != (char) KeyCodes.KEY_RIGHT)
						&& (keyCode != (char) KeyCodes.KEY_DOWN)) {
					// TextBox.cancelKey() suppresses the current keyboard
					// event.		
					((TextBox)event.getSource()).cancelKey();
				}

			}
		};

		return kph;
	}	
}
