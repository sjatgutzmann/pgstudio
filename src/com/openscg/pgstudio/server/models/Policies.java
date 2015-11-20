/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.INDEX_TYPE;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.util.QueryExecutor;

public class Policies {

	private final Connection conn;

	private final static String TABLE_POLICY = "SELECT oid::int, polname, polcmd, " + "       CASE WHEN polroles = '{0}' "
			+ "            THEN 'PUBLIC' " + "            ELSE array_to_string(ARRAY(SELECT quote_ident(rolname) "
			+ "                                         FROM pg_roles "
			+ "                                        WHERE oid = ANY(polroles)), ', ') "
			+ "       END AS polroles, pg_get_expr(polqual, polrelid) AS polqual, "
			+ "       pg_get_expr(polwithcheck, polrelid) AS polwithcheck " + "  FROM pg_policy " + " WHERE polrelid = ? ";

	public Policies(Connection conn) {
		this.conn = conn;
	}

	public String getList(int item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(TABLE_POLICY);
		stmt.setInt(1, item);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("id", rs.getString("oid"));
			jsonMessage.put("name", rs.getString("polname"));
			jsonMessage.put("command", rs.getString("polcmd"));
			jsonMessage.put("roles", rs.getString("polroles"));
			jsonMessage.put("using", rs.getString("polqual"));
			jsonMessage.put("withcheck", rs.getString("polwithcheck"));

			result.add(jsonMessage);
		}

		return result.toString();
	}

	public String drop(int item, String policyName) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		String command = "DROP POLICY " + policyName + " ON " + name;

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}

	public String rename(int item, String oldName, String newName) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		String command = "ALTER POLICY " + oldName + " ON " + name + " RENAME TO " + newName;

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}

	public String create(int item, String policyName, String cmd, String role, String using, String withCheck) throws SQLException {

		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		StringBuffer command = new StringBuffer("CREATE POLICY " + policyName + " ON " + name + " FOR " + cmd + " TO " + role);

		// Only policies on INSERT commands do not have a USING clause
		if (!cmd.equals("INSERT")) {
			command.append(" USING (");
			command.append(using);
			command.append(")");
		}

		// SELECT and DELETE do not have a WITH CHECK clause
		if (!cmd.equals("SELECT") && !cmd.equals("DELETE")) {
			command.append(" WITH CHECK (");
			command.append(withCheck);
			command.append(")");
		}

		QueryExecutor qe = new QueryExecutor(conn);
		String result = qe.executeUtilityCommand(command.toString());

		return result;
	}

	public String configureRowSecurity(int item, boolean rowSecurity, boolean forceRowSecurity) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		String enable = "ENABLE";
		if (!rowSecurity)
			enable = "DISABLE";

		String force = "FORCE";
		if (!forceRowSecurity)
			force = "NO FORCE";

		String command = "ALTER TABLE " + name + " " + enable + " ROW LEVEL SECURITY, " + force + " ROW LEVEL SECURITY";

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}
}
