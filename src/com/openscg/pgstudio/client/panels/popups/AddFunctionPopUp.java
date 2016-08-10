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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.messages.DataTypesJsObject;
import com.openscg.pgstudio.client.messages.ListJsObject;
import com.openscg.pgstudio.client.models.DataTypeInfo;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.FunctionInfo;
import com.openscg.pgstudio.client.providers.FunctionListDataProvider;
import com.openscg.pgstudio.client.providers.ModelListProvider;
import com.openscg.pgstudio.shared.dto.AlterFunctionRequest;

public class AddFunctionPopUp implements StudioModelPopUp {
	
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();
	DialogBox dialog_child = null;

    private SingleSelectionModel<FunctionInfo> selectionModel = null;
    private FunctionListDataProvider dataProvider = null;
    
	private ArrayList<DataTypeInfo> returnTypes = null;
	private ArrayList<DatabaseObjectInfo> languages = null;
	private ArrayList<String> paramIndex = new ArrayList<String>();

    private DatabaseObjectInfo schema = null;
    
	private TextBox functionName = new TextBox();	
	private TextBox linkSymbol = new TextBox();
	
	private TextBox objectFile = new TextBox();

	private FlexTable params;

	private ListBox returnType;
	private ListBox language;

	private SimplePanel codePanel;
	private final TextArea codeArea = new TextArea();
	private CodeMirror cm;

    private AddParameterPopUp pop;
    
    CaptionPanel definitionPanel = null;
	HorizontalPanel linkPanel = null;
	HorizontalPanel objFilePanel = null;
	
	private boolean isRestricted;

	public void setRestricted(boolean isRestricted) {
		this.isRestricted = isRestricted;
	}

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		if (schema == null )
			throw new PopUpException("Schema is not set");
		
		dialogBox.setWidget(getPanel());
		
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
		this.dataProvider = (FunctionListDataProvider) provider;
		
	}

	public void setSchema(DatabaseObjectInfo schema) {
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
		lbl.setText("Add Function");
		
		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblName.setText("Function Name");

		functionName = new TextBox();
		functionName.setWidth("155px");
		
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(functionName);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		info.add(lbl);
		info.add(namePanel);
		
		returnType = new ListBox();
		returnType.setWidth("100px");

		returnTypes = new ArrayList<DataTypeInfo>();

		studioService.getList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.DATA_TYPE, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
            	returnType.clear();
            	returnTypes.clear();
                // Show the RPC error message to the user
                Window.alert(caught.getMessage());
            }

            public void onSuccess(String result) {
            	returnType.clear();
            	
    			JsArray<DataTypesJsObject> types = DataTypeInfo.json2Messages(result);

                for (int i = 0; i < types.length(); i++) {
                	DataTypeInfo info = DataTypeInfo.msgToInfo(types.get(i));                	
                	returnType.addItem(info.getName());    
                	returnTypes.add(info);                	
                }
                
                DataTypeInfo voidInfo = new DataTypeInfo(0, 0, "void");
                returnType.insertItem(voidInfo.getName(), 0);
            	returnTypes.add(0, voidInfo);                	
            	returnType.setSelectedIndex(0);
            }
          });

		Label lblReturns = new Label();
		lblReturns.setStyleName("StudioPopup-Msg");
		lblReturns.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblReturns.setText("Returns");

		HorizontalPanel returnsPanel = new HorizontalPanel();
		returnsPanel.setSpacing(10);
		returnsPanel.add(lblReturns);
		returnsPanel.add(returnType);
		returnsPanel.setCellVerticalAlignment(lblReturns, HasVerticalAlignment.ALIGN_MIDDLE);
				
		language = new ListBox();
		language.setWidth("100px");

		languages = new ArrayList<DatabaseObjectInfo>();
		
		if(!isRestricted){
			studioService.getLanguageFullList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.LANGUAGE,
					new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							language.clear();
							languages.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							language.clear();

							JsArray<ListJsObject> langs = DatabaseObjectInfo.json2Messages(result);

							for (int i = 0; i < langs.length(); i++) {
								DatabaseObjectInfo info = DatabaseObjectInfo.msgToInfo(langs.get(i));
								language.addItem(info.getName());
								languages.add(info);
							}

							language.setSelectedIndex(0);
						}
					});
		}
		else {
			studioService.getList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.LANGUAGE,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						language.clear();
						languages.clear();
						// Show the RPC error message to the user
						Window.alert(caught.getMessage());
					}

					public void onSuccess(String result) {
						language.clear();

						JsArray<ListJsObject> langs = DatabaseObjectInfo.json2Messages(result);

						for (int i = 0; i < langs.length(); i++) {
							DatabaseObjectInfo info = DatabaseObjectInfo.msgToInfo(langs.get(i));
							language.addItem(info.getName());
							languages.add(info);
						}

						language.setSelectedIndex(0);
					}
				});
		}
		
		Label lblLanguage = new Label();
		lblLanguage.setStyleName("StudioPopup-Msg");
		lblLanguage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblLanguage.setText("Language");

		HorizontalPanel languagePanel = new HorizontalPanel();
		languagePanel.setSpacing(10);
		languagePanel.add(lblLanguage);
		languagePanel.add(language);
		languagePanel.setCellVerticalAlignment(lblLanguage, HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel detailsPanel = new HorizontalPanel();
		detailsPanel.setWidth("380px");
		detailsPanel.setSpacing(5);
		detailsPanel.add(returnsPanel);
		detailsPanel.add(languagePanel);
		
		definitionPanel = new CaptionPanel("Definition");
		definitionPanel.setStyleName("StudioCaption");
		definitionPanel.add(getSQLPanel());
		definitionPanel.setVisible(false);
		
		Label lblLink = new Label();
		lblLink.setStyleName("StudioPopup-Msg");
		lblLink.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblLink.setText("Link Symbol");

		linkSymbol = new TextBox();
		linkSymbol.setWidth("155px");
		
		linkPanel = new HorizontalPanel();
		linkPanel.setSpacing(10);
		linkPanel.add(lblLink);
		linkPanel.add(linkSymbol);
		linkPanel.setVisible(true);
		linkPanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label lblObjFile = new Label();
		lblObjFile.setStyleName("StudioPopup-Msg");
		lblObjFile.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	 
		lblObjFile.setText("Object File");

		objectFile = new TextBox();
		objectFile.setWidth("155px");
		
		objFilePanel = new HorizontalPanel();
		objFilePanel.setSpacing(10);
		objFilePanel.add(lblObjFile);
		objFilePanel.add(objectFile);
		objFilePanel.setVisible(true);
		objFilePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);
		
		if(isRestricted){
			definitionPanel.setVisible(true);
			linkPanel.setVisible(false);
			objFilePanel.setVisible(false);
		}

		language.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event)	{
				String languageSel = languages.get(language.getSelectedIndex()).getName();
				
				if(!"internal".equals(languageSel) && !"C".equalsIgnoreCase(languageSel)){
					definitionPanel.setVisible(true);
					linkPanel.setVisible(false);
					objFilePanel.setVisible(false);
				}else if("internal".equals(languageSel)){
					definitionPanel.setVisible(false);
					linkPanel.setVisible(true);
					objFilePanel.setVisible(false);
				}else {
					definitionPanel.setVisible(false);
					linkPanel.setVisible(true);
					objFilePanel.setVisible(true);
				}
			}
		});
		
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
		
		Button addButton = new Button("Add");
		Button cancelButton = new Button("Cancel");
		
		bar.add(addButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(functionName.getText() != null && !functionName.getText().equals("")) {
					ArrayList<String> paramList = new ArrayList<String>();

					if(params.getRowCount()>1) {
						for(int i =1; i<params.getRowCount();i++)	{
							// Add mode
							String paramDef = params.getText(i,2);
							
							// Add name if it exists
							if (!params.getText(i,0).trim().equals("")) 
								paramDef = paramDef + " " + params.getText(i,0).trim();
							
							// Add type
							paramDef = paramDef + " " + params.getText(i,1);
							
							// Add Default if it exists
							if (!params.getText(i,3).trim().equals("")) 
								paramDef = paramDef + " DEFAULT " + params.getText(i,3).trim();
								
							//+" " + params.getText(i,0)+" "+ params.getText(i,1);
							
							paramList.add(paramDef);
						}
					}

					UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
					ac.setAutoRefresh(true);
					ac.setShowResultOutput(false);
					
					
					AlterFunctionRequest request = new AlterFunctionRequest();
					request.setNewFunctionName(functionName.getText());
					String langValue = language.getValue(language.getSelectedIndex());
					if(!"internal".equals(langValue) && !"C".equalsIgnoreCase(langValue))
						request.setNewFunctionBody(cm.getContent());
					else
						request.setNewFunctionBody(linkSymbol.getText());
					if("C".equalsIgnoreCase(langValue)){
						request.setObjectFilePath(objectFile.getText());
					}
					request.setParamsList(paramList.toArray(new String[paramList.size()]));
					request.setLanguage(langValue);
					request.setReturns(returnType.getValue(returnType.getSelectedIndex()));
					request.setSchema(schema.getName());
					
					studioService.createFunction(PgStudio.getToken(), request, ac);
				} else {
					Window.alert("Name is mandatory to create a function");
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
			
	private Widget getParameterPanel() {
		CaptionPanel panel = new CaptionPanel("Parameters");
		panel.setStyleName("StudioCaption");
		VerticalPanel vPanel = new VerticalPanel();
		ScrollPanel paramPanel = new ScrollPanel();

		Button addParam = new Button("Add");
		addParam.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pop = new AddParameterPopUp();
				
				try {
					DialogBox box = pop.getDialogBox();
					box.addCloseHandler(getAddParameterPopUpCloseHandler());
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
			}
		});

		
		params = new FlexTable();

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

	    vPanel.add(addParam);
	    vPanel.add(paramPanel);
	    
		panel.add(vPanel);
		panel.setSize("380px","120px");

		return panel.asWidget();
	}

	private CloseHandler<PopupPanel> getAddParameterPopUpCloseHandler() {
		CloseHandler<PopupPanel> handler = new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (pop.isParameterAdded()) {
					ArrayList<String> param = new ArrayList<String>();
					int init_row = params.getRowCount();

					paramIndex.add(pop.getParameterName());

					param.add(pop.getParameterName());
					param.add(pop.getDataType());
					param.add(pop.getMode());
					param.add(pop.getDefaultValue());

					for (int i = 0; i < param.size(); i++) {
						params.setText(init_row, i, param.get(i));
					}

					PushButton removeButton = new PushButton(new Image(
							PgStudio.Images.delete()));
					removeButton.setTitle("Remove this parameter");
					removeButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							int removedIndex = paramIndex.indexOf(pop
									.getParameterName());
							paramIndex.remove(removedIndex);
							params.removeRow(removedIndex + 1);
						}
					});
					params.setWidget(init_row, 4, removeButton);
				}
			}
		};

		return handler;
	}

	private Widget getSQLPanel() {
		codePanel = new SimplePanel();
				
		codeArea.setWidth("380px");
		codeArea.setHeight("150px");

		codeArea.setReadOnly(false);
		
		codePanel.add(codeArea);
		
		return codePanel.asWidget();
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
}
