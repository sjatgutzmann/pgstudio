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
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.PgStudioService;
import com.openscg.pgstudio.client.PgStudioServiceAsync;
import com.openscg.pgstudio.client.messages.DictionaryJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.DictionaryInfo;

public class DictionaryListDataProvider extends AsyncDataProvider<DictionaryInfo> implements ModelListProvider {
	private List<DictionaryInfo> dictionaryList = new ArrayList<DictionaryInfo>();

	private DatabaseObjectInfo schema = null;

	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
		getData();
	}

	public List<DictionaryInfo> getList() {
		return dictionaryList;
	}

	public void refresh() {
		getData();
	}

	@Override
	protected void onRangeChanged(HasData<DictionaryInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= dictionaryList.size() ? dictionaryList.size() : end;
		List<DictionaryInfo> sub = dictionaryList.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
		if (schema != null) {
			studioService.getList(PgStudio.getToken(), schema.getId(), ITEM_TYPE.DICTIONARY,
					new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							dictionaryList.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							dictionaryList = new ArrayList<DictionaryInfo>();

							JsArray<DictionaryJsObject> dicts = json2Messages(result);

							if (dicts != null) {
								dictionaryList.clear();

								for (int i = 0; i < dicts.length(); i++) {
									DictionaryJsObject dict = dicts.get(i);
									dictionaryList.add(msgToTypeInfo(dict));
								}
							}

							updateRowCount(dictionaryList.size(), true);
							updateRowData(0, dictionaryList);

						}
					});
		}
	}

	private DictionaryInfo msgToTypeInfo(DictionaryJsObject msg) {
		int id = Integer.parseInt(msg.getId());

		DictionaryInfo type = new DictionaryInfo(schema.getId(), msg.getSchema(), id, msg.getName(), msg.getComment(), msg.getOptions());
		return type;
	}

	private static final native JsArray<DictionaryJsObject> json2Messages(String json)
	/*-{ 
	  	return eval(json); 
	}-*/;

}