/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Monitor {

	private final Connection conn;
	
	private final static String ACTIVITY_LIST = 
			"SELECT datname, state, pid, usename, client_addr::varchar, " +
			"       backend_start::timestamp(0), xact_start::timestamp(0)," +
			"       justify_interval((clock_timestamp() - query_start)::interval(0))::varchar as query_time, " +
			"       waiting, query " +
			"  FROM pg_stat_activity " +
			" WHERE datname = current_database() " +
			" ORDER BY 7 DESC ";

	
	public Monitor(Connection conn) {
		this.conn = conn;
	}

	public String getActivity() {
		JSONArray result = new JSONArray();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(ACTIVITY_LIST);
			ResultSet rs = stmt.executeQuery();
			
			
			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();

				jsonMessage.put("datname", rs.getString("datname"));
				jsonMessage.put("state", rs.getString("state"));
				jsonMessage.put("usename", rs.getString("usename"));				
				jsonMessage.put("pid", Integer.toString(rs.getInt("pid")));				
				jsonMessage.put("client_addr", rs.getString("client_addr"));	
				
				jsonMessage.put("backend_start", ((Date)rs.getTimestamp("backend_start")).toString());
				
				if (rs.getTimestamp("xact_start") != null) {
					jsonMessage.put("xact_start", ((Date)rs.getTimestamp("xact_start")).toString()); 
				} else {
					jsonMessage.put("xact_start", "");					
				}
				jsonMessage.put("query_time", rs.getString("query_time"));				
				jsonMessage.put("waiting", Boolean.toString(rs.getBoolean("waiting")));
				jsonMessage.put("query", rs.getString("query"));				

				result.add(jsonMessage);
			}
			
		} catch (SQLException e) {
			return "";
		}
		
		return result.toString();
	}
	

}