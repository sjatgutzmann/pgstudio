/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.mastergaurav.codemirror.client.CodeMirror;
import com.mastergaurav.codemirror.client.CodeMirrorConfig;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.DATABASE_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.messages.DataTypesJsObject;
import com.openscg.pgstudio.client.messages.ListJsObject;
import com.openscg.pgstudio.client.models.DataTypeInfo;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.panels.FunctionsScriptPanel;
import com.openscg.pgstudio.client.providers.ModelListProvider;
import com.openscg.pgstudio.shared.dto.AlterFunctionRequest;

public class EditFunctionPopUp implements StudioModelPopUp {
	
	private FunctionsScriptPanel panel;
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();
	DialogBox dialog_child = null;
  
	private TextBox functionNameTxtBx = new TextBox();	
	private TextBox linkSymbol = new TextBox();
	private String functionName;
	private SimplePanel codePanel;
	private final TextArea codeArea = new TextArea();
	private String code;
	private CodeMirror cm;
	private Label returnType = new Label();
	private FlexTable params = new FlexTable();
	private Label languageTxtBx = new Label();
	private CaptionPanel definitionPanel = null;
	private HorizontalPanel linkPanel = null;
	private String[] parameters;
	private String language;
	private String returns;
	private String schema;
	
	private String objectFilePath;
	
	public void setObjectFilePath(String objectFilePath) {
		this.objectFilePath = objectFilePath;
		objectFile.setText(objectFilePath);
	}

	private TextBox objectFile = new TextBox();
	HorizontalPanel objFilePanel = null;
	
	
	public EditFunctionPopUp(FunctionsScriptPanel panel) {
		this.panel = panel;
	}
	@Override
	public DialogBox getDialogBox() throws PopUpException {
		
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
		//this.dataProvider = (FunctionListDataProvider) provider;		
	}

	@Override
	public void setSchema(DatabaseObjectInfo schema) {		
	
	}
		
	public void setSchema(String schema) {		
		this.schema = schema;
	}
	
	public void setupCodePanel() {
		Element e = codeArea.getElement();
		TextAreaElement tae = TextAreaElement.as(e);
		
		CodeMirrorConfig config = getConfig();
		cm = CodeMirror.fromTextArea(tae, config);	
	}

	private VerticalPanel getPanel(){
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");
		panel.setSpacing(5);

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);
		
		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 
		lbl.setText("Alter Function");
		
		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblName.setText("Function Name");

		functionNameTxtBx.setWidth("155px");
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(functionNameTxtBx);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		info.add(lbl);
		info.add(namePanel);	

