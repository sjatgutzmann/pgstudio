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
import com.openscg.pgstudio.client.messages.ForeignTablesJsObject;
import com.openscg.pgstudio.client.models.DatabaseObjectInfo;
import com.openscg.pgstudio.client.models.ForeignTableInfo;

public class ForeignTableListDataProvider extends
		AsyncDataProvider<ForeignTableInfo> implements ModelListProvider {
	private List<ForeignTableInfo> tableList = new ArrayList<ForeignTableInfo>();

	private DatabaseObjectInfo schema = null;

	private final PgStudioServiceAsync studioService = GWT
			.create(PgStudioService.class);

	public void setSchema(DatabaseObjectInfo schema) {
		this.schema = schema;
		getData();
	}

	public List<ForeignTableInfo> getList() {
		return tableList;
	}

	public void refresh() {
		getData();
	}

	@Override
	protected void onRangeChanged(HasData<ForeignTableInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= tableList.size() ? tableList.size() : end;
		List<ForeignTableInfo> sub = tableList.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
		if (schema != null) {
			studioService.getList(PgStudio.getToken(), schema.getId(),
					ITEM_TYPE.FOREIGN_TABLE, new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							tableList.clear();
							// Show the RPC error message to the user
							Window.alert(caught.getMessage());
						}

						public void onSuccess(String result) {
							tableList = new ArrayList<ForeignTableInfo>();

							JsArray<ForeignTablesJsObject> tables = json2Messages(result);

							if (tables != null) {
								tableList.clear();
								for (int i = 0; i < tables.length(); i++) {
									ForeignTablesJsObject table = tables.get(i);
									tableList.add(msgToInfo(table));
								}
							}

							updateRowCount(tableList.size(), true);
							updateRowData(0, tableList);
						}
					});
		}
	}

	private ForeignTableInfo msgToInfo(ForeignTablesJsObject msg) {
		int id = Integer.parseInt(msg.getId());

		ForeignTableInfo table = new ForeignTableInfo(schema.getId(), id, msg.getName());

		table.setType(msg.getTableType());
		table.setComment(msg.getComment());

		return table;
	}

	private static final native JsArray<ForeignTablesJsObject> json2Messages(
			String json)
	/*-{
		return eval(json);
	}-*/;

}