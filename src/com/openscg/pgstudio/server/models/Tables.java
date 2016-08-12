/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.util.DBVersionCheck;
import com.openscg.pgstudio.server.util.QueryExecutor;
import com.openscg.pgstudio.server.util.QuotingLogic;
import com.openscg.pgstudio.server.util.DBVersionCheck.PG_FLAVORS;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;

public class Tables {

	private final Connection conn;
	private final PG_FLAVORS pgFlavor;
	private final int version;

	private final static String TABLE_LIST_XC =
			"SELECT c.relname as tablename, xc.pclocatortype as tabletype, d.description, c.oid,  " +
					"       false as relrowsecurity, false as relforcerowsecurity " +
					"  FROM pgxc_class xc, pg_class c " +
					"       LEFT OUTER JOIN pg_description d" +
					"         ON (c.oid = d.objoid AND d.objsubid = 0) " +
					" WHERE c.relnamespace = ? " +
					"   AND c.oid = xc.pcrelid " +
					"   AND c.relkind = 'r' " +
					" ORDER BY tablename";

	private final static String TABLE_LIST_95 =
			"SELECT c.relname as tablename, \'R\' as tabletype, d.description, c.oid, " +
					"       relrowsecurity, relforcerowsecurity " +
					"  FROM pg_class c" +
					"       LEFT OUTER JOIN pg_description d" +
					"         ON (c.oid = d.objoid AND d.objsubid = 0) " +
					" WHERE c.relnamespace = ? " +
					"   AND c.relkind = 'r' " +
					" ORDER BY tablename ";

	private final static String TABLE_LIST =
			"SELECT c.relname as tablename, \'R\' as tabletype, d.description, c.oid, " +
					"       false as relrowsecurity, false as relforcerowsecurity " +
					"  FROM pg_class c" +
					"       LEFT OUTER JOIN pg_description d" +
					"         ON (c.oid = d.objoid AND d.objsubid = 0) " +
					" WHERE c.relnamespace = ? " +
					"   AND c.relkind = 'r' " +
					" ORDER BY tablename ";

	public Tables(Connection conn) {
		this.conn = conn;
		DBVersionCheck ver = new DBVersionCheck(conn);
		this.version = ver.getVersion();
		this.pgFlavor = ver.getPgFlavor();
	}

	public String getList(int schema) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = null;
			if(this.pgFlavor == PG_FLAVORS.OTHER_XC || this.pgFlavor == PG_FLAVORS.STORMDB)	{
				stmt = conn.prepareStatement(TABLE_LIST_XC);
			}

			if(this.pgFlavor == PG_FLAVORS.POSTGRESQL)	{
				if (version >= 90500) {
					stmt = conn.prepareStatement(TABLE_LIST_95);
				} else {
					stmt = conn.prepareStatement(TABLE_LIST);
				}
			}

			stmt.setInt(1, schema);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));
				jsonMessage.put("name", rs.getString("tablename"));
				jsonMessage.put("table_type", rs.getString("tabletype"));
				jsonMessage.put("comment", rs.getString("description"));

				if (rs.getBoolean("relrowsecurity")) {
					jsonMessage.put("row_security", "true");
				} else {
					jsonMessage.put("row_security", "false");
				}

				if (rs.getBoolean("relforcerowsecurity")) {
					jsonMessage.put("force_row_security", "true");
				} else {
					jsonMessage.put("force_row_security", "false");
				}

				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}
		return result.toString();
	}
	
	public String drop(long item, boolean cascade) throws SQLException{
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		StringBuffer command = new StringBuffer("DROP TABLE " + name);

		if (cascade)
			command.append(" CASCADE");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}
	
	public String analyze(long item, ITEM_TYPE type, boolean vacuum,
			boolean vacuumFull, boolean reindex) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		StringBuffer command = new StringBuffer();

		if(reindex) {
			command.append("REINDEX TABLE ");
		} else if (vacuum) {
			if (vacuumFull) {
				command.append("VACUUM FULL ANALYZE VERBOSE ");
			} else {
				command.append("VACUUM ANALYZE VERBOSE ");
			}
		} else {
			command.append("ANALYZE VERBOSE ");
		}
		command.append(name);
		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}
	
	public String rename(long item, ITEM_TYPE type, String newName) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		String command = "ALTER TABLE " + name + " RENAME TO " + newName;

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command);
	}
	
	public String truncate(long item, ITEM_TYPE type) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TABLE);

		String command = "TRUNCATE " + name;

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command, "TRUNCATE TABLE");
	}

	public String create(int schema,
			String tableName, boolean unlogged, boolean temporary, String fill,
			ArrayList<String> col_list, HashMap<Integer, String> commentLog,
			ArrayList<String> col_index) throws SQLException {

		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);

		String result = "";

		StringBuffer main;
		StringBuffer column;
		String query;

		main = new StringBuffer("CREATE TABLE " + schemaName + "." + tableName);
		if (unlogged)
			main = new StringBuffer("CREATE UNLOGGED TABLE " + schemaName + "."
					+ tableName);
		else if (temporary)
			main = new StringBuffer("CREATE TEMPORARY TABLE " + schemaName + "."
					+ tableName);

		column = new StringBuffer("");
		if (col_list.size() > 0) {
			/*
			 * The ArrayList col_list contains the details of each column as it
			 * would appear in the Final CREATE TABLE query
			 */
			for (int i = 0; i < col_list.size(); i++) {
				if (i != 0)
					column.append(" , ");
				column.append(col_list.get(i));

			}
		}
		column = new StringBuffer(" ( " + column + " ) ");

		query = (main.toString() + column.toString());

		QueryExecutor qe = new QueryExecutor(conn);
		result = qe.executeUtilityCommand(query);

		/*
		 * Now iterating through the comment log to execute comment statements
		 * on table and/or columns The HashMap commentLog is prepared such that
		 * the key 0 will always correspond to the comment on table and all the
		 * non zero positive keys will correspond to comments on different
		 * columns
		 */
		Iterator<?> it = commentLog.entrySet().iterator();
		while (it.hasNext()) {
			StringBuffer commentQuery = new StringBuffer("COMMENT ON ");
			Map.Entry<Integer, String> pairs = (Map.Entry) it.next();

			// If key is 0 (comment on table)
			if ((Integer) pairs.getKey() == 0) {
				commentQuery.append("TABLE " + tableName + " IS " + "'"
						+ pairs.getValue().toString() + "'");

				String r2 = qe.executeUtilityCommand(commentQuery.toString());
			}
			// If key is non zero (comment on column(s))
			else {
				QuotingLogic q = new QuotingLogic();
				commentQuery
				.append("COLUMN "
						+ tableName
						+ "."
						+ q.addQuote(col_index.get((Integer) pairs
								.getKey() - 1)) + " IS " + "'"
								+ pairs.getValue().toString() + "'");

				String r2 = qe.executeUtilityCommand(commentQuery.toString());

			}

		}

		return result;
	}

	public String createTableLike(String connectionToken, int schema, String tableName, String source, boolean defaults, boolean constraints, boolean indexes)  throws SQLException {

		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);

		String result;

		StringBuffer main;
		String query = "";
		String tempQuery = "";

		String[] schemaTableName = source.split("\\.");

		main = new StringBuffer("CREATE TABLE " + schemaName + "." + tableName+" (like \""+ schemaTableName[0] + "\".\"" + schemaTableName[1] + "\"");

		if(defaults){
			tempQuery += " INCLUDING DEFAULTS";
		}

		if(constraints){
			tempQuery += " INCLUDING CONSTRAINTS";
		}

		if(indexes){
			tempQuery += " INCLUDING INDEXES";
		}

		query = main.toString().concat(tempQuery).concat(")");
		System.out.println(query);
		QueryExecutor qe = new QueryExecutor(conn);
		result = qe.executeUtilityCommand(query);

		return result;
	}

}
