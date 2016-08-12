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
import com.openscg.pgstudio.client.PgStudio.ITEM_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.messages.ConstraintJsObject;
import com.openscg.pgstudio.client.models.ConstraintInfo;

public class ConstraintListDataProvider extends
		AsyncDataProvider<ConstraintInfo> implements ItemListProvider {
	private List<ConstraintInfo> constList = new ArrayList<ConstraintInfo>();

	private int schema = -1;
	private long item = -1;

	private final PgStudioServiceAsync studioService = GWT
			.create(PgStudioService.class);

	@Override
	public void setItem(int schema, long item, ITEM_TYPE type) {
		this.item = item;
		this.schema = schema;

		getData();
	}

	public void refresh() {
		getData();
	}

	@Override
	protected void onRangeChanged(HasData<ConstraintInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= constList.size() ? constList.size() : end;
		List<ConstraintInfo> sub = constList.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
		if (item > 0) {
			studioService.getItemObjectList(PgStudio.getToken(), item,
					ITEM_TYPE.TABLE, ITEM_OBJECT_TYPE.CONSTRAINT,
					new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							constList.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							constList = new ArrayList<ConstraintInfo>();

							JsArray<ConstraintJsObject> inds = json2Messages(result);

							if (inds != null) {
								constList.clear();

								for (int i = 0; i < inds.length(); i++) {
									ConstraintJsObject ind = inds.get(i);
									constList.add(msgToConstraintInfo(ind));
								}
							}

							updateRowCount(constList.size(), true);
							updateRowData(0, constList);

						}
					});
		}
	}

	private ConstraintInfo msgToConstraintInfo(ConstraintJsObject msg) {

		ConstraintInfo c = new ConstraintInfo(schema, Integer.parseInt(msg
				.getId()), msg.getConstraintName());

		c.setType(msg.getConstraintType());
		c.setDeferrable(msg.getDeferrable().equalsIgnoreCase("true"));
		c.setDeferred(msg.getDeferred().equalsIgnoreCase("true"));
		c.setIndexName(msg.getIndexName());
		c.setOwner(msg.getIndexOwner());
		c.setUpdateType(msg.getUpdateAction());
		c.setDeleteType(msg.getDeleteAction());
		c.setDefinition(msg.getDefinition());

		return c;
	}

	private static final native JsArray<ConstraintJsObject> json2Messages(
			String json)
	/*-{
		return eval(json);
	}-*/;
}
