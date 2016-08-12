/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.providers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.PgStudio.ITEM_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.messages.PrivilegeJsObject;
import com.openscg.pgstudio.client.models.PrivilegeInfo;

public class PrivilegeListDataProvider extends AsyncDataProvider<PrivilegeInfo>
		implements ItemListProvider {
	private List<PrivilegeInfo> privList = new ArrayList<PrivilegeInfo>();

	private long item = -1;
	private int schema = -1;
	private ITEM_TYPE type;

	private final PgStudioServiceAsync studioService = GWT
			.create(PgStudioService.class);

	public void setItem(int schema, long item, ITEM_TYPE type) {
		this.item = item;
		this.schema = schema;
		this.type = type;

		getData();
	}

	public void refresh() {
		getData();
	}

	@Override
	protected void onRangeChanged(HasData<PrivilegeInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= privList.size() ? privList.size() : end;
		List<PrivilegeInfo> sub = privList.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
		if (item > 0) {
			studioService.getItemObjectList(PgStudio.getToken(), item, type,
					ITEM_OBJECT_TYPE.GRANT, new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							privList.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							privList = new ArrayList<PrivilegeInfo>();

							JsArray<PrivilegeJsObject> inds = json2Messages(result);

							if (inds != null) {
								privList.clear();

								for (int i = 0; i < inds.length(); i++) {
									PrivilegeJsObject ind = inds.get(i);
									privList.add(msgToInfo(ind));
								}
							}

							updateRowCount(privList.size(), true);
							updateRowData(0, privList);

						}
					});
		}
	}

	private PrivilegeInfo msgToInfo(PrivilegeJsObject msg) {

		PrivilegeInfo priv = new PrivilegeInfo(msg.getGrantee(), msg.getType());

		priv.setGrantor(msg.getGrantor());
		priv.setGrantable(msg.getGrantable().equalsIgnoreCase("yes"));

		return priv;
	}

	private static final native JsArray<PrivilegeJsObject> json2Messages(
			String json)
	/*-{
		return eval(json);
	}-*/;

}
