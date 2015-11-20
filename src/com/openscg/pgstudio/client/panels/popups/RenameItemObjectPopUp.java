/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.panels.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudio.ITEM_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.handlers.UtilityCommandAsyncCallback;
import com.openscg.pgstudio.client.models.ModelInfo;
import com.openscg.pgstudio.client.providers.ItemListProvider;

public class RenameItemObjectPopUp implements StudioItemPopUp {
	
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	final DialogBox dialogBox = new DialogBox();

    private SingleSelectionModel<ModelInfo> selectionModel = null;
    private ItemListProvider dataProvider = null;
    
    private ModelInfo item = null;
    private ITEM_OBJECT_TYPE objType = null;

    private TextBox objectName;

	@Override
	public DialogBox getDialogBox() throws PopUpException {
		if (selectionModel == null)
			throw new PopUpException("Selection Model is not set");

		if (dataProvider == null)
			throw new PopUpException("Data Provider is not set");

		if (item == null)
			throw new PopUpException("Item is not set");

		try {
			dialogBox.setWidget(getPanel());	
			dialogBox.setGlassEnabled(true);
			dialogBox.center();
		} catch (Exception e) {
			throw new PopUpException(e.getMessage());
		}
		
		return dialogBox;
	}

	@Override
	public void setSelectionModel(SingleSelectionModel model) {
		this.selectionModel = model;
	}

	@Override
	public void setDataProvider(ItemListProvider provider) {
		this.dataProvider = (ItemListProvider) provider;
		
	}

	public void setItem(ModelInfo item) {
		this.item = item;
	}
	
    public void setObjectType(ITEM_OBJECT_TYPE objType) {
		this.objType = objType;
	}

	private VerticalPanel getPanel() throws PopUpException{
		VerticalPanel panel = new VerticalPanel();

		try {
			panel.setStyleName("StudioPopup");
	
			VerticalPanel info = new VerticalPanel();
			info.setSpacing(10);
			
			Label lbl = new Label();
			lbl.setStyleName("StudioPopup-Msg-Strong");
			lbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);	 

			String title = "RENAME " + objType.name();
			lbl.setText(title);

			objectName = new TextBox();
			objectName.setWidth("155px");
			objectName.setText(selectionModel.getSelectedObject().getName());
			
			info.add(lbl);
			info.add(objectName);
			
			panel.add(info);
			panel.add(getButtonPanel());
		} catch (Exception e) {
			throw new PopUpException(e.getMessage());
		}
		
		return panel;
	}
	
	private Widget getButtonPanel(){
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("50px");
		bar.setSpacing(10);
		
		Button renameButton = new Button("Rename");
		Button cancelButton = new Button("Cancel");
		
		bar.add(renameButton);
		bar.add(cancelButton);
		
		bar.setCellHorizontalAlignment(renameButton, HasHorizontalAlignment.ALIGN_CENTER);
		bar.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

		renameButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectionModel.getSelectedObject() != null
						&& !"".equals(selectionModel.getSelectedObject()
								.getName())) {
					if (objectName.getText() != null
							&& !objectName.getText().equals("")) {
						
						UtilityCommandAsyncCallback ac = new UtilityCommandAsyncCallback(
								dialogBox, dataProvider);
						ac.setAutoRefresh(true);
						ac.setShowResultOutput(false);

						studioService.renameItemObject(PgStudio.getToken(),
								item.getId(), item.getItemType(), selectionModel
										.getSelectedObject().getName(),
										objType, objectName.getText(), ac);
					} else {
						Window.alert("Enter new name");
					}
				}
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				objectName.setText("");
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
