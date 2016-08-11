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
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.FTSConfigurationInfo;


public class FTSMenuDataProvider extends AsyncDataProvider<FTSConfigurationInfo> implements ModelListProvider 
{
	//private FullTextSearchInfo ftsInfo = new FullTextSearchInfo();

	private List<FTSConfigurationInfo> ftsConfigurationList = new ArrayList<FTSConfigurationInfo>();

	private DatabaseObjectInfo schema = null;

	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	private final FTSMenuDataProvider me = this;

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
		getFTSData();
	}

	public List<FTSConfigurationInfo> getFTSConfigurationList()	{
		return ftsConfigurationList;
	}

	public void refresh() {
		getFTSData();
	}

	@Override
	protected void onRangeChanged(HasData<FTSConfigurationInfo> display)
	{
		getFTSData();
	}

	private void getFTSData() {
		if (schema != null) {
			try {
				studioService.getFTSConfigurations(PgStudio.getToken(), PgStudio.getSelectedSchema().getName(), new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						ftsConfigurationList.clear();
						Window.alert(caught.getMessage());
					}

					public void onSuccess(String result) {
						//ftsInfo = new FullTextSearchInfo();
						ftsConfigurationList = new ArrayList<FTSConfigurationInfo>();

						JsArray<FTSConfigurationJsObject> cols = json2Messages(result);
						if (cols != null) {
							for (int i = 0; i < cols.length(); i++) {
								FTSConfigurationJsObject col = cols.get(i);
								FTSConfigurationInfo ftsConfigInfo = new FTSConfigurationInfo(col.getName());
								ftsConfigInfo.setComment(col.getComment());
								ftsConfigurationList.add(ftsConfigInfo)	;		
							}
						}
						//ftsInfo.setFtsConfigurationInfoList(ftsConfigurationList);
						updateRowCount(ftsConfigurationList.size(), true);
						updateRowData(0, ftsConfigurationList);
					}
				});
			} catch(Exception ex) {
				Window.alert(ex.getMessage());
			}
		}
	}

	private static final native JsArray<FTSConfigurationJsObject> json2Messages(
			String json)
			/*-{
return eval(json);
}-*/;


}
