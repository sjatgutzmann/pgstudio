package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.providers.ItemListProvider;

public class ImportDataPopUp implements StudioItemPopUp {

	PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	private ModelInfo item = null;

	final PopupPanel popup = new PopupPanel(false, true);
	private DialogBox dialogBox = new DialogBox();
	private Label label = new Label("Format");
	private Label file = new Label("File");
	private Button importButton = new Button();
	private Button cancelButton = new Button();
	
	@Override
	public void setItem(final ModelInfo item) {
		this.item = item;
	}

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		dialogBox.setWidget(getPanel());
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		return dialogBox;
	}

	public ImportDataPopUp() {
	}
	
	private FormPanel getPanel() {

		final Label progressLabel = new Label("Please wait while we import the data");
		
		// Create a FormPanel
		final FormPanel form = new FormPanel();
	
		// Because we're going to add a FileUpload widget,
		// we'll need to set the form to use the POST method,
		// and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		// Create a panel to hold all of the form widgets.
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("StudioPopup");
		panel.setSpacing(10);

		// HorizontalPanel hp = new HorizontalPanel();

		ListBox lb = new ListBox();

		lb.setName("listBoxFormElement");
		lb.addItem("csv", "csv");

		lb.setWidth("220");
		//label.setStyleName("StudioPopup-Msg-Strong");
		Label header = new Label("Import Data");
		header.setStyleName("StudioPopup-Msg-Strong");
		panel.add(header);
		panel.add(label);
		panel.setSpacing(4);
		panel.add(lb);

		// panel.add(hp);

		// hp = new HorizontalPanel();
		// Create a FileUpload widget.
		//file.setStyleName("StudioPopup-Msg-Strong");
		panel.add(file);

		// hp.setSpacing(4);

		final FileUpload fileUpload = new FileUpload();
		fileUpload.setName("uploadFormElement");
		panel.add(fileUpload);

		// panel.add(hp);

		// Add a 'submit' button.
		importButton.setText("Import");
		importButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				String requestParams = "connectionToken=" + PgStudio.getToken() + "&schema="
						+ PgStudio.getSelectedSchema().getName() + "&tableName=" + item.getName() + "&tableId="
						+ item.getId();
				String url = GWT.getModuleBaseURL() + "/importDataFile?" + requestParams;
				//Window.alert("request params : " + requestParams);
				//Window.alert("url : " + url);
				String filename = fileUpload.getFilename();
				if (filename.length() == 0) {
					// error.setText("No file chosen!");
					Window.alert("Please select file");
				} else {
					// submit the form
					form.setAction(url);
					form.submit();					
					
				}
				
			}
			
		});
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(importButton);
		hPanel.setSpacing(10);
		
		cancelButton.setText("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide(true);
			}
		});
		
		hPanel.add(cancelButton);
		panel.add(hPanel);
		
		// Add an event handler to the form.
		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				popup.add(progressLabel);
				popup.center(); // Center the popup and make it visible
			}
		});

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed,
				// this event is fired. Assuming the service returned
				// a response of type text/html, we can get the result
				// here.
				// Window.alert("Result....."+event.getResults());

				//message.setText(event.getResults().toString());
				popup.remove(progressLabel);
				ResultsPopUp result = new ResultsPopUp("Results", event.getResults().toString());
				try{
					result.getDialogBox();
				}catch(Exception e) {}
				popup.hide();
				dialogBox.hide(true);
				//PgStudio.dtp.refreshCurrent();
			}
		});

		form.setWidget(panel);
		
		return form;
	}

	@Override
	public void setSelectionModel(SingleSelectionModel<ModelInfo> model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataProvider(ItemListProvider provider) {
		// TODO Auto-generated method stub
		
	}

}
