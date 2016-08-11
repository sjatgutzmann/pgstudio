/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class QueryExecutor {

	private final Connection conn;

	public QueryExecutor(Connection conn) {
		this.conn = conn;
	}

	public String Execute(String query, String queryType) {
		try {
			if (queryType.equals("SELECT")) {
				// get out because it should not come here
				return "";
			}

			PreparedStatement stmt = conn.prepareStatement(query);
			
			if (queryType.equals("COMMAND")) {
				stmt.execute();
				
				stmt.close();
				// TODO: actually return back better messages like "Table Created"
				return "Command Executed";
			}
	
			int r = stmt.executeUpdate();
			stmt.close();
			
			if (queryType.equals("INSERT")) {
				return "INSERT 0 " + r;
			}
			
			if (queryType.equals("UPDATE")) {
				return "UPDATE " + r; 
			}
			
			if (queryType.equals("DELETE")) {
				return "DELETE " + r; 
			}
			
			return "";

		} catch (SQLException e) {
			return e.getMessage();
		} catch (Exception e) {
			return "Exception";
		}
	}

	public String executeUtilityCommand(String command) {
		return executeUtilityCommand(command, null);
	}
	
	public String executeUtilityCommand(String command, String defaultReturnMessage) {

		String status = "";
		String info = "";
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(command);
			int queryResult = stmt.executeUpdate();
			if (queryResult == 0) {
				status = "SUCCESS";
				if (defaultReturnMessage != null) {
					info = defaultReturnMessage;
				} else {
					info = "Successfully completed.";
				}
			}
			else
				status = "ERROR";

			SQLWarning warning = stmt.getWarnings();
			while (warning != null) {
				info = info + warning.getMessage() + "\n";
				warning = warning.getNextWarning();
				System.out.print("warning*********"+warning);
			}

		} catch (SQLException e) {
			status = "ERROR";
			info = e.getMessage();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO: Log this error
				}			
		}
		JSONArray result = new JSONArray();
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("status", status);
		jsonMessage.put("message", info);
		result.add(jsonMessage);

		return result.toString();
	}
	
	public String executeSelectQuery(String command, String defaultReturnMessage) {
		String status = "";
		String info = "";
		PreparedStatement stmt = null;
		boolean queryResult = false;
		try {
			stmt = conn.prepareStatement(command);
			queryResult = stmt.execute();
			
			if (queryResult) {
				status = "SUCCESS";
				if (defaultReturnMessage != null) {
					info = defaultReturnMessage;
				} else {
					info = "Successfully completed.";
				}
			}
			else
				status = "ERROR";

			SQLWarning warning = stmt.getWarnings();
			while (warning != null) {
				info = info + warning.getMessage() + "\n";
				warning = warning.getNextWarning();
			}

		} catch (SQLException e) {
			status = "ERROR";
			info = e.getMessage();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO: Log this error
				}			
		}

		JSONArray result = new JSONArray();
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("status", status);
		jsonMessage.put("message", info);
		result.add(jsonMessage);

		return result.toString();
	}
	
	public String executeBatchUtilityCommand(List<String> commandList, String defaultReturnMessage) {

		String status = "";
		String info = "";
		Statement stmt = null;
		int[] queryResult = null;
		try {
			stmt = conn.createStatement();
			for(String command: commandList) {
				stmt.addBatch(command);
			}
			System.out.println("executeBatchUtilityCommand.....");
			queryResult = stmt.executeBatch();
			System.out.println("executeBatchUtilityCommand.....queryResult::"+queryResult.length);
			if (queryResult.length != 0) {
				status = "SUCCESS";
				if (defaultReturnMessage != null) {
					info = defaultReturnMessage;
				} else {
					info = "Successfully completed.";
				}
			}
			else
				status = "ERROR";

			SQLWarning warning = stmt.getWarnings();
			
			while (warning != null) {
				info = info + warning.getMessage() + "\n";
				warning = warning.getNextWarning();
				System.out.print("SQLWarning is not null...."+info);
			}
			

		} catch (SQLException e) {
			status = "ERROR";
			info = e.getMessage();
			System.out.println("error:::"+info);
			e.printStackTrace();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO: Log this error
				}			
		}

		JSONArray result = new JSONArray();
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("status", status);
		jsonMessage.put("message", info);
		result.add(jsonMessage);

		return result.toString();
	}


}
