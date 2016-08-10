/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.util.QueryExecutor;
import com.openscg.pgstudio.shared.Constants;
import com.openscg.pgstudio.shared.dto.AlterFunctionRequest;

public class Functions {

	private final Connection conn;

	private final static String FUNC_LIST = 
			"SELECT proname, p.oid,  pg_get_function_identity_arguments(p.oid) as ident" +
					"  FROM pg_proc p " +
					" WHERE p.pronamespace = ? " +
					" ORDER BY proname ";

	private final static String RANGE_DIFF_FUNC =
			"SELECT proname " +
					"  FROM pg_proc " +
					" WHERE prorettype = (SELECT oid FROM pg_type WHERE typname = 'float8') " +
					"   AND array_length(string_to_array(proargtypes::text, ' '), 1) = 2 " +
					"   AND (string_to_array(proargtypes::text, ' '))[1] = (SELECT oid FROM pg_type WHERE typname = ?)::text " +
					"   AND (string_to_array(proargtypes::text, ' '))[2] = (SELECT oid FROM pg_type WHERE typname = ?)::text " +
					" ORDER BY proname ";

	private final static String TRIGGER_FUNC_LIST = 
			"SELECT proname, p.oid " +
					"  FROM pg_proc p " +
					" WHERE p.pronamespace = ? " +
					"   AND prorettype = (SELECT oid FROM pg_type WHERE typname = 'trigger') " +
					" ORDER BY proname ";

	public Functions(Connection conn) {
		this.conn = conn;
	}

	public String getList(int schema) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(FUNC_LIST);
			stmt.setInt(1, schema);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Integer.toString(rs.getInt("oid")));
				jsonMessage.put("name", rs.getString("proname"));
				jsonMessage.put("ident", rs.getString("ident"));				

				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String getTriggerFunctionList(int schema) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(TRIGGER_FUNC_LIST);
			stmt.setInt(1, schema);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Integer.toString(rs.getInt("oid")));
				jsonMessage.put("name", rs.getString("proname"));

				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String getRangeDiffList(String schema, String subType) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(RANGE_DIFF_FUNC);
			stmt.setString(1, subType);
			stmt.setString(2, subType);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("name", rs.getString("proname"));

				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String dropFunction(int item, boolean cascade) throws SQLException{
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.FUNCTION);

		StringBuffer command = new StringBuffer("DROP FUNCTION " + name);

		if (cascade)
			command.append(" CASCADE");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}

	public String create(AlterFunctionRequest request) throws SQLException {
		String query = constructFunctionScript(request);
		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(query);
	}

	public String alter(AlterFunctionRequest request) throws Exception {
		System.out.println("Invoking sql constructor");
		List<String> sqlList = constructAlterSQL(request);
		QueryExecutor qe = new QueryExecutor(conn);
		System.out.println("SQL LIST SIZE : " + sqlList.size());
		String response = null;
		for (String sql : sqlList) {
			System.out.println("Executing : " + sql);
			response = qe.executeUtilityCommand(sql); // If any of the sql fails, then the
			// function stops
			System.out.println("Execution complete ");
		}
		System.out.println("Final response : " + response);
		return response;
	}

	public List<String> constructAlterSQL(AlterFunctionRequest request) {
		System.out.println("Constructing SQLS");
		List<String> sqlList = new ArrayList<String>();
		String sql = null;
		String functionName = request.getFunctionName();
		String newFunctionName = request.getNewFunctionName();
		String functionBody = request.getFunctionBody();
		String newFunctionBody = request.getNewFunctionBody();

		if (!functionName.equals(newFunctionName)) {
			System.out.println("Function name changed");
			System.out.println("Old : " + functionName);
			System.out.println("New : " + newFunctionName);
			sql = constructRenameScript(request);
			// request.setFunctionName(newFunctionName); // Replacing the old
			// value with the new value
			sqlList.add(sql);
		}

		if (!functionBody.equals(newFunctionBody) || "C".equalsIgnoreCase(request.getLanguage())) {
			System.out.println("Function body changed");
			System.out.println("Old\n" + functionBody);
			System.out.println("New\n" + newFunctionBody);
			sql = constructFunctionScript(request);
			sqlList.add(sql);
		}
		return sqlList;
	}

	private String constructRenameScript(AlterFunctionRequest request) {
		StringBuffer sql = new StringBuffer("ALTER FUNCTION ");
		sql = sql.append(request.getSchema()).append(".\"").append(request.getFunctionName()).append("\"");

		String[] paramList = request.getParamsList();
		if (paramList != null && paramList.length > 0) {
			sql.append("(");
			for (int i = 0; i < paramList.length; i++) {
				if (i != 0)
					sql.append(" , ");
				sql.append(paramList[i]);
			}
			sql.append(")");
		}

		sql.append(" RENAME TO ")
		.append("\"").append(request.getNewFunctionName()).append("\"");

		return sql.toString();
	}

	private String constructFunctionScript(AlterFunctionRequest request) {
		String body = null;
		body = constructSQL(request.getSchema(),
				request.getNewFunctionName(), 
				Arrays.asList(request.getParamsList()), 
				request.getNewFunctionBody(), 
				request.getReturns(), 
				request.getLanguage(),
				request.getObjectFilePath());
		return body;
	}

	private String constructSQL(String schemaName, String functionName, List<String> paramList, String definition, String returns, String language, String objFilePath) {
		StringBuffer prefix;
		StringBuffer parameter;
		StringBuffer suffix;
		String query;
		prefix = new StringBuffer(Constants.CREATE_OR_REPLACE_FUNCTION + schemaName + "." + functionName);

		parameter = new StringBuffer("");
		if (paramList.size() > 0) {
			/*
			 * The ArrayList paramList contains the details of each parameter as
			 * it would appear in the final CREATE FUNCTION query
			 */
			for (int i = 0; i < paramList.size(); i++) {
				if (i != 0)
					parameter.append(" , ");
				parameter.append(paramList.get(i));
			}
		}
		parameter = new StringBuffer(" ( " + parameter + " ) ");

		prefix.append(parameter);
		prefix.append(Constants.RETURNS + returns);

		suffix = new StringBuffer("\n");
		if(!"internal".equals(language) && !"C".equalsIgnoreCase(language)) {
			prefix.append(" AS $$\n");
			suffix.append(" $$");
		}
		else {
			definition = "'"+definition+"'";
			prefix.append(" AS \n");			
		}

		if("C".equalsIgnoreCase(language))
			prefix.append("'"+objFilePath+"',");

		suffix.append(" LANGUAGE " + language);

		query = (prefix.toString() + definition + suffix.toString());

		System.out.println("function query :: "+query);
		return query;
	}
	
	public String getFullList(int schema) {
		JSONArray result = new JSONArray();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(FUNC_LIST);
			stmt.setInt(1, schema);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));
				jsonMessage.put("name", rs.getString("proname"));
				jsonMessage.put("ident", rs.getString("ident"));				
				
				result.add(jsonMessage);
			}
			
		} catch (SQLException e) {
			return "";
		}
		
		return result.toString();
	}

}
