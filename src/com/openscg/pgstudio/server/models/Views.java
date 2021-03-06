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

public class Views {
	
	private final Connection conn;
	
	private final static String VIEW_LIST = 
		"SELECT c.relname, c.relkind, d.description, c.oid " +
		"  FROM pg_class c " +
		"       LEFT OUTER JOIN pg_description d" +
		"         ON (c.oid = d.objoid AND d.objsubid = 0) " +
		" WHERE c.relnamespace = ? " +
		"   AND c.relkind in ('v', 'm') " +
		" ORDER BY 1 ";

	
	public Views(Connection conn) {
		this.conn = conn;
	}
	
	public String getList(int schema) {
		JSONArray result = new JSONArray();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(VIEW_LIST);
			stmt.setInt(1, schema);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));

				jsonMessage.put("name", rs.getString("relname"));
				
				if (rs.getString("relkind").equals("m"))
					jsonMessage.put("is_materialized", "true");
				else
					jsonMessage.put("is_materialized", "false");
										
				jsonMessage.put("comment", rs.getString("description"));

				
				result.add(jsonMessage);
			}
			
		} catch (SQLException e) {
			return "";
		}
		
		return result.toString();
	}
	
	public String dropView(long item, boolean cascade, boolean isMaterialized) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.VIEW);

		StringBuffer command = new StringBuffer();
		
		if (isMaterialized)
			command.append("DROP MATERIALIZED VIEW ");
		else 
			command.append("DROP VIEW ");

		command.append(name);

		if (cascade)
			command.append(" CASCADE");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}

	public String rename(long item, ITEM_TYPE type, String newName) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.VIEW);

		StringBuffer command = new StringBuffer();
		
		if (type == ITEM_TYPE.MATERIALIZED_VIEW)
			command.append("ALTER MATERIALIZED VIEW ");
		else 
			command.append("ALTER VIEW ");
		
		command.append(name	+ " RENAME TO " +  newName);

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}

	public String createView(String schema, String viewName, String definition, String comment, boolean isMaterialized) {
		String command;
		
		if (isMaterialized)
			command = "CREATE MATERIALIZED VIEW ";
		else 
			command = "CREATE VIEW ";

		command = command + schema + "." + viewName + " AS " + definition;
		
		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}
	
	public String refreshMaterializedView(String schema, String viewName) {
		String command = "REFRESH MATERIALIZED VIEW " + schema + "." + viewName;

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}

}
