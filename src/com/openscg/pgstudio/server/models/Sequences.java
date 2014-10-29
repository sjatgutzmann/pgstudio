/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.util.QueryExecutor;
import com.openscg.pgstudio.shared.DatabaseConnectionException;

public class Sequences {

	private final Connection conn;

	private final static String SEQ_LIST = "SELECT c.relname, c.oid "
			+ "  FROM pg_class c " + " WHERE c.relnamespace = ? "
			+ "   AND c.relkind = 'S' " + " ORDER BY relname ";

	public Sequences(Connection conn) {
		this.conn = conn;
	}

	public String getList(int schema) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(SEQ_LIST);
			stmt.setInt(1, schema);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Integer.toString(rs.getInt("oid")));
				jsonMessage.put("name", rs.getString("relname"));

				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String create(int schema, String sequenceName,
			boolean temporary, int increment, int minValue, int maxValue,
			int start, int cache, boolean cycle)
			throws DatabaseConnectionException, SQLException {

		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);

		StringBuffer query;

		if (temporary)
			query = new StringBuffer("CREATE TEMPORARY SEQUENCE " + schemaName
					+ "." + sequenceName);
		else
			query = new StringBuffer("CREATE SEQUENCE " + schemaName + "."
					+ sequenceName);

		if (increment != 0 && increment != 1) {
			query = query.append(" INCREMENT BY " + increment);
		}

		if (minValue != 0 && minValue != 1) {
			query = query.append(" MINVALUE " + minValue);
		}

		if (maxValue != 0 && maxValue != 1) {
			query = query.append(" MAXVALUE " + maxValue);
		}

		if (start != 0 && start != 1) {
			query = query.append(" START WITH " + start);
		}

		if (cache != 0 && cache != 1) {
			query = query.append(" CACHE " + cache);
		}

		if (cycle)
			query = query.append(" CYCLE");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(query.toString());
	}

	public String drop(int item, boolean cascade) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.SEQUENCE);

		StringBuffer command = new StringBuffer("DROP SEQUENCE " + name);

		if (cascade)
			command.append(" CASCADE");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}
}
