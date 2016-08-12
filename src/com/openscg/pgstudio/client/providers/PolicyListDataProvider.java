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
import com.openscg.pgstudio.client.messages.PolicyJsObject;
import com.openscg.pgstudio.client.models.PolicyInfo;

public class PolicyListDataProvider extends AsyncDataProvider<PolicyInfo> implements ItemListProvider {
	private List<PolicyInfo> list = new ArrayList<PolicyInfo>();

	private long item = -1;
	private int schema = -1;
	private ITEM_TYPE type;

	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

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
	protected void onRangeChanged(HasData<PolicyInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= list.size() ? list.size() : end;
		List<PolicyInfo> sub = list.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
		if (item > 0) {
			studioService.getItemObjectList(PgStudio.getToken(), item, type, ITEM_OBJECT_TYPE.POLICY, new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					list.clear();
					// Show the RPC error message to the user
					Window.alert(caught.getMessage());
				}

				public void onSuccess(String result) {
					list = new ArrayList<PolicyInfo>();

					JsArray<PolicyJsObject> inds = json2Messages(result);

					if (inds != null) {
						list.clear();

						for (int i = 0; i < inds.length(); i++) {
							PolicyJsObject ind = inds.get(i);
							list.add(msgToInfo(ind));
						}
					}

					updateRowCount(list.size(), true);
					updateRowData(0, list);

				}
			});
		}
	}

	private PolicyInfo msgToInfo(PolicyJsObject msg) {

		PolicyInfo pol = new PolicyInfo(schema, Integer.parseInt(msg.getId()), msg.getName());

		pol.setCommand(msg.getCommand());
		pol.setRoles(msg.getRoles());
		pol.setUsing(msg.getUsing());
		pol.setWithCheck(msg.getWithCheck());

		return pol;
	}

	private static final native JsArray<PolicyJsObject> json2Messages(String json)
	/*-{
		return eval(json);
	}-*/;

}
