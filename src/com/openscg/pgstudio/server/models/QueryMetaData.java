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

public class QueryMetaData {
	
	private final Connection conn;

	public QueryMetaData(Connection conn) {
		this.conn = conn;
	}
	
	public String getMetaData(String query) {
		JSONArray ret = new JSONArray();		
		JSONObject result = new JSONObject();		
			
		String queryType = getQueryType(query);
		
		result.put("query_type", queryType);

		if (queryType.equals("SELECT")) {
			
			String sql = query;
			
			if(!query.toLowerCase().contains("limit")){
				sql = sql + " LIMIT 5";
			}

			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();

				JSONArray metadata = new JSONArray();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					JSONObject jsonMessage = new JSONObject();

					jsonMessage.put("name", rs.getMetaData().getColumnName(i));
					jsonMessage.put("data_type", rs.getMetaData().getColumnType(i));

					metadata.add(jsonMessage);
				}			
				result.put("metadata", metadata);				
			} catch (SQLException e) {
				JSONArray error = new JSONArray();
				JSONObject errorMessage = new JSONObject();

				errorMessage.put("error", e.getMessage());
				errorMessage.put("error_code", e.getErrorCode());

				error.add(errorMessage);

				result.put("error", error);
			} catch (Exception e) {
				JSONArray error = new JSONArray();
				JSONObject errorMessage = new JSONObject();

				errorMessage.put("error", e.getMessage());
				errorMessage.put("error_code", -1);

				error.add(errorMessage);

				result.put("error", error);
			}
		}

		ret.add(result);
		return ret.toString();

	}

	private String getQueryType(String query) {
		String q = query.trim().toUpperCase();
		
		if (q.startsWith("SELECT")) {
			return "SELECT";
		}
		
		/*if (q.startsWith("INSERT")) {
			return "INSERT";
		}
		
		if (q.startsWith("UPDATE")) {
			return "UPDATE";
		}

		if (q.startsWith("DELETE")) {
			return "DELETE";
		}*/

		return "COMMAND";
	}
	
}
