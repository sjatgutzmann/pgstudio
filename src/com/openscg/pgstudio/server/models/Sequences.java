/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.util.QueryExecutor;
import com.openscg.pgstudio.shared.DatabaseConnectionException;

public class Sequences {

	private final Connection conn;

	private final static String SEQ_LIST = "SELECT c.relname, c.oid "
			+ "  FROM pg_class c " + " WHERE c.relnamespace = ? "
			+ "   AND c.relkind = 'S' " + " ORDER BY relname ";

	public Sequences(Connection conn) {
		this.conn = conn;
	}

	public String getList(int schema) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(SEQ_LIST);
			stmt.setInt(1, schema);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));
				jsonMessage.put("name", rs.getString("relname"));

				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String create(int schema, String sequenceName,
			boolean temporary, String increment, String minValue, String maxValue,
			String start, int cache, boolean cycle)
			throws DatabaseConnectionException, SQLException {

		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);

		StringBuffer query;

		if (temporary)
			query = new StringBuffer("CREATE TEMPORARY SEQUENCE " + schemaName
					+ "." + sequenceName);
		else
			query = new StringBuffer("CREATE SEQUENCE " + schemaName + "."
					+ sequenceName);

		
		if(null != increment && !increment.isEmpty()){
			BigInteger incrInt = new BigInteger(increment);
			if (incrInt.intValue() != 0 && incrInt.intValue() != 1) {
				query = query.append(" INCREMENT BY " + increment);
			}
		}
		
		if(null != minValue && !minValue.isEmpty()){
			BigInteger minInt = new BigInteger(minValue);
			if (minInt.intValue() != 0 && minInt.intValue() != 1) {
				query = query.append(" INCREMENT BY " + minValue);
			}
		}
		
		if(null != maxValue && !maxValue.isEmpty()){
			BigInteger maxInt = new BigInteger(maxValue);
			if (maxInt.intValue() != 0 && maxInt.intValue() != 1) {
				query = query.append(" INCREMENT BY " + maxValue);
			}
		}
		
		if(null != start && !start.isEmpty()){
			BigInteger startInt = new BigInteger(start);
			if (startInt.intValue() != 0 && startInt.intValue() != 1) {
				query = query.append(" INCREMENT BY " + start);
			}
		}
		
		if (cache != 0 && cache != 1) {
			query = query.append(" CACHE " + cache);
		}

		if (cycle)
			query = query.append(" CYCLE");

		System.out.println("Create Sequence Query :: "+query.toString());
		
		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(query.toString());
	}

	public String drop(long item, boolean cascade) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.SEQUENCE);

		StringBuffer command = new StringBuffer("DROP SEQUENCE " + name);

		if (cascade)
			command.append(" CASCADE");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}
	
	public String alter(int schema, String sequenceName,
			String increment, String minValue, String maxValue,
			String start, int cache, boolean cycle)
			throws DatabaseConnectionException, SQLException {

		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);

		StringBuffer query;

		query = new StringBuffer("ALTER SEQUENCE " + schemaName + "."
					+ sequenceName);

		if(null != increment && !increment.isEmpty())
			query = query.append(" INCREMENT BY " + increment);

		if(null != minValue && !minValue.isEmpty())
			query = query.append(" MINVALUE " + minValue);

		if(null != maxValue && !maxValue.isEmpty())
			query = query.append(" MAXVALUE " + maxValue);

		if(null != start && !start.isEmpty())
			query = query.append(" START WITH " + start);

		query = query.append(" CACHE " + cache);

		if (cycle)
			query = query.append(" CYCLE");
		else
			query = query.append(" NO CYCLE");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(query.toString());
	}
	
	public String restart(int schema, String sequenceName)
			throws DatabaseConnectionException, SQLException {
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		String seqName = schemaName+"."+sequenceName;
		StringBuffer query;
		query = new StringBuffer("select setval('"+seqName+"', (select start_value from "+ seqName +"), false)");
		//query = new StringBuffer("ALTER SEQUENCE " + schemaName + "." + sequenceName + " RESTART WITH 1");
		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeSelectQuery(query.toString(),null);
	
	}

	public String incrementValue(int schema, String sequenceName) 
			throws DatabaseConnectionException, SQLException {
		
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		String seqName = schemaName+"."+sequenceName;
		StringBuffer query;
		
		query = new StringBuffer("select nextval('"+seqName+"')");
		QueryExecutor qe = new QueryExecutor(conn);
		System.out.println("increment sequence value query :: "+query);
		return qe.executeSelectQuery(query.toString(),null);
	}
	
	public String changeValue(int schema, String sequenceName, String value) throws SQLException {
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		String seqName = schemaName+"."+sequenceName;
		StringBuffer query = new StringBuffer("SELECT setval(");
		query.append("'").append(seqName).append("',").append(value).append(")");
		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeSelectQuery(query.toString(), null);
	}
	
	public String reset(int schema, String sequenceName) throws DatabaseConnectionException, SQLException {
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		String seqName = schemaName+"."+sequenceName;
		StringBuffer query = new StringBuffer("select setval('"+seqName+"', (select min_value from "+ seqName +"), true)");
		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeSelectQuery(query.toString(), null);
	}
}
