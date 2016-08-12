package com.openscg.pgstudio.server.models.export;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.models.Database;

public abstract class AbstractDataExporter {

	private final Connection conn;

	public AbstractDataExporter(Connection conn) {
		this.conn = conn;
	}

	public Object getData(String query) throws SQLException {
		if (query == null || query.isEmpty())
			return null;
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		return exportData(rs);
	}

	public Object getData(String item, String itemType) throws SQLException {
		ITEM_TYPE type = null;
		if (itemType != null) {
			type = ITEM_TYPE.valueOf(itemType);
		}
		if (type!= null && type != ITEM_TYPE.TABLE && type != ITEM_TYPE.VIEW && type != ITEM_TYPE.FOREIGN_TABLE) {
			return null;
		}

		Database db = new Database(conn);
		long itm = Long.parseLong(item);
		String name = db.getItemFullName(itm, type);
		Object response = null;
		String sql = "SELECT * FROM " + name;
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		response = exportData(rs);

		return response;
	}

	protected abstract Object exportData(ResultSet rs);

	protected Connection getConn() {
		return conn;
	}
}
