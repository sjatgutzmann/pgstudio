package com.openscg.pgstudio.server.models.fulltextsearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.models.Database;
import com.openscg.pgstudio.server.models.Schemas;
import com.openscg.pgstudio.server.util.QueryExecutor;

public class Dictionaries {

	private final Connection conn;

	private static final String CREATE_DICTIONARY = "CREATE TEXT SEARCH DICTIONARY ";

	private static final String DROP_DICTIONARY = " DROP TEXT SEARCH DICTIONARY ";

	private static final String CREATE_TEMPLATE = "CREATE TEXT SEARCH TEMPLATE ";// lexize=dispell_lexize,
	// init=dispell_init

	private static final String GET_FTS_DICTIONARIES = " SELECT d.oid as id, " + " n.nspname as schema, d.dictname as name, "
			+ " pg_catalog.obj_description(d.oid, 'pg_ts_dict') as comment, d.dictinitoption as options " + " FROM pg_catalog.pg_ts_dict d "
			+ " LEFT JOIN pg_catalog.pg_namespace n ON n.oid = d.dictnamespace "
			+ " WHERE pg_catalog.pg_ts_dict_is_visible(d.oid) AND d.dictnamespace=?";

	private static final String GET_FTS_DICTIONARY = " SELECT d.oid as id, " + " n.nspname as schema, d.dictname as name, "
			+ " pg_catalog.obj_description(d.oid, 'pg_ts_dict') as comment, d.dictinitoption as options " + " FROM pg_catalog.pg_ts_dict d "
			+ " LEFT JOIN pg_catalog.pg_namespace n ON n.oid = d.dictnamespace "
			+ " WHERE pg_catalog.pg_ts_dict_is_visible(d.oid) AND d.dictnamespace=? AND d.oid=?";

	private static final String GET_TEMPLATES = "SELECT " + " n.nspname as schema, " + " t.tmplname as name, "
			+ " ( SELECT COALESCE(np.nspname, '(null)')::pg_catalog.text || '.' || p.proname "
			+ " 	FROM pg_catalog.pg_proc p "
			+ " 	LEFT JOIN pg_catalog.pg_namespace np ON np.oid = p.pronamespace "
			+ " 	WHERE t.tmplinit = p.oid ) AS  init, "
			+ " ( SELECT COALESCE(np.nspname, '(null)')::pg_catalog.text || '.' || p.proname "
			+ " 	FROM pg_catalog.pg_proc p "
			+ " 	LEFT JOIN pg_catalog.pg_namespace np ON np.oid = p.pronamespace "
			+ " 	WHERE t.tmpllexize = p.oid ) AS  lexize, "
			+ " pg_catalog.obj_description(t.oid, 'pg_ts_template') as comment " + " FROM pg_catalog.pg_ts_template t "
			+ " 	LEFT JOIN pg_catalog.pg_namespace n ON n.oid = t.tmplnamespace "
			+ " WHERE pg_catalog.pg_ts_template_is_visible(t.oid) " + " ORDER BY name ";

	public static final String ALTER_DICTIONARY = "ALTER TEXT SEARCH DICTIONARY ";
	
	public Dictionaries(Connection conn) {
		this.conn = conn;

	}

	@SuppressWarnings("unchecked")
	public String getTemplates() throws Exception {

		JSONArray result = new JSONArray();
		try {

			PreparedStatement ps = conn.prepareStatement(GET_TEMPLATES);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("schema", rs.getString("schema"));
				jsonMessage.put("name", rs.getString("name"));
				jsonMessage.put("lexize", rs.getString("lexize"));
				jsonMessage.put("init", rs.getString("init"));
				
				jsonMessage.put("comment", rs.getString("comment"));
				jsonMessage.put("fullName", rs.getString("schema") + "." + rs.getString("name"));
				
				result.add(jsonMessage);
			}

		} catch (SQLException sqle) {

			throw new Exception(sqle.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Get template failed");

		}

		return result.toString();

	}

	public String create(String schema, String dictName, String template, String option, String comment)
			throws Exception {

		try {

			StringBuilder builder = new StringBuilder(CREATE_DICTIONARY);

			builder.append(schema).append(".").append(dictName);

			builder.append("(");

			builder.append("TEMPLATE=" + template);

			if (option != null && !option.isEmpty()) {
				builder.append(",").append(option);
			}
			
			builder.append(")");

			QueryExecutor qe = new QueryExecutor(conn);
			String result = qe.executeUtilityCommand(builder.toString());
			if(result.contains("ERROR")) {
				return result;
			}
			
			if(comment != null && !comment.isEmpty())
			{
				String commentSql = "COMMENT ON TEXT SEARCH DICTIONARY " + dictName + " IS '" + comment + "'";
				qe = new QueryExecutor(conn);
				result = qe.executeUtilityCommand(commentSql);
			}
			return result;
		} catch (Exception e) {
			throw new Exception("");
		}

	}

	@SuppressWarnings("unchecked")
	public String getList(int schema) {

		JSONArray result = new JSONArray();
		try {
			PreparedStatement ps = conn.prepareStatement(GET_FTS_DICTIONARIES);
			ps.setInt(1, schema);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", rs.getInt("id"));
				jsonMessage.put("schema", rs.getString("schema"));
				jsonMessage.put("name", rs.getString("name"));
				jsonMessage.put("comment", rs.getString("comment"));
				jsonMessage.put("options", rs.getString("options"));
				
				result.add(jsonMessage);
			}
			System.out.println("FETCHED DATA : " + result.toJSONString());
		} catch (SQLException sqle) {
			sqle.printStackTrace();			
		} catch (Exception e) {
			e.printStackTrace();			
		}

		return result.toString();

	}

	@SuppressWarnings("unchecked")
	public String get(int schema, long id) throws SQLException {

		JSONArray result = new JSONArray();
		PreparedStatement ps = conn.prepareStatement(GET_FTS_DICTIONARY);
		ps.setInt(1, schema);
		ps.setLong(2, id);

		ResultSet rs = ps.executeQuery();
		rs.next();

		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("id", rs.getLong("id"));
		jsonMessage.put("schema", rs.getString("schema"));
		jsonMessage.put("name", rs.getString("name"));
		jsonMessage.put("comment", rs.getString("comment"));
		jsonMessage.put("options", rs.getString("options"));
		
		result.add(jsonMessage);

		System.out.println("FETCHED DATA : " + result.toJSONString());

		return result.toString();

	}

	public String drop(long item) throws SQLException {
			Database db = new Database(conn);
			String name = db.getItemFullName(item, ITEM_TYPE.DICTIONARY);
			StringBuilder builder = new StringBuilder(DROP_DICTIONARY);
			
			builder.append(name);

			QueryExecutor qe = new QueryExecutor(conn);
			return qe.executeUtilityCommand(builder.toString());
	}

	public String alter(String name, String newName, String options, String comments) throws SQLException {
		QueryExecutor qe = null;
		String result = null;
		StringBuilder builder = null;
		
		// Alter name
		if(name != null && !name.equalsIgnoreCase(newName))
		{
			builder = new StringBuilder(ALTER_DICTIONARY)
							.append(name)
							.append(" RENAME TO ")
							.append(newName);
			System.out.println("Executing name alter : "  + builder);
			qe = new QueryExecutor(conn);
			result = qe.executeUtilityCommand(builder.toString());
			if(result.contains("ERROR")) 
				return result;
		}
		
		// Alter comments
		if(comments != null )
		{
			String commentSql = "COMMENT ON TEXT SEARCH DICTIONARY " + newName + " IS '" + comments + "'";
			qe = new QueryExecutor(conn);
			System.out.println("Executing comment alter : "  + commentSql);
			result = qe.executeUtilityCommand(commentSql);
			if(result.contains("ERROR")) 
				return result;
		}

		// Alter options
		builder = new StringBuilder(ALTER_DICTIONARY)
				.append(newName).append(" (")				
				.append(options)	
				.append(")");
		System.out.println("Executing options alter : "  + builder);
		qe = new QueryExecutor(conn);
		result = qe.executeUtilityCommand(builder.toString());
		
		return result;
	}

}
