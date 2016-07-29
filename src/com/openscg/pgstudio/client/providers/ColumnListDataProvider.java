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
import com.openscg.pgstudio.client.messages.ColumnJsObject;
import com.openscg.pgstudio.client.models.ColumnInfo;

public class ColumnListDataProvider extends AsyncDataProvider<ColumnInfo>
implements ItemListProvider {
	private List<ColumnInfo> columnList = new ArrayList<ColumnInfo>();

	private int item = -1;
	private int schema = -1;
	private ITEM_TYPE type;

	private final PgStudioServiceAsync studioService = GWT
			.create(PgStudioService.class);

	public void setItem(int schema, int item, ITEM_TYPE type) {
		this.schema = schema;
		this.item = item;
		this.type = type;

		getData();
	}

	public void refresh() {
		getData();
	}

	@Override
	protected void onRangeChanged(HasData<ColumnInfo> display) {
		getData();

		int start = display.getVisibleRange().getStart();
		int end = start + display.getVisibleRange().getLength();
		end = end >= columnList.size() ? columnList.size() : end;
		List<ColumnInfo> sub = columnList.subList(start, end);
		updateRowData(start, sub);

	}

	private void getData() {
		if (item > 0) {
			studioService.getItemObjectList(PgStudio.getToken(), item, type,
					ITEM_OBJECT_TYPE.COLUMN, new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					columnList.clear();
					Window.alert(caught.getMessage());
				}

				public void onSuccess(String result) {
					columnList = new ArrayList<ColumnInfo>();

					JsArray<ColumnJsObject> cols = json2Messages(result);

					if (cols != null) {
						columnList.clear();

						for (int i = 0; i < cols.length(); i++) {
							ColumnJsObject col = cols.get(i);
							columnList.add(msgToColumnInfo(col));
						}
					}

					updateRowCount(columnList.size(), true);
					updateRowData(0, columnList);

				}
			});
		}
	}

	private ColumnInfo msgToColumnInfo(ColumnJsObject msg) {
		int id = Integer.parseInt(msg.getId());

		ColumnInfo column = new ColumnInfo(schema, id, msg.getColumnName());

		column.setDataType(msg.getDataType());
		column.setDefault(msg.getDefaultValue());
		column.setComment(msg.getComment());

		if (msg.getDistributionKey().equalsIgnoreCase("true")) {
			column.setDistributionKey(true);
		} else {
			column.setDistributionKey(false);
		}

		if (msg.getPrimaryKey().equalsIgnoreCase("true")) {
			column.setPrimaryKey(true);
		} else {
			column.setPrimaryKey(false);
		}

		if (msg.getNullable().equalsIgnoreCase("true")) {
			column.setNullable(true);
		} else {
			column.setNullable(false);
		}

		return column;
	}

	private static final native JsArray<ColumnJsObject> json2Messages(
			String json)
			/*-{
		return eval(json);
	}-*/;

	public void clearColumnData() {
		columnList = new ArrayList<ColumnInfo>();
		columnList.clear();
		updateRowCount(columnList.size(), true);
		updateRowData(0, columnList);
	}

}