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
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.messages.StatsJsObject;
import com.openscg.pgstudio.client.models.StatsInfo;

public class StatsListDataProvider extends AsyncDataProvider<StatsInfo> {
	private List<StatsInfo> statsList = new ArrayList<StatsInfo>();

	private int item = -1;
	private ITEM_TYPE type;

	private final PgStudioServiceAsync studioService = GWT
			.create(PgStudioService.class);

	public void setItem(int item, ITEM_TYPE type) {
		this.item = item;

		getData();
	}

	@Override
	protected void onRangeChanged(HasData<StatsInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= statsList.size() ? statsList.size() : end;
		List<StatsInfo> sub = statsList.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
		if (item > 0) {
			studioService.getItemObjectList(PgStudio.getToken(), item, type,
					ITEM_OBJECT_TYPE.STATS, new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							statsList.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							statsList = new ArrayList<StatsInfo>();

							JsArray<StatsJsObject> stats = json2Messages(result);

							if (stats != null) {
								statsList.clear();

								for (int i = 0; i < stats.length(); i++) {
									StatsJsObject stat = stats.get(i);
									statsList.add(msgToColumnInfo(stat));
								}
							}

							updateRowCount(statsList.size(), true);
							updateRowData(0, statsList);

						}
					});
		}
	}

	private StatsInfo msgToColumnInfo(StatsJsObject msg) {

		StatsInfo stat = new StatsInfo(msg.getName(), msg.getValue());

		return stat;
	}

	private static final native JsArray<StatsJsObject> json2Messages(String json)
	/*-{
		return eval(json);
	}-*/;

}