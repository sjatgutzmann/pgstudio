/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.util.DBVersionCheck;
import com.openscg.pgstudio.server.util.DBVersionCheck.PG_FLAVORS;
import com.openscg.pgstudio.server.util.QueryExecutor;
import com.openscg.pgstudio.shared.dto.AlterColumnRequest;

public class Columns {

	private final Connection conn;

	private final PG_FLAVORS pgFlavor;

	private final static String COLUMN_LIST = "SELECT a.attname, pg_catalog.format_type(a.atttypid, a.atttypmod) as datatype, "
			+ "       a.attnotnull, a.attnum, "
			+ "       (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128) as default_value"
			+ "          FROM pg_catalog.pg_attrdef d"
			+ "         WHERE d.adrelid = a.attrelid "
			+ "           AND d.adnum = a.attnum AND a.atthasdef) as devault_value, "
			+ "       EXISTS (SELECT 1 "
			+ "                 FROM (SELECT unnest(conkey) attnum "
			+ "                         FROM pg_constraint "
			+ "                        WHERE conrelid = a.attrelid AND contype = 'p') c"
			+ "                WHERE c.attnum = a.attnum) as is_pk, "
			+ "      false as is_dk,"
			+ "      d.description "
			+ "  FROM pg_attribute a "
			+ "       LEFT OUTER JOIN pg_description d"
			+ "         ON (a.attrelid = d.objoid AND d.objsubid = a.attnum) "
			+ " WHERE a.attrelid = ? "
			+ "   AND a.attnum > 0 "
			+ "   AND NOT a.attisdropped " + " ORDER BY a.attnum ";

	private final static String COLUMN_LIST_XC = "SELECT a.attname, pg_catalog.format_type(a.atttypid, a.atttypmod) as datatype, "
			+ "       a.attnotnull, a.attnum, "
			+ "       (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128) as default_value"
			+ "          FROM pg_catalog.pg_attrdef d"
			+ "         WHERE d.adrelid = a.attrelid "
			+ "           AND d.adnum = a.attnum AND a.atthasdef) as devault_value, "
			+ "       EXISTS (SELECT 1 "
			+ "                 FROM (SELECT unnest(conkey) attnum "
			+ "                         FROM pg_constraint "
			+ "                        WHERE conrelid = a.attrelid AND contype = 'p') c"
			+ "                WHERE c.attnum = a.attnum) as is_pk, "
			+ "       EXISTS (SELECT 1"
			+ "                 FROM pgxc_class xc"
			+ "                WHERE xc.pcrelid = a.attrelid"
			+ "                  AND xc.pcattnum = a.attnum) as is_dk, "
			+ "      d.description "
			+ "  FROM pg_attribute a "
			+ "       LEFT OUTER JOIN pg_description d"
			+ "         ON (a.attrelid = d.objoid AND d.objsubid = a.attnum) "
			+ " WHERE a.attrelid = ?  "
			+ "   AND a.attnum > 0 "
			+ "   AND NOT a.attisdropped " + " ORDER BY a.attnum ";
	
	/*private final static String COLUMN_DATATYPE_LIST =" select column_name, data_type , udt_name from information_schema.columns "
	 +" where table_name = ? ";*/
	
	private final static String COLUMN_DATATYPE_LIST = "SELECT a.attname, pg_catalog.format_type(a.atttypid, a.atttypmod) as datatype, "
		+ "       a.attnotnull, a.attnum, "
		+ "       (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128) as default_value"
		+ "          FROM pg_catalog.pg_attrdef d"
		+ "         WHERE d.adrelid = a.attrelid "
		+ "           AND d.adnum = a.attnum AND a.atthasdef) as devault_value, "
		+ "       EXISTS (SELECT 1 "
		+ "                 FROM (SELECT unnest(conkey) attnum "
		+ "                         FROM pg_constraint "
		+ "                        WHERE conrelid = a.attrelid AND contype = 'p') c"
		+ "                WHERE c.attnum = a.attnum) as is_pk, "
		+ "      false as is_dk,"
		+ "      d.description "
		+ "  FROM pg_attribute a "
		+ "       LEFT OUTER JOIN pg_description d"
		+ "         ON (a.attrelid = d.objoid AND d.objsubid = a.attnum) "
		+ " WHERE a.attrelid = ? "
		+ "   AND a.attnum > 0 "
		+ "   AND NOT a.attisdropped " + " ORDER BY a.attnum ";

	private static final String DEFAULT_RETURN_MESSAGE = "SUCCESS";
	private static final String RENAME_COLUMN_COMMAND = " RENAME COLUMN ";
	private static final String ALTER_TABLE_COMMAND = "ALTER TABLE ";
	private static final String ALTER_COLUMN_COMMAND = " ALTER COLUMN ";
	private static final String SET_DEFAULT = " SET DEFAULT ";
	private static final String NOT_NULL = " NOT NULL ";
	
	public Columns(Connection conn) {
		this.conn = conn;
		DBVersionCheck ver = new DBVersionCheck(conn);
		this.pgFlavor = ver.getPgFlavor();
	}

	public String getList(int item) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = null;
			if (this.pgFlavor == PG_FLAVORS.OTHER_XC
					|| this.pgFlavor == PG_FLAVORS.STORMDB) {
				stmt = conn.prepareStatement(COLUMN_LIST_XC);
			}

			if (this.pgFlavor == PG_FLAVORS.POSTGRESQL) {
				stmt = conn.prepareStatement(COLUMN_LIST);
			}

			stmt.setInt(1, item);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("name", rs.getString("attname"));
				jsonMessage.put("data_type", rs.getString("datatype"));
				jsonMessage.put("id", Integer.toString(rs.getInt("attnum")));

				if (rs.getBoolean("attnotnull")) {
					jsonMessage.put("nullable", "true");
				} else {
					jsonMessage.put("nullable", "false");
				}

				jsonMessage.put("default_value", rs.getString("devault_value"));

				jsonMessage.put("primary_key",
						Boolean.toString(rs.getBoolean("is_pk")));
				jsonMessage.put("distribution_key",
						Boolean.toString(rs.getBoolean("is_dk")));
				jsonMessage.put("comment", rs.getString("description"));

				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String drop(int item, String objectName) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		String command = "ALTER TABLE " + name + " DROP COLUMN " + objectName
				+ " RESTRICT";

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}

	public String rename(int item, String oldName, String newName)
			throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		String command = "ALTER TABLE " + name + " RENAME COLUMN " + oldName
				+ " TO " + newName;

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}

	public String create(int item, String columnName, String datatype,
			String comment, boolean not_null, String defaultval)
			throws SQLException {

		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		StringBuffer command = new StringBuffer("ALTER TABLE " + name
				+ " ADD COLUMN " + columnName + " " + datatype);

		if (not_null)
			command.append(" NOT NULL");
		if (!defaultval.equalsIgnoreCase(""))
			command.append(" DEFAULT " + defaultval);

		QueryExecutor qe = new QueryExecutor(conn);
		String result = qe.executeUtilityCommand(command.toString());

		if (comment != "") {
			String commentCommand = "COMMENT ON COLUMN " + name + "."
					+ columnName + " IS " + "$$" + comment + "$$";

			String r2 = qe.executeUtilityCommand(commentCommand);
		}

		return result;
	}

	public String alter(long item, AlterColumnRequest command) throws SQLException {
		String result = null;
		result = executeAlterStatement(item, command);			
		return result;
	}

	private String executeAlterStatement(long item, AlterColumnRequest command) throws SQLException {
		String result = null;
		
		Database db = new Database(conn);
		String tableName = db.getItemFullName(item, ITEM_TYPE.TABLE);
		QueryExecutor qe = new QueryExecutor(conn);
		
		String columnName = command.getColumnName();
		
		StringBuffer alterStatement = null;
		if (!StringUtils.isEmpty(command.getNewColumnName())) {
			alterStatement = initAlterStatement(tableName);
			alterStatement.append(RENAME_COLUMN_COMMAND).append(columnName).append(" TO ")
					.append(command.getNewColumnName());
			result = qe.executeUtilityCommand(alterStatement.toString(), DEFAULT_RETURN_MESSAGE);
			// Changing old column to new as subsequent alter statements will need the new column name
			columnName = command.getNewColumnName();
		}
		
		if (!StringUtils.isEmpty(command.getDataType()) || !StringUtils.isEmpty(command.getLength())) {
			alterStatement = initAlterStatement(tableName);
			
			alterStatement.append(ALTER_COLUMN_COMMAND).append(columnName).append(" TYPE ").append(command.getDataType());
			if(command.getLength() != null && !command.getLength().isEmpty()) 
				alterStatement.append("(").append(command.getLength()).append(")");
			result = qe.executeUtilityCommand(alterStatement.toString(), DEFAULT_RETURN_MESSAGE);
		}
		
		if (!StringUtils.isEmpty(command.getDefaultValue())) {
			alterStatement = initAlterStatement(tableName);
			alterStatement.append(ALTER_COLUMN_COMMAND).append(columnName).append(SET_DEFAULT)
					.append(command.getDefaultValue());
			result = qe.executeUtilityCommand(alterStatement.toString(), DEFAULT_RETURN_MESSAGE);
		}
		
		if (command.getNullable() != null) {
			alterStatement = initAlterStatement(tableName);
			String notNullConstraint = " SET ";
			if (command.getNullable()) {
				notNullConstraint = " DROP ";
			}
			alterStatement.append(ALTER_COLUMN_COMMAND).append(columnName).append(notNullConstraint).append(NOT_NULL);
			result = qe.executeUtilityCommand(alterStatement.toString(), DEFAULT_RETURN_MESSAGE);
		}
	
		if (!StringUtils.isEmpty(command.getComments())) {
			String commentCommand = "COMMENT ON COLUMN " + tableName + "."
					+ command.getColumnName() + " IS " + "$$" + command.getComments() + "$$";
			result = qe.executeUtilityCommand(commentCommand, DEFAULT_RETURN_MESSAGE);
		}	
		return result;
	}

	private StringBuffer initAlterStatement(String tableName) {
		StringBuffer alterStatement = new StringBuffer(ALTER_TABLE_COMMAND).append(tableName);
		return alterStatement;
	}
	
	public Map<String,String> getColumnDataTypeMap(long item) {
		Map<String,String> result = new HashMap<String,String>();

		try {
			PreparedStatement stmt = null;
			if (this.pgFlavor == PG_FLAVORS.OTHER_XC
					|| this.pgFlavor == PG_FLAVORS.STORMDB) {
				stmt = conn.prepareStatement(COLUMN_DATATYPE_LIST);
			}

			if (this.pgFlavor == PG_FLAVORS.POSTGRESQL) {
				stmt = conn.prepareStatement(COLUMN_LIST);
			}

			stmt.setLong(1, item);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
			   /*Replace extra () and numbers for checking data type ex varchar[0] to make it generic varchar[]*/ 
			    
			   result.put(rs.getString("attname"),rs.getString("datatype").replaceAll("[^a-zA-Z\\[\\]\" \"]", ""));			
			}

		} catch (SQLException e) {
			e.printStackTrace();
			
		}

		return result;
	}
}
