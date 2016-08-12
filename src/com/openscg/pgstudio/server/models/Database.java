/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class Database {

	private final Connection conn;

	private final static String DATA_TYPE_LIST = "SELECT tn, CASE WHEN typmodin = 0 "
			+ "                 THEN false "
			+ "                 ELSE true "
			+ "               END AS has_length,"
			+ "       (SELECT count(*) "
			+ "          FROM pg_attribute a, pg_class c, pg_namespace n"
			+ "         WHERE a.atttypid = ts.oid"
			+ "           AND a.attnum > 0"
			+ "           AND a.attrelid = c.oid"
			+ "           AND c.relnamespace = n.oid"
			+ "           AND n.nspname NOT IN ('pg_catalog', 'information_schema')) AS usage_count "
			+ "  FROM (SELECT typname AS tn, typrelid, typelem, typtype, "
			+ "               typcategory, typbasetype, typmodin, oid"
			+ "          FROM pg_type "
			+ "        UNION"
			+ "        SELECT format_type(oid, null) AS tn,typrelid, typelem, "
			+ "               typtype, typcategory, typbasetype, typmodin, oid"
			+ "          FROM pg_type) AS ts "
			+ " WHERE typrelid = 0 "
			+ "   AND typelem = 0 "
			+ "   AND typtype != 'p' "
			+ "   AND typcategory != 'X' "
			+ "   AND typbasetype = 0 "
			+ "UNION SELECT 'serial', false, 0 "
			+ "UNION SELECT 'bigserial', false, 0 " + " ORDER BY 1";

	private final static String LANGUAGE_LIST = "SELECT lanname, oid "
			+ "  FROM pg_language "
			+ " ORDER BY lanname ";

	private final static String LANGUAGE_LIST_NO_C_INTERNAL = "SELECT lanname, oid "
			+ "  FROM pg_language "
			+ " WHERE lanname NOT IN ('internal', 'c') " + " ORDER BY lanname ";

	private final static String ROLE_LIST = "SELECT rolname, oid "
			+ "  FROM pg_roles " + " ORDER BY rolname ";

	private final static String DATABASE_LIST = " SELECT datname, oid "
			+ " FROM pg_database WHERE datistemplate = false ";

	private final static String SERVER_LIST = "SELECT srvname, oid "
			+ "  FROM pg_foreign_server";

	private final static String CLASS_NAME = "SELECT n.nspname, c.relname "
			+ "  FROM pg_class c, pg_namespace n "
			+ " WHERE c.relnamespace = n.oid " + "   AND c.oid = ? ";

	private final static String FUNCTION_NAME = "SELECT n.nspname, p.proname, "
			+ "       pg_get_function_identity_arguments(p.oid) as args "
			+ "  FROM pg_proc p, pg_namespace n "
			+ " WHERE p.pronamespace = n.oid " + "   AND p.oid = ? ";

	private final static String TYPE_NAME = "SELECT n.nspname, t.typname "
			+ "  FROM pg_type t, pg_namespace n "
			+ " WHERE t.typnamespace = n.oid "
			+ "   AND (t.oid = ? OR t.typrelid = ?) ";

	private final String DICTIONARY_NAME = " SELECT n.nspname as schema, d.dictname as name FROM pg_catalog.pg_ts_dict d, pg_namespace n "
			+ " WHERE d.dictnamespace = n.oid AND d.oid=?";

	public Database(Connection conn) {
		this.conn = conn;
	}

	public String getDataTypes() {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(DATA_TYPE_LIST);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", "0");
				jsonMessage.put("name", rs.getString(1));
				jsonMessage.put("has_length",
						Boolean.toString(rs.getBoolean(2)));
				jsonMessage.put("usage_count", Integer.toString(rs.getInt(3)));

				result.add(jsonMessage);
			}
		} catch (SQLException e) {
			return "";
		}
		return result.toString();
	}

	public String getLanguageList() {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(LANGUAGE_LIST_NO_C_INTERNAL);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));
				jsonMessage.put("name", rs.getString("lanname"));

				result.add(jsonMessage);
			}
		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String getLanguageFullList() {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(LANGUAGE_LIST);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));
				jsonMessage.put("name", rs.getString("lanname"));

				result.add(jsonMessage);
			}
		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String getForeignServerList() {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = null;
			stmt = conn.prepareStatement(SERVER_LIST);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));
				jsonMessage.put("name", rs.getString("srvname"));
				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}
		return result.toString();
	}

	public String getRoleList() {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(ROLE_LIST);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));
				jsonMessage.put("name", rs.getString("rolname"));

				result.add(jsonMessage);
			}
		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String getDatabaseList() {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(DATABASE_LIST);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));
				jsonMessage.put("name", rs.getString("datname"));

				result.add(jsonMessage);
			}
		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String getItemFullName(long item, ITEM_TYPE type) throws SQLException {
		switch (type) {
		case FOREIGN_TABLE:
		case MATERIALIZED_VIEW:
		case SEQUENCE:
		case TABLE:
		case VIEW:
			return getFullClassName(item);
		case FUNCTION:
			return getFullFunctionName(item);
		case TYPE:
			return getFullTypeName(item);
		case DICTIONARY:
			return getDictionaryName(item);
		}

		throw new SQLException("Unknown Item Type");
	}

	private String getFullClassName(long oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(CLASS_NAME);
		stmt.setLong(1, oid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String schema = rs.getString("nspname");
			String name = rs.getString("relname");

			if (!StringUtils.isAlphanumeric(schema)
					|| !StringUtils.isAllLowerCase(schema))
				schema = "\"" + schema + "\"";

			if (!StringUtils.isAlphanumeric(name)
					|| !StringUtils.isAllLowerCase(name))
				name = "\"" + name + "\"";

			return schema + "." + name;
		}

		throw new SQLException("Invalid Resultset");
	}

	private String getFullFunctionName(long oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(FUNCTION_NAME);
		stmt.setLong(1, oid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String schema = rs.getString("nspname");
			String name = rs.getString("proname");
			String args = rs.getString("args");

			if (!StringUtils.isAlphanumeric(schema)
					|| !StringUtils.isAllLowerCase(schema))
				schema = "\"" + schema + "\"";

			if (!StringUtils.isAlphanumeric(name)
					|| !StringUtils.isAllLowerCase(name))
				name = "\"" + name + "\"";

			return schema + "." + name + "(" + args + ")";
		}

		throw new SQLException("Invalid Resultset");
	}

	private String getFullTypeName(long oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(TYPE_NAME);
		stmt.setLong(1, oid);
		stmt.setLong(2, oid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String schema = rs.getString("nspname");
			String name = rs.getString("typname");

			if (!StringUtils.isAlphanumeric(schema)
					|| !StringUtils.isAllLowerCase(schema))
				schema = "\"" + schema + "\"";

			if (!StringUtils.isAlphanumeric(name)
					|| !StringUtils.isAllLowerCase(name))
				name = "\"" + name + "\"";

			return schema + "." + name;
		}

		throw new SQLException("Invalid Resultset");
	}

	public String getItemSchema(long item, ITEM_TYPE type) throws SQLException {
		switch (type) {
		case FOREIGN_TABLE:
		case MATERIALIZED_VIEW:
		case SEQUENCE:
		case TABLE:
		case VIEW:
			return getClassSchema(item);
		case FUNCTION:
			return getFunctionSchema(item);
		case TYPE:
			return getTypeSchema(item);
		case DICTIONARY:
			return getDictionarySchema(item);
		}

		throw new SQLException("Unknown Item Type");
	}

	private String getClassSchema(long oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(CLASS_NAME);
		stmt.setLong(1, oid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String schema = rs.getString("nspname");

			if (StringUtils.isAlphanumeric(schema)
					&& StringUtils.isAllLowerCase(schema))
				return schema;

			return "\"" + schema + "\"";
		}

		throw new SQLException("Invalid Resultset");
	}

	private String getFunctionSchema(long oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(FUNCTION_NAME);
		stmt.setLong(1, oid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String schema = rs.getString("nspname");

			if (!StringUtils.isAlphanumeric(schema)
					|| !StringUtils.isAllLowerCase(schema))
				return schema;

			return "\"" + schema + "\"";
		}

		throw new SQLException("Invalid Resultset");
	}

	private String getTypeSchema(long oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(TYPE_NAME);
		stmt.setLong(1, oid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String schema = rs.getString("nspname");

			if (!StringUtils.isAlphanumeric(schema)
					|| !StringUtils.isAllLowerCase(schema))
				return schema;

			return "\"" + schema + "\"";
		}

		throw new SQLException("Invalid Resultset");
	}

	private String getDictionarySchema(long oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(DICTIONARY_NAME);
		stmt.setLong(1, oid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String schema = rs.getString("schema");

			if (!StringUtils.isAlphanumeric(schema)
					|| !StringUtils.isAllLowerCase(schema))
				return schema;

			return "\"" + schema + "\"";
		}

		throw new SQLException("Invalid Resultset");
	}

	private String getDictionaryName(long oid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(DICTIONARY_NAME);
		stmt.setLong(1, oid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String schema = rs.getString("schema");
			String name = rs.getString("name");

			if (!StringUtils.isAlphanumeric(schema)
					|| !StringUtils.isAllLowerCase(schema))
				schema = "\"" + schema + "\"";

			if (!StringUtils.isAlphanumeric(name)
					|| !StringUtils.isAllLowerCase(name))
				name = "\"" + name + "\"";

			return schema + "." + name;
		}

		throw new SQLException("Invalid Resultset");
	}

}
