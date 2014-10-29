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
import com.openscg.pgstudio.client.messages.ActivityJsObject;
import com.openscg.pgstudio.client.models.ActivityInfo;

public class ActivityListDataProvider extends AsyncDataProvider<ActivityInfo>
		implements ListProvider {
	private List<ActivityInfo> activityList = new ArrayList<ActivityInfo>();

	private final PgStudioServiceAsync studioService = GWT
			.create(PgStudioService.class);


	public void refresh() {
		getData();
	}

	@Override
	protected void onRangeChanged(HasData<ActivityInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= activityList.size() ? activityList.size() : end;
		List<ActivityInfo> sub = activityList.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
			studioService.getActivity(PgStudio.getToken(),
					new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							activityList.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							activityList = new ArrayList<ActivityInfo>();

							JsArray<ActivityJsObject> inds = json2Messages(result);

							if (inds != null) {
								activityList.clear();

								for (int i = 0; i < inds.length(); i++) {
									ActivityJsObject ind = inds.get(i);
									activityList.add(msgToActivityInfo(ind));
								}
							}

							updateRowCount(activityList.size(), true);
							updateRowData(0, activityList);

						}
					});
	}

	private ActivityInfo msgToActivityInfo(ActivityJsObject msg) {
		int pid = Integer.parseInt(msg.getPid());

		ActivityInfo activity = new ActivityInfo(pid);

		activity.setDatName(msg.getDatName());
		activity.setUserName(msg.getUserName());
		activity.setState(msg.getState());
		activity.setClientAddr(msg.getClientAddr());
		activity.setBackendStart(msg.getBackendStart());
		activity.setXactStart(msg.getXactStart());
		activity.setQueryTime(msg.getQueryTime());

		activity.setWaiting(msg.getWaiting().equalsIgnoreCase("true"));
		activity.setQuery(msg.getQuery());
		
		return activity;
	}

	private static final native JsArray<ActivityJsObject> json2Messages(String json)
	/*-{
		return eval(json);
	}-*/;
}