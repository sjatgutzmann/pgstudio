/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mastergaurav.codemirror.client.CodeMirror;
import com.mastergaurav.codemirror.client.CodeMirrorConfig;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.models.ModelInfo;

public class ScriptPanel extends Composite implements DetailsPanel {

	private SimplePanel codePanel;
	private final TextArea codeArea = new TextArea();
	private CodeMirror cm;
	
	private boolean isConfigured;
	private ITEM_TYPE type = null;
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
			
			
			isConfigured = true;
			this.type = item.getItemType();

		}
		
		PgStudio.studioService.getItemObjectList(PgStudio.getToken(), item.getId(), type, ITEM_OBJECT_TYPE.SOURCE, new AsyncCallback<String>() {
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
	
	public ScriptPanel() {
		
		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.add(getButtonBar());
		mainPanel.add(getMainPanel());
				
		initWidget(mainPanel);
	}

	private Widget getButtonBar() {
		HorizontalPanel bar = new HorizontalPanel();
		
		PushButton refresh = getRefreshButton();
		
		bar.add(refresh);
		
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
	
	private Widget getMainPanel() {
		codePanel = new SimplePanel();
				
		codeArea.setWidth("95%");
		codeArea.setHeight(PgStudio.RIGHT_PANEL_HEIGHT);

		codeArea.setReadOnly(true);
		
		isConfigured = false;
		
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

}
