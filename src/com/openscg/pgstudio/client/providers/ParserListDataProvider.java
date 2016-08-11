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
import com.openscg.pgstudio.client.messages.ParserJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.ParserInfo;

public class ParserListDataProvider extends AsyncDataProvider<ParserInfo> implements ModelListProvider {
	private List<ParserInfo> parserList = new ArrayList<ParserInfo>();

	private DatabaseObjectInfo schema = null;

	private final PgStudioServiceAsync studioService = GWT.create(PgStudioService.class);

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
		getData();
	}

	public List<ParserInfo> getList() {
		return parserList;
	}

	public void refresh() {
		getData();
	}

	@Override
	protected void onRangeChanged(HasData<ParserInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= parserList.size() ? parserList.size() : end;
		List<ParserInfo> sub = parserList.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
		if (schema != null) {
			studioService.getList(PgStudio.getToken(), schema.getId(), ITEM_TYPE.PARSER,
					new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							parserList.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							parserList = new ArrayList<ParserInfo>();
							JsArray<ParserJsObject> pars = json2Messages(result);

							if (pars != null) {
								parserList.clear();

								for (int i = 0; i < pars.length(); i++) {
									ParserJsObject dict = pars.get(i);
									parserList.add(msgToTypeInfo(dict));
								}
							}

							updateRowCount(parserList.size(), true);
							updateRowData(0, parserList);

						}
					});
		}
	}

	private ParserInfo msgToTypeInfo(ParserJsObject msg) {
		int id = Integer.parseInt(msg.getId());

		ParserInfo type = new ParserInfo(schema.getId(), msg.getSchema(), id, msg.getName(), msg.getComment());
		return type;
	}

	private static final native JsArray<ParserJsObject> json2Messages(String json)
	/*-{ 
	  	return eval(json); 
	}-*/;

}