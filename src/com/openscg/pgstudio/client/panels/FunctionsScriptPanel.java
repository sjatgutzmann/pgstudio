/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mastergaurav.codemirror.client.CodeMirror;
import com.mastergaurav.codemirror.client.CodeMirrorConfig;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_OBJECT_TYPE;
import com.openscg.pgstudio.client.messages.CommandJsObject;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.panels.popups.AddParameterPopUp;
import com.openscg.pgstudio.client.panels.popups.EditFunctionPopUp;
import com.openscg.pgstudio.client.panels.popups.PopUpException;
import com.openscg.pgstudio.shared.Constants;

public class FunctionsScriptPanel extends Composite implements DetailsPanel {

	private SimplePanel codePanel;
	private final TextArea codeArea = new TextArea();
	private CodeMirror cm;
	private ModelInfo item;
	private ModelInfo previousItem;
	
	
	public void setItem(ModelInfo item) {
		this.previousItem = this.item;
		this.item = item;
		
		if (this.previousItem == null || this.previousItem.getFullName() != item.getFullName()) {
			Element e = codeArea.getElement();
			TextAreaElement tae = TextAreaElement.as(e);
			
			CodeMirrorConfig config = getConfig();

			if (cm != null) {
				Element e1 = cm.getElement();
				e1.removeFromParent();
			}
			
			cm = CodeMirror.fromTextArea(tae, config);

		}
		
		PgStudio.studioService.getItemObjectList(PgStudio.getToken(), item.getId(), item.getItemType(), ITEM_OBJECT_TYPE.SOURCE, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
            	cm.setContent("");
                // Show the RPC error message to the user
                Window.alert(caught.getMessage());
            }

            public void onSuccess(String result) {
            	cm.setContent(result);
                // force a redraw
                codePanel.setVisible(true);
            }
          });		
		
	}
	
	public FunctionsScriptPanel() {
		
		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());
		
		initWidget(mainPanel);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		
		PushButton refresh = getRefreshButton();
		
		PushButton edit = getEditButton();
		//PushButton alterButton = getAlterButton();
		
		bar.add(refresh);
		
		bar.add(edit);
		
		//bar.add(alterButton);
		
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
	
	private PushButton getEditButton() {
		PushButton edit = new PushButton(new Image(PgStudio.Images.edit()));
		edit.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				EditFunctionPopUp pop = new EditFunctionPopUp(FunctionsScriptPanel.this);				
				
				String function = cm.getContent();
				
				String functionStart = ".";
				String functionEnd = "(";
				String argsStart = functionEnd;
				String argsEnd = ")";
				
				String schema = function.substring(Constants.CREATE_FUNCTION.length(), function.indexOf(functionStart));
				String functionName = function.substring(function.indexOf(functionStart) + functionStart.length() , function.indexOf(functionEnd)).replace("\"", "");
				String params[] = function.substring(function.indexOf(argsStart) + argsStart.length(), function.indexOf(argsEnd)).split(",");
				//Window.alert("1 : " + params[0]);
				String returnType = function.substring(function.indexOf(Constants.RETURNS) + Constants.RETURNS.length(), function.indexOf(Constants.AS_$$));
				String code = function.substring(function.indexOf(Constants.AS_$$) + Constants.AS_$$.length(), function.indexOf(Constants.LANGUAGE_$$));
				String language = function.substring(function.indexOf(Constants.LANGUAGE_$$) + Constants.LANGUAGE_$$.length(), function.length() - 1);
				
				pop.setFunctionName(functionName);
				pop.setParameters(params);
				pop.setFunctionCode(code);
				if("C".equalsIgnoreCase(language)){
					String objectFile = code.split(",")[0];
					String linkSymbol = code.split(",")[1];
					pop.setFunctionCode(linkSymbol);
					pop.setObjectFilePath(objectFile);
				}
				pop.setReturns(returnType);
				pop.setLanguage(language);
				pop.setSchema(schema);
				//Window.alert("2");
				try {				
					pop.getDialogBox();					
				} catch (PopUpException caught) {
					Window.alert(caught.getMessage());
				}
				pop.setupCodePanel();
				
				
			}
		});
		return edit;
	}
	
	private Widget getMainPanel() {
		codePanel = new SimplePanel();
				
		codeArea.setWidth("95%");
		codeArea.setHeight(PgStudio.RIGHT_PANEL_HEIGHT);

		codeArea.setReadOnly(true);
		
		codePanel.add(codeArea);		
		
		return codePanel.asWidget();
	}
	
	private CodeMirrorConfig getConfig() {
		CodeMirrorConfig config = CodeMirrorConfig.getDefault();
		
		String parserFile = GWT.getModuleBaseURL() + "cm/contrib/sql/js/parsesql.js";
		String styleSheet = GWT.getModuleBaseURL() + "cm/contrib/sql/css/sqlcolors.css";
		
		config.setParserFile(parserFile);
		config.setStylesheet(styleSheet);
		
		config.setWidth(95, Unit.PCT);
		config.setHeight(385, Unit.PX);
		
		return config;
	}

	@Override
	public void refresh() {
		setItem(item);
	}

	private static final native JsArray<CommandJsObject> json2Messages(String json)
	/*-{ 
	  	return eval(json); 
	}-*/;
}
