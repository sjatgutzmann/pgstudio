package com.openscg.pgstudio.server.models.fulltextsearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.server.models.Schemas;
import com.openscg.pgstudio.shared.DatabaseConnectionException;

public class Parsers {

    private final Connection conn;

	private final String GET_FTS_PARSERS = "SELECT p.oid as id, " + "n.nspname as schema, " + "p.prsname as name, "
			+ "pg_catalog.obj_description(p.oid, 'pg_ts_parser') as comment " + "FROM pg_catalog.pg_ts_parser p "
			+ "LEFT JOIN pg_catalog.pg_namespace n ON (n.oid = p.prsnamespace) "
			+ "WHERE pg_catalog.pg_ts_parser_is_visible(p.oid) " + "AND n.nspname=?";


    public Parsers(Connection conn) {
	this.conn = conn;

    }
    
    @SuppressWarnings("unchecked")
    public String getList(int schema) throws DatabaseConnectionException {

    	JSONArray result = new JSONArray();
		try {
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);

	    PreparedStatement ps = conn.prepareStatement(GET_FTS_PARSERS);
	    ps.setString(1, schemaName.replaceAll("\"", ""));

	    ResultSet rs = ps.executeQuery();
	    while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("id", rs.getInt("id"));
			jsonMessage.put("schema", rs.getString("schema"));
			jsonMessage.put("name", rs.getString("name"));
			jsonMessage.put("comment", rs.getString("comment"));
	
			result.add(jsonMessage);
	    }
		} catch(SQLException e){
			return "";
		}
	return result.toString();
    }

}
