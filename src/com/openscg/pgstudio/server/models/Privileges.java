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

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.util.QueryExecutor;

public class Privileges {

	private final Connection conn;

	private final static String COMPOSITE_PRIVS = "SELECT grantee, privilege_type, is_grantable, grantor"
			+ "  FROM information_schema.udt_privileges p, pg_class c, pg_namespace n"
			+ " WHERE p.udt_schema = n.nspname "
			+ "   AND p.udt_name = c.relname "
			+ "   AND c.relnamespace = n.oid " + "   AND c.oid = ? ";

	private final static String DOMAIN_PRIVS = "SELECT grantee, privilege_type, is_grantable, grantor"
			+ "  FROM information_schema.usage_privileges p, pg_type t, pg_namespace n"
			+ " WHERE object_schema = n.nspname "
			+ "   AND object_name = t.typname "
			+ "   AND t.typnamespace = n.oid " + "   AND t.oid = ? ";

	private final static String FUNCTION_PRIVS = "SELECT grantee, privilege_type, is_grantable, grantor"
			+ "  FROM information_schema.routine_privileges p, pg_proc c, pg_namespace n "
			+ " WHERE p.routine_schema = n.nspname"
			+ "   AND p.routine_name = c.proname "
			+ "   AND c.pronamespace = n.oid " + "   AND c.oid = ? ";

	private final static String RELATION_PRIVS =
			"SELECT u_grantor.rolname AS grantor, grantee.rolname AS grantee," +
			"       c.prtype AS privilege_type," +
			"       CASE WHEN" +
			"            pg_has_role(grantee.oid, c.relowner, 'USAGE')" +
			"            OR c.grantable" +
			"            THEN 'YES' ELSE 'NO' END AS is_grantable," +
			"       CASE WHEN c.prtype = 'SELECT' THEN 'YES' ELSE 'NO' END AS with_hierarchy" +
			"  FROM (SELECT oid, relname, relnamespace, " +
			"               relkind, relowner, " +
			"               (aclexplode(coalesce(relacl, acldefault('r', relowner)))).* " +
			"          FROM pg_class" +
			"       ) AS c (oid, relname, relnamespace, relkind, relowner, grantor, grantee, prtype, grantable)," +
			"       pg_authid u_grantor," +
			"       (SELECT oid, rolname FROM pg_authid" +
			"         UNION ALL" +
			"        SELECT 0::oid, 'PUBLIC') AS grantee (oid, rolname)" +
			" WHERE c.grantee = grantee.oid" +
			"   AND c.grantor = u_grantor.oid" +
			"   AND c.prtype IN ('INSERT', 'SELECT', 'UPDATE', 'DELETE', 'TRUNCATE', 'REFERENCES', 'TRIGGER')" +
			"   AND (pg_has_role(u_grantor.oid, 'USAGE')" +
			"        OR pg_has_role(grantee.oid, 'USAGE')" +
			"        OR grantee.rolname = 'PUBLIC')" +
			"   AND c.oid = ? ";
	
	private final static String SEQUENCE_PRIVS = "SELECT CAST(grantee.rolname AS varchar) AS grantee, "
			+ "       CAST(c.prtype AS varchar) AS privilege_type, "
			+ "       CAST( "
			+ "       CASE WHEN "
			+ "         pg_has_role(grantee.oid, c.relowner, 'USAGE') "
			+ "         OR c.grantable "
			+ "       THEN 'YES' ELSE 'NO' END AS varchar) AS is_grantable, "
			+ "       CAST(u_grantor.rolname AS varchar) AS grantor "
			+ "  FROM ( "
			+ "        SELECT oid, relname, relnamespace, relkind, relowner, "
			+ "               (aclexplode(coalesce(relacl, acldefault('S', relowner)))).* "
			+ "          FROM pg_class) AS c "
			+ "        (oid, relname, relnamespace, relkind, relowner, "
			+ "         grantor, grantee, prtype, grantable), "
			+ "         pg_namespace nc, pg_authid u_grantor, "
			+ "        (SELECT oid, rolname FROM pg_authid "
			+ "          UNION ALL "
			+ "         SELECT 0::oid, 'PUBLIC') AS grantee (oid, rolname) "
			+ " WHERE c.relnamespace = nc.oid "
			+ "   AND c.relkind = 'S' "
			+ "   AND c.grantee = grantee.oid "
			+ "   AND c.grantor = u_grantor.oid "
			+ "   AND (pg_has_role(u_grantor.oid, 'USAGE') "
			+ "    OR pg_has_role(grantee.oid, 'USAGE') "
			+ "    OR grantee.rolname = 'PUBLIC') " + "   AND c.oid = ? ";

	private final static String TABLE_PRIVS = "SELECT grantee, privilege_type, is_grantable, grantor"
			+ "  FROM information_schema.table_privileges p, pg_class c, pg_namespace n "
			+ " WHERE p.table_schema = n.nspname "
			+ "   AND p.table_name = c.relname "
			+ "   AND c.relnamespace = n.oid " + "   AND c.oid = ? ";

	private final static String VIEW_PRIVS = "SELECT grantee, privilege_type, is_grantable, grantor"
			+ "  FROM information_schema.table_privileges p, pg_class c, pg_namespace n "
			+ " WHERE p.table_schema = n.nspname "
			+ "   AND p.table_name = c.relname "
			+ "   AND c.relnamespace = n.oid "
			+ "   AND c.oid = ? "
			+ "   AND privilege_type in ('SELECT') ";

	private final static String TYPE_KIND = 
			"SELECT relkind " +
			"  FROM pg_class " +
			" WHERE oid = ? " +
			" UNION " +
			"SELECT typtype " +
			"  FROM pg_type " +
			" WHERE oid = ? ";

	public Privileges(Connection conn) {
		this.conn = conn;
	}

	public String getPrivileges(long item, ITEM_TYPE type) throws SQLException {
		if (type == null)
			return "";

		switch (type) {
		case FOREIGN_TABLE:
			return getRelationPrivs(item);
		case FUNCTION:
			return getFunctionPrivs(item);
		case SEQUENCE:
			return getSequencePrivs(item);
		case TABLE:
			return getTablePrivs(item);
		case TYPE:
			return getTypePrivs(item);
		case VIEW:
			return getViewPrivs(item);
		default:
			break;
		}

		return "";
	}

	private String getCompositePrivs(long item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(COMPOSITE_PRIVS);
		stmt.setLong(1, item);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("grantee", rs.getString(1));
			jsonMessage.put("type", rs.getString(2));
			jsonMessage.put("grantable", rs.getString(3));
			jsonMessage.put("grantor", rs.getString(4));

			result.add(jsonMessage);
		}

		return result.toString();
	}

	private String getDomainPrivs(long item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(DOMAIN_PRIVS);
		stmt.setLong(1, item);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("grantee", rs.getString(1));
			jsonMessage.put("type", rs.getString(2));
			jsonMessage.put("grantable", rs.getString(3));
			jsonMessage.put("grantor", rs.getString(4));

			result.add(jsonMessage);
		}

		return result.toString();
	}

	private String getFunctionPrivs(long item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(FUNCTION_PRIVS);
		stmt.setLong(1, item);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("grantee", rs.getString(1));
			jsonMessage.put("type", rs.getString(2));
			jsonMessage.put("grantable", rs.getString(3));
			jsonMessage.put("grantor", rs.getString(4));

			result.add(jsonMessage);
		}

		return result.toString();
	}

	private String getRelationPrivs(long item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(RELATION_PRIVS);
		stmt.setLong(1, item);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("grantee", rs.getString("grantee"));
			jsonMessage.put("type", rs.getString("privilege_type"));
			jsonMessage.put("grantable", rs.getString("is_grantable"));
			jsonMessage.put("grantor", rs.getString("grantor"));

			result.add(jsonMessage);
		}

		return result.toString();
	}

	private String getSequencePrivs(long item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(SEQUENCE_PRIVS);
		stmt.setLong(1, item);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("grantee", rs.getString(1));
			jsonMessage.put("type", rs.getString(2));
			jsonMessage.put("grantable", rs.getString(3));
			jsonMessage.put("grantor", rs.getString(4));

			result.add(jsonMessage);
		}

		return result.toString();
	}

	private String getTablePrivs(long item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(TABLE_PRIVS);
		stmt.setLong(1, item);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("grantee", rs.getString(1));
			jsonMessage.put("type", rs.getString(2));
			jsonMessage.put("grantable", rs.getString(3));
			jsonMessage.put("grantor", rs.getString(4));

			result.add(jsonMessage);
		}

		return result.toString();
	}

	private String getTypePrivs(long item) throws SQLException {
		String result = "";

		PreparedStatement stmt = conn.prepareStatement(TYPE_KIND);
		stmt.setLong(1, item);
		stmt.setLong(2, item);

		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String typeKind = rs.getString(1);

			if (typeKind.equalsIgnoreCase("c")) {
				result = getCompositePrivs(item);
			} else if (typeKind.equalsIgnoreCase("d")) {
				result = getDomainPrivs(item);
				// } else if (typeKind.equalsIgnoreCase("e")) {
				// result = getEnumPrivs(schema, item);
			}

		}

		return result;
	}

	private String getViewPrivs(long item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(VIEW_PRIVS);
		stmt.setLong(1, item);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("grantee", rs.getString(1));
			jsonMessage.put("type", rs.getString(2));
			jsonMessage.put("grantable", rs.getString(3));
			jsonMessage.put("grantor", rs.getString(4));

			result.add(jsonMessage);
		}

		return result.toString();
	}

	public String revoke(long item, ITEM_TYPE type, String privilege,
			String grantee, boolean cascade) throws SQLException {

		Database db = new Database(conn);
		String name = db.getItemFullName(item, type);

		String command = "REVOKE " + privilege;
		command = command + " ON " + type.name() + " " + name;
		command = command + " FROM " + grantee;

		if (cascade)
			command = command + " CASCADE";

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}

	public String grant(long item, ITEM_TYPE type, ArrayList<String> privileges,
			String grantee) throws SQLException {

		Database db = new Database(conn);
		String name = db.getItemFullName(item, type);

		StringBuffer command = new StringBuffer("GRANT ");

		if (privileges.size() > 0) {
			for (int i = 0; i < privileges.size(); i++) {
				if (i != 0)
					command.append(" , ");
				command.append(privileges.get(i));

			}
		}

		String objectType = type.name();

		// Override to TABLE because the syntax is a little weird for GRANT
		if (type == ITEM_TYPE.VIEW || type == ITEM_TYPE.FOREIGN_TABLE)
			objectType = "TABLE";

		command.append(" ON " + objectType + " " + name);
		command.append(" TO " + grantee);

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}

}
