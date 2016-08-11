package com.openscg.pgstudio.client.panels.popups;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.messages.FTSConfigurationJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.FTSConfigurationInfo;
import com.openscg.pgstudio.client.providers.FTSMenuDataProvider;
import com.openscg.pgstudio.client.providers.ModelListProvider;

public class AddFTSConfigPopUp implements StudioModelPopUp {
	private DatabaseObjectInfo schema = null;
	protected final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);
	final DialogBox dialogBox = new DialogBox();

	protected SingleSelectionModel<FTSConfigurationInfo> selectionModel = null;
	protected FTSMenuDataProvider dataProvider = new FTSMenuDataProvider();

	protected ArrayList<String> ftsTemplatesList = new ArrayList<String>();
	protected ArrayList<String> ftsParsersList = new ArrayList<String>();

	protected FTSConfigurationInfo item = null;

	protected TextBox confName;
	protected ListBox ftsTemplates;
	protected ListBox ftsParsers;
	protected TextArea comment;

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");
		dialogBox.setWidget(getPanel());
		populateFTSTemplates();
		populateFTSParsers();
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		return dialogBox;
	}

	@Override
	public void setSelectionModel(SingleSelectionModel model) {
		this.selectionModel = model;
	}

	@Override
	public void setDataProvider(ModelListProvider provider) {
		this.dataProvider = (FTSMenuDataProvider) provider;

	}

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
	}

	public void setItem(FTSConfigurationInfo item) {
		this.item = item;
	}

	public String getConfName() {
		return confName.getText();
	}

	public void setConfName(String confName) {
		this.confName.setText(confName);
	}

	public String getComment() {
		return comment.getValue();
	}

	public void setComment(String comment) {
		this.comment.setText(comment);
	}

	protected VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lbl.setText("Create FTS Configuration");

		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblName.setText("Name");

		confName = new TextBox();
		confName.setWidth("155px");

		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(confName);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		CaptionPanel dtCommentPanel = new CaptionPanel("Comment");
		dtCommentPanel.setStyleName("StudioCaption");
		comment = new TextArea();
		comment.setVisibleLines(2);
		comment.setWidth("300px");
		dtCommentPanel.add(comment);

		Label lblTemplates = new Label();
		lblTemplates.setStyleName("StudioPopup-Msg");
		lblTemplates.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblTemplates.setText("Template");

		ftsTemplates = new ListBox();
		ftsTemplates.setWidth("200px");

		HorizontalPanel templatesPanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblTemplates);
		namePanel.add(ftsTemplates);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		Label lblParsers = new Label();
		lblParsers.setStyleName("StudioPopup-Msg");
		lblParsers.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblParsers.setText("Parser");

		ftsParsers = new ListBox();
		ftsParsers.setWidth("200px");

		HorizontalPanel parsersPanel = new HorizontalPanel();
		parsersPanel.setSpacing(10);
		parsersPanel.add(lblParsers);
		parsersPanel.add(ftsParsers);
		parsersPanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		panel.add(lbl);
		panel.add(namePanel);
		panel.add(templatesPanel);
		panel.add(parsersPanel);
		panel.add(dtCommentPanel);

		panel.add(getButtonPanel());
		return panel;
	}

	protected Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		bar.setWidth("350px");

		Button createButton = new Button("Create");
		Button cancelButton = new Button("Cancel");

		bar.add(createButton);
		bar.add(cancelButton);

		bar.setCellHorizontalAlignment(createButton, HasHorizontalAlignment.ALIGN_RIGHT);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_LEFT);

		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(confName.getText() == null || confName.getText().equals("")) {
					Window.alert("Configuration name can not be blank");
				}  
				if (confName.getText() != null
						&& !confName.getText().equals("")
						&& ((ftsTemplates.getValue(ftsTemplates.getSelectedIndex()) != null && !ftsTemplates.getValue(ftsTemplates.getSelectedIndex()).equals(""))
								|| (ftsParsers.getValue(ftsParsers.getSelectedIndex()) != null && !ftsParsers.getValue(ftsParsers.getSelectedIndex()).equals("")))) {
					if(((ftsTemplates.getValue(ftsTemplates.getSelectedIndex()) != null && !ftsTemplates.getValue(ftsTemplates.getSelectedIndex()).equals(""))
							&& (ftsParsers.getValue(ftsParsers.getSelectedIndex()) != null && !ftsParsers.getValue(ftsParsers.getSelectedIndex()).equals(""))))
						Window.alert("Can't specify both parser and template during text search configuration creation.");
					else {
						UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
						ac.setAutoRefresh(true);
						ac.setShowResultOutput(true);
						try {
							studioService.createFTSConfiguration(PgStudio.getToken(), PgStudio.getSelectedSchema().getName(), confName.getText(), ftsTemplates.getValue(ftsTemplates.getSelectedIndex()), ftsParsers.getValue(ftsParsers.getSelectedIndex()), comment.getValue(), ac);
						} catch(Exception ex) {
							Window.alert("Error.."+ex.getMessage());
						}
					}

				} else {
					Window.alert("Either template or parser must be selected.");
				}
			} 
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				confName.setText("");
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}

	protected void populateFTSTemplates() {
		try {
			studioService.getFTSTemplatesList(PgStudio.getToken(), new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					ftsTemplates.clear();
					ftsTemplatesList.clear();
					// Show the RPC error message to the user
					Window.alert(caught.getMessage());
				}

				public void onSuccess(String result) {
					ftsTemplates.clear();
					ftsTemplatesList.clear();
					ftsTemplates.addItem("");
					String templateName = "";
					JsArray<FTSConfigurationJsObject> jsonArr = json2Messages(result);
					if(jsonArr != null) {
						for(int i = 0 ; i < jsonArr.length(); i++) {
							FTSConfigurationJsObject jsonObj = jsonArr.get(i);
							templateName = jsonObj.getName();
							ftsTemplates.addItem(templateName);
						}
					} 
				}
			});
		} catch(Exception ex) {
			Window.alert(ex.getMessage());
		}

	}

	protected void populateFTSParsers() {
		try {
			studioService.getFTSParsersList(PgStudio.getToken(), new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					ftsParsers.clear();
					ftsParsersList.clear();
					Window.alert(caught.getMessage());
				}
				public void onSuccess(String result) {
					ftsParsers.clear();
					ftsParsersList.clear(); 
					ftsParsers.addItem("");
					String parserName = "";
					JsArray<FTSConfigurationJsObject> jsonArr = json2Messages(result);
					if(jsonArr != null) {
						for(int i = 0 ; i < jsonArr.length(); i++) {
							FTSConfigurationJsObject jsonObj =jsonArr.get(i);
							parserName = jsonObj.getName();
							ftsParsers.addItem(parserName);
						}
					}  
				}
			});

		}
		catch(Exception ex) {
			Window.alert(ex.getMessage());
		}

	}

	private static final native JsArray<FTSConfigurationJsObject> json2Messages(
			String json)
			/*-{
return eval(json);
}-*/;
}