		studioService.getList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.DATA_TYPE, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                // Show the RPC error message to the user
                Window.alert(caught.getMessage());
            }

            public void onSuccess(String result) {
        		JsArray<DataTypesJsObject> types = DataTypeInfo.json2Messages(result);

                for (int i = 0; i < types.length(); i++) {
                	DataTypeInfo info = DataTypeInfo.msgToInfo(types.get(i));                	
                 }
                
                DataTypeInfo voidInfo = new DataTypeInfo(0, 0, "void");
            }
          });

		Label lblReturns = new Label();
		lblReturns.setStyleName("StudioPopup-Msg");
		lblReturns.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblReturns.setText("Returns");

		returnType.setStyleName("StudioPopup-Msg");
		returnType.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		
		HorizontalPanel returnsPanel = new HorizontalPanel();
		returnsPanel.setSpacing(10);
		returnsPanel.add(lblReturns);
		returnsPanel.add(returnType);
		returnsPanel.setCellVerticalAlignment(lblReturns, HasVerticalAlignment.ALIGN_MIDDLE);
		studioService.getList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.LANGUAGE,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {						
						// Show the RPC error message to the user
						Window.alert(caught.getMessage());
					}

					public void onSuccess(String result) {			

						JsArray<ListJsObject> langs = DatabaseObjectInfo.json2Messages(result);

						for (int i = 0; i < langs.length(); i++) {
							DatabaseObjectInfo info = DatabaseObjectInfo.msgToInfo(langs.get(i));					
						}
					}
				});

		Label lblLanguage = new Label();
		lblLanguage.setStyleName("StudioPopup-Msg");
		lblLanguage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblLanguage.setText("Language");

		HorizontalPanel languagePanel = new HorizontalPanel();
		languagePanel.setSpacing(10);
		languagePanel.add(lblLanguage);
		languagePanel.add(languageTxtBx);
		languagePanel.setCellVerticalAlignment(lblLanguage, HasVerticalAlignment.ALIGN_MIDDLE);
		
		
		
		HorizontalPanel detailsPanel = new HorizontalPanel();
		detailsPanel.setWidth("380px");
		detailsPanel.setSpacing(5);
		detailsPanel.add(returnsPanel);
		detailsPanel.add(languagePanel);
		
		definitionPanel = new CaptionPanel("Definition");
		definitionPanel.setStyleName("StudioCaption");
		definitionPanel.add(getSQLPanel());
		
		Label lblLink = new Label();
		lblLink.setStyleName("StudioPopup-Msg");
		lblLink.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblLink.setText("Link Symbol");

		linkSymbol.setWidth("155px");
		
		linkPanel = new HorizontalPanel();
		linkPanel.setSpacing(5);
		linkPanel.setWidth("330px");
		linkPanel.add(lblLink);
		linkPanel.add(linkSymbol);
		linkPanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label lblObjFile = new Label();
		lblObjFile.setStyleName("StudioPopup-Msg");
		lblObjFile.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblObjFile.setText("Object File");

		//objectFile = new TextBox();
		objectFile.setWidth("155px");
		
		objFilePanel = new HorizontalPanel();
		objFilePanel.setSpacing(5);
		objFilePanel.setWidth("360px");
		objFilePanel.add(lblObjFile);
		objFilePanel.add(objectFile);
		objFilePanel.setVisible(true);
		objFilePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		String langSel = languageTxtBx.getText();
		
		if("C".equalsIgnoreCase(langSel)){
			definitionPanel.setVisible(false);
			linkPanel.setVisible(true);
			objFilePanel.setVisible(true);
		}else 
		if("internal".equals(langSel)){
			definitionPanel.setVisible(false);
			linkPanel.setVisible(true);
			objFilePanel.setVisible(false);
		}else{
			definitionPanel.setVisible(true);
			linkPanel.setVisible(false);
			objFilePanel.setVisible(false);
		}
		
		panel.add(info);
		panel.add(getParameterPanel());
		panel.add(detailsPanel);
		panel.add(definitionPanel);
		panel.add(linkPanel);
		panel.add(objFilePanel);
		
		Widget buttonBar = getButtonPanel(); 
		panel.add(buttonBar);
		panel.setCellHorizontalAlignment(buttonBar, HasHorizontalAlignment.ALIGN_CENTER);
		
		return panel;
	}
	
	private Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		
		Button alterButton = new Button("Alter");
		Button cancelButton = new Button("Cancel");
		
		bar.add(alterButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(alterButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		
		alterButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				// Create model and send to server
				AlterFunctionRequest request = new AlterFunctionRequest();
				request.setFunctionName(functionName);
				request.setNewFunctionName(functionNameTxtBx.getText());
				request.setFunctionBody(code);
				
				if(!"internal".equals(language) && !"C".equalsIgnoreCase(language))
					request.setNewFunctionBody(cm.getContent());
				else
					request.setNewFunctionBody(linkSymbol.getText());
				
				if("C".equalsIgnoreCase(language)){
					request.setObjectFilePath(objectFile.getText());
				}
				
				request.setParamsList(parameters);
				request.setLanguage(language);
				request.setReturns(returns);
				request.setSchema(schema);
				try {
					PgStudio.studioService.alterFunction(PgStudio.getToken(), request, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable t) {
							ResultsPopUp pop = new ResultsPopUp("Error", t.getMessage());
							try {
								pop.getDialogBox();
							} catch (PopUpException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							dialogBox.hide(true);
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
								} catch (PopUpException caught) {
									Window.alert(caught.getMessage());
								}
							} else {								
									ResultsPopUp pop = new ResultsPopUp("Results", res.getStatus());
									try {
										pop.getDialogBox();
									} catch (PopUpException caught) {
										Window.alert(caught.getMessage());
									}							
									dialogBox.hide(true);
								}
								panel.refresh();								
								PgStudio.msp.refreshCurrent();
							}
						
						});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			
	
	

	private Widget getSQLPanel() {
		codePanel = new SimplePanel();
				
		codeArea.setWidth("380px");
		codeArea.setHeight("150px");

		codeArea.setReadOnly(false);
		
		codePanel.add(codeArea);
		
		return codePanel.asWidget();
	}
	
	private Widget getParameterPanel() {
		CaptionPanel panel = new CaptionPanel("Parameters");
		panel.setStyleName("StudioCaption");
		VerticalPanel vPanel = new VerticalPanel();
		ScrollPanel paramPanel = new ScrollPanel();

		
		params.setText(0,0, "Name");
		params.setText(0,1, "Datatype");
		params.setText(0,2, "Mode");
		params.setText(0, 3, "Default");

		params.getColumnFormatter().setWidth(0, "80px");
		params.getColumnFormatter().setWidth(1, "80px");
		params.getColumnFormatter().setWidth(2, "60px");
		params.getColumnFormatter().setWidth(3, "80px");

		paramPanel.setHeight("80px");
		paramPanel.setWidth("380px");
		paramPanel.add(params);

	    vPanel.add(paramPanel);
	    
		panel.add(vPanel);
		panel.setSize("380px","120px");

		return panel.asWidget();
	}

	
	private CodeMirrorConfig getConfig() {
		CodeMirrorConfig config = CodeMirrorConfig.getDefault();
		
		String parserFile = GWT.getModuleBaseURL() + "cm/contrib/sql/js/parsesql.js";
		String styleSheet = GWT.getModuleBaseURL() + "cm/contrib/sql/css/sqlcolors.css";
		
		config.setParserFile(parserFile);
		config.setStylesheet(styleSheet);
		
		config.setWidth(380, Unit.PX);
		config.setHeight(150, Unit.PX);
		
		return config;
	}
	
	
	public void setFunctionName(String functionName) {
		this.functionName = functionName; // Store the current function name.
		this.functionNameTxtBx.setText(functionName); // Display in the text box so that user can change it.
	}
	
	public void setFunctionCode(String functionCode) {				
		this.code = functionCode;
		linkSymbol.setText(functionCode);
		codeArea.setText(functionCode); // Display in the text area.
	}
	
	public void setReturns(String returnType) {
		this.returns = returnType;
		this.returnType.setText(returnType);
	}
	
	public void setLanguage(String language) {
		this.language = language;
		this.languageTxtBx.setText(language);
	}
	
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
		for(int row = 1; row <= parameters.length; row++) {		
			int arrIdx = row - 1;
			if(parameters[arrIdx] != null) {
				String[] param = parameters[arrIdx].trim().split("\\s");
				if(param.length == 1) { 
					// Only datatype					
					params.setText(row, 0, "");
					params.setText(row, 1, param[0]);
				}
				if(param.length == 2) {
					// name and datatype
					params.setText(row, 0, param[0]);
					params.setText(row, 1, param[1]);
				}
				if(param.length == 3) {
					// name, datatype and mode 
					params.setText(row, 0, param[1]);
					params.setText(row, 1, param[2]);
					params.setText(row, 2, param[0]);
				}
						
			}	
		}
	}

	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{ 
	  	return eval(json); 
	}-*/;

}
