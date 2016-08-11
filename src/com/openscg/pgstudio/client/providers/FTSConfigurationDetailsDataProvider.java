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
import com.openscg.pgstudio.client.messages.FTSConfigurationJsObject;
import com.openscg.pgstudio.client.models.FTSConfigurationDetailsInfo;

public class FTSConfigurationDetailsDataProvider extends AsyncDataProvider<FTSConfigurationDetailsInfo> implements ListProvider
{
	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);
	private List<FTSConfigurationDetailsInfo> ftsConfigInfoList = new ArrayList<FTSConfigurationDetailsInfo>();
	private FTSConfigurationDetailsInfo item = null;


	public void setItem(FTSConfigurationDetailsInfo item ) {
		this.item = item;
		getData();
	}

	public void refresh() {
		getData();
	}

	@Override
	protected void onRangeChanged(HasData<FTSConfigurationDetailsInfo> display) {
		getData();
		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= ftsConfigInfoList.size() ? ftsConfigInfoList.size() : end;
		List<FTSConfigurationDetailsInfo> sub = ftsConfigInfoList.subList(start, end);
		updateRowData(start, sub);
	}

	public void getData() {
		try {
			if(item != null) { 
			studioService.getFTSConfigurationDetails(PgStudio.getToken(), PgStudio.getSelectedSchema().getName(), item.getConfigName(), new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					ftsConfigInfoList.clear();
					Window.alert(caught.getMessage());
				}

				public void onSuccess(String result) {
					if(result != null && !"".equals(result)) {
						ftsConfigInfoList = new ArrayList<FTSConfigurationDetailsInfo>();
						JsArray<FTSConfigurationJsObject> jsArr = json2Messages(result);
						if (jsArr != null) {
							ftsConfigInfoList.clear();
							for (int i = 0; i < jsArr.length(); i++) {
								FTSConfigurationJsObject jsObj = jsArr.get(i);
								ftsConfigInfoList.add(msgToFTSMappingInfo(jsObj));
							}
						}
					}
					updateRowCount(ftsConfigInfoList.size(), true);
					updateRowData(0, ftsConfigInfoList);
				}
			}); }
		} catch(Exception ex) {
			Window.alert(ex.getMessage());
		}
	}

	public List<FTSConfigurationDetailsInfo> getFtsConfigInfoList() {
		return ftsConfigInfoList;
	}

	private FTSConfigurationDetailsInfo msgToFTSMappingInfo(FTSConfigurationJsObject msg) {
		FTSConfigurationDetailsInfo ftsConfInfo = new FTSConfigurationDetailsInfo(item.getConfigName());
		ftsConfInfo.setToken(msg.getToken());
		ftsConfInfo.setDictionaries(msg.getDictionaries());
		ftsConfInfo.setComment(msg.getComment());
		return ftsConfInfo;
	}

	public void clearColumnData() {
		ftsConfigInfoList = new ArrayList<FTSConfigurationDetailsInfo>();
		ftsConfigInfoList.clear();
		updateRowCount(ftsConfigInfoList.size(), true);
		updateRowData(0, ftsConfigInfoList);
	}

	private static final native JsArray<FTSConfigurationJsObject> json2Messages(
			String json)
			/*-{
return eval(json);
}-*/;







}