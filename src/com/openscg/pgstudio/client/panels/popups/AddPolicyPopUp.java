/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.openscg.pgstudio.client.PgStudio.DATABASE_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.messages.ListJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.models.PolicyInfo;
import com.openscg.pgstudio.client.providers.ItemListProvider;
import com.openscg.pgstudio.client.providers.PolicyListDataProvider;

public class AddPolicyPopUp implements StudioItemPopUp {

	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);
	final DialogBox dialogBox = new DialogBox();

	private SingleSelectionModel<PolicyInfo> selectionModel = null;
	private PolicyListDataProvider dataProvider = null;

	private ModelInfo item = null;

	private TextBox policyName;

	private TextArea readExpression;
	private TextArea writeExpression;
	private ListBox cmdList;
	private ListBox roleList;

	private CaptionPanel readExpressionPanel;
	private CaptionPanel writeExpressionPanel;

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		if (item == null)
			throw new PopUpException("Table is not set");

		dialogBox.setWidget(getPanel());

		dialogBox.setGlassEnabled(true);
		dialogBox.center();

		return dialogBox;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectionModel(@SuppressWarnings("rawtypes") SingleSelectionModel model) {
		this.selectionModel = model;
	}

	@Override
	public void setDataProvider(ItemListProvider provider) {
		this.dataProvider = (PolicyListDataProvider) provider;

	}

	public void setItem(ModelInfo item) {
		this.item = item;
	}

	private VerticalPanel getPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");

		VerticalPanel info = new VerticalPanel();
		info.setSpacing(10);

		Label lbl = new Label();
		lbl.setStyleName("StudioPopup-Msg-Strong");
		lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lbl.setText("Add Policy");

		Label lblName = new Label();
		lblName.setStyleName("StudioPopup-Msg");
		lblName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		lblName.setText("Policy Name");

		policyName = new TextBox();
		policyName.setWidth("155px");

		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(10);
		namePanel.add(lblName);
		namePanel.add(policyName);
		namePanel.setCellVerticalAlignment(lblName, HasVerticalAlignment.ALIGN_MIDDLE);

		cmdList = new ListBox();
		cmdList.setWidth("220px");
		cmdList.clear();
		cmdList.addItem("ALL");
		cmdList.addItem("SELECT");
		cmdList.addItem("INSERT");
		cmdList.addItem("UPDATE");
		cmdList.addItem("DELETE");
		cmdList.setSelectedIndex(0);

		cmdList.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				String value = cmdList.getValue(cmdList.getSelectedIndex());

				if (value.equals("ALL") || value.equals("UPDATE")) {
					readExpressionPanel.setVisible(true);
					writeExpressionPanel.setVisible(true);
				} else if (value.equals("SELECT") || value.equals("DELETE")) {
					readExpressionPanel.setVisible(true);
					writeExpressionPanel.setVisible(false);
				} else {
					readExpressionPanel.setVisible(false);
					writeExpressionPanel.setVisible(true);
				}

			}
		});

		HorizontalPanel cmdPanel = new HorizontalPanel();
		cmdPanel.add(cmdList);
		cmdPanel.setCellVerticalAlignment(cmdList, HasVerticalAlignment.ALIGN_MIDDLE);

		CaptionPanel cmdCaptionPanel = new CaptionPanel("Command");
		cmdCaptionPanel.setStyleName("StudioCaption");
		cmdCaptionPanel.add(cmdPanel);

		roleList = new ListBox();
		roleList.setWidth("220px");

		studioService.getList(PgStudio.getToken(), DATABASE_OBJECT_TYPE.ROLE, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				roleList.clear();
				// Show the RPC error message to the user
				Window.alert(caught.getMessage());
			}

			public void onSuccess(String result) {
				roleList.clear();
				roleList.addItem("PUBLIC");

				JsArray<ListJsObject> roles = DatabaseObjectInfo.json2Messages(result);

				for (int i = 0; i < roles.length(); i++) {
					DatabaseObjectInfo info = DatabaseObjectInfo.msgToInfo(roles.get(i));
					roleList.addItem(info.getName());
				}
			}
		});

		roleList.setSelectedIndex(0);

		HorizontalPanel rolePanel = new HorizontalPanel();
		rolePanel.add(roleList);
		rolePanel.setCellVerticalAlignment(roleList, HasVerticalAlignment.ALIGN_MIDDLE);

		CaptionPanel roleCaptionPanel = new CaptionPanel("Role");
		roleCaptionPanel.setStyleName("StudioCaption");
		roleCaptionPanel.add(rolePanel);

		readExpressionPanel = new CaptionPanel("Read Expression (Using)");
		readExpressionPanel.setStyleName("StudioCaption");
		readExpression = new TextArea();
		readExpression.setVisibleLines(2);
		readExpression.setWidth("220px");
		readExpressionPanel.add(readExpression);

		writeExpressionPanel = new CaptionPanel("Write Expression (With Check)");
		writeExpressionPanel.setStyleName("StudioCaption");
		writeExpression = new TextArea();
		writeExpression.setVisibleLines(2);
		writeExpression.setWidth("220px");
		writeExpressionPanel.add(writeExpression);

		info.add(lbl);
		info.add(namePanel);
		info.add(cmdCaptionPanel);
		info.add(roleCaptionPanel);
		info.add(readExpressionPanel);
		info.add(writeExpressionPanel);

		panel.add(info);
		panel.add(getButtonPanel());

		panel.setCellHorizontalAlignment(info, HasHorizontalAlignment.ALIGN_CENTER);
		return panel;
	}

	private Widget getButtonPanel() {
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		bar.setWidth("350px");

		Button addButton = new Button("Add");
		Button cancelButton = new Button("Cancel");

		bar.add(addButton);
		bar.add(cancelButton);

		bar.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_RIGHT);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_LEFT);

		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (policyName.getText() != null && !policyName.getText().equals("")
						&& roleList.getValue(roleList.getSelectedIndex()) != null
						&& !roleList.getValue(roleList.getSelectedIndex()).equals("")) {

					UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(dialogBox, dataProvider);
					ac.setAutoRefresh(true);
					ac.setShowResultOutput(false);

					studioService.createPolicy(PgStudio.getToken(), item.getId(), policyName.getValue(),
							cmdList.getValue(cmdList.getSelectedIndex()), roleList.getValue(roleList.getSelectedIndex()),
							readExpression.getValue(), writeExpression.getValue(), ac);
				} else {
					Window.alert("Name and Role are mandatory to create a policy");
				}
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				policyName.setText("");
				refresh();
				dialogBox.hide(true);
			}
		});

		return bar.asWidget();
	}

	private void refresh() {
		dataProvider.setItem(item.getSchema(), item.getId(), item.getItemType());
	}
}
