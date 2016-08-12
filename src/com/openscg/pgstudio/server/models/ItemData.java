/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.util.QueryExecutor;
import com.openscg.pgstudio.server.util.QuotingLogic;

public class ItemData {
	
	private final Connection conn;

	public ItemData(Connection conn) {
		this.conn = conn;
	}
	
	public String getData(int item, ITEM_TYPE type, int count) throws SQLException {
		if (type != ITEM_TYPE.TABLE && type != ITEM_TYPE.VIEW && type != ITEM_TYPE.FOREIGN_TABLE) {
			return "";
		}

		Database db = new Database(conn);
		String name = db.getItemFullName(item, type);

		JSONArray ret = new JSONArray();		
		JSONObject result = new JSONObject();		
			
		String sql = "SELECT * FROM " + name + " LIMIT " + count; 
		
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
								
				JSONArray resultset = new JSONArray();
				while (rs.next()) {
					JSONObject jsonMessage = new JSONObject();
	
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						String col = "c" + i;
						String val = "";
						
						if (rs.getObject(i) != null) {
							switch (rs.getMetaData().getColumnType(i)) {
								case -5:   /* BIGINT */
								case 4:    /* INT */
								case 5:    /* SMALLINT */
									val = Long.toString(rs.getLong(i));
									break;
								case -7:   /* BINARY */
								case 16:   /* BOOLEAN */
									if (rs.getBoolean(i)) {
										val = "true";					
									} else {
										val = "false";										
									}
									break;
								case 12 :  /* VARCHAR */
									val = rs.getString(i);
									break;
								case 91 : /* DATE */
									val = rs.getDate(i).toString();
									break;
								case 92 : /* TIME */
									val = rs.getTime(i).toString();
									break;
								case 93 : /* TIMESTAMP */
									val = rs.getTimestamp(i).toString();
									break;
								default :
									val = rs.getString(i);
							}
						}
						
						jsonMessage.put(col, val);	
					}
									
					resultset.add(jsonMessage);
				}
				result.put("resultset", resultset);			
		} catch (SQLException e) {
			return "";
		} catch (Exception e) {
			return "";
		}
		
		ret.add(result);
		
		return ret.toString();

	}

	public String getExplainResult(String query){
		String JsonQuery = "EXPLAIN (format json) " + query; 
				
		String explain = "";

		try {
			PreparedStatement stmt = conn.prepareStatement(JsonQuery);
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
				explain += rs.getString(1);

		} catch (SQLException e) {
			return "";
		}

		return explain;
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
	
	public String createData(int schema,
			String tableName, ArrayList<String>colNames, ArrayList<String> col_values) throws SQLException {

		String result = "";
		Statement statement = null;
		try {
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		
		ArrayList<String> tmpColNamesLst = new ArrayList<String>();
		ArrayList<String> tmpColValuesLst = new ArrayList<String>();
		
		StringBuffer main;
		StringBuffer columns;
		StringBuffer columnValues;
		String query;

		main = new StringBuffer("INSERT INTO \"" + tableName + "\" ");
		
		columns = new StringBuffer();
		columnValues = new StringBuffer();
		if (col_values.size() > 0) {
			
			for (int i = 0; i < col_values.size(); i++) {
				if(null != col_values.get(i) && col_values.get(i).trim().length() > 0){
					tmpColNamesLst.add(colNames.get(i));
					tmpColValuesLst.add(col_values.get(i));
				}
			}
			
			for (int i = 0; i < tmpColNamesLst.size(); i++) {
				if (i != 0)
					columns.append(" , ");
				columns.append(tmpColNamesLst.get(i));
			}
			
			/*
			 * The ArrayList col_list contains the details of each column as it
			 * would appear in the Final INSERT query
			 */
			for (int i = 0; i < tmpColValuesLst.size(); i++) {
				if (i != 0)
					columnValues.append(" , ");
				columnValues.append("'"+tmpColValuesLst.get(i)+"'");
			}
		}
		
		columns = new StringBuffer(" ( " + columns + " ) ");
		columnValues = new StringBuffer(" ( " + columnValues + " ) ");

		query = (main.toString() + columns.toString() + " VALUES "+ columnValues.toString());

		statement = conn.createStatement();
		System.out.println("insert query 1:: "+query);
		int output = statement.executeUpdate(query);
		
		result = output+"";
		
		}finally{
		      //finally block used to close resources
			      try{
			         if(statement!=null)
			            statement.close();
			      }catch(SQLException se){
			      }// do nothing
		}
		return result;
	}
	
	public String dropItemData(int schema,
			String tableName) {

		String result = "";
		Statement statement = null;
		try {
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		
		StringBuffer main;
		String query;

		main = new StringBuffer("DELETE FROM \"" + tableName + "\"");
		
		
		query = main.toString();
		
		statement = conn.createStatement();
		int output = statement.executeUpdate(query);
		
		result = output+"";
		
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
		      //finally block used to close resources
			      try{
			         if(statement!=null)
			        	 statement.close();
			      }catch(SQLException se){
			      }// do nothing
		}
		return result;
	}
	
	
}
