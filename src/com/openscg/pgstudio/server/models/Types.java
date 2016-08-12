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
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.PgStudio.TYPE_FORM;
import com.openscg.pgstudio.server.util.QueryExecutor;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.dto.AlterDomainRequest;
import com.openscg.pgstudio.shared.dto.DomainDetails;

public class Types {

	private final Connection conn;

	private final static String TYPE_LIST = "SELECT t.typname, t.typtype, c.oid "
			+ "  FROM pg_type t, pg_class c "
			+ " WHERE t.typnamespace = ? " + "   AND t.typrelid = c.oid "
			+ "   AND t.typtype = 'c' " + "   AND c.relkind = 'c' "
			+ " UNION "
			+ "SELECT d.typname, d.typtype, d.oid "
			+ "  FROM pg_type d "
			+ " WHERE d.typnamespace = ? "
			+ "   AND d.typtype in ('d', 'e', 'r') " 
			+ " ORDER BY typname  ";

	private final static String DOMAIN_DEFINITION =
			"SELECT pg_catalog.format_type(t.typbasetype, t.typtypmod) as base_type, " +
			"pg_catalog.array_to_string(ARRAY( SELECT r.conname FROM pg_catalog.pg_constraint r WHERE t.oid = r.contypid ), ',') as constraint_list," +
			"pg_catalog.array_to_string(ARRAY(" +
			"         SELECT pg_catalog.pg_get_constraintdef(r.oid, true) FROM pg_catalog.pg_constraint r WHERE t.oid = r.contypid" +
			"       ), ',') as check_list ," +
			"       typdefault, typnotnull " +
			"  FROM pg_type t " +
			" WHERE t.typtype = 'd' " +
			"   AND t.oid = ? ";
	
	public Types(Connection conn) {
		this.conn = conn;
	}

	public String getList(int schema) {
		JSONArray result = new JSONArray();

		try {
			PreparedStatement stmt = conn.prepareStatement(TYPE_LIST);
			stmt.setInt(1, schema);
			stmt.setInt(2, schema);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMessage = new JSONObject();
				jsonMessage.put("id", Long.toString(rs.getLong("oid")));

				jsonMessage.put("name", rs.getString("typname"));
				jsonMessage.put("type_kind", rs.getString("typtype"));

				result.add(jsonMessage);
			}

		} catch (SQLException e) {
			return "";
		}

		return result.toString();
	}

	public String dropType(long item, boolean cascade) throws SQLException {
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TYPE);
		
		StringBuffer command = new StringBuffer("DROP TYPE " + name);
		
		if (cascade)
			command.append(" CASCADE");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(command.toString());
	}

	public String createType(String connectionToken, String schema,
			String typeName, TYPE_FORM form, String baseType,
			String definition, ArrayList<String> attributeList) {

		switch (form) {
		case COMPOSITE:
			return createComposite(connectionToken, schema, typeName,
					attributeList);
		case ENUM:
			return createEnum(connectionToken, schema, typeName, attributeList);
		case DOMAIN:
			return createDomain(connectionToken, schema, typeName, baseType,
					definition);
		case RANGE:
			return createRange(connectionToken, schema, typeName, baseType,
					definition);
		default:
			break;
		}

		return "Command Not Executed";
	}

	private String createComposite(String connectionToken, String schema,
			String typeName, ArrayList<String> attributeList) {

		StringBuffer prefix;
		StringBuffer attr;
		String query;

		prefix = new StringBuffer("CREATE TYPE " + schema + "." + typeName
				+ " AS ");

		attr = new StringBuffer("");
		if (attributeList.size() > 0) {
			/*
			 * The ArrayList paramList contains the details of each parameter as
			 * it would appear in the final CREATE FUNCTION query
			 */
			for (int i = 0; i < attributeList.size(); i++) {
				if (i != 0)
					attr.append(" , ");
				attr.append(attributeList.get(i));
			}
		}
		attr = new StringBuffer(" ( " + attr + " ) ");

		query = (prefix.toString() + attr.toString());

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(query);
	}

	private String createDomain(String connectionToken, String schema,
			String typeName, String baseType, String definition) {

		StringBuffer prefix;
		String query;

		prefix = new StringBuffer("CREATE DOMAIN " + schema + "." + typeName
				+ " AS ");
		prefix.append(baseType);

		query = (prefix.toString() + " " + definition);

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(query);
	}

	private String createEnum(String connectionToken, String schema,
			String typeName, ArrayList<String> attributeList) {

		StringBuffer prefix;
		StringBuffer attr;
		String query;

		prefix = new StringBuffer("CREATE TYPE " + schema + "." + typeName
				+ " AS ENUM ");

		attr = new StringBuffer("");
		if (attributeList.size() > 0) {
			/*
			 * The ArrayList paramList contains the details of each parameter as
			 * it would appear in the final CREATE FUNCTION query
			 */
			for (int i = 0; i < attributeList.size(); i++) {
				if (i != 0)
					attr.append(" , ");
				attr.append("$$" + attributeList.get(i) + "$$");
			}
		}
		attr = new StringBuffer(" ( " + attr + " ) ");

		query = (prefix.toString() + attr.toString());

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(query);
	}

	private String createRange(String connectionToken, String schema,
			String typeName, String baseType, String definition) {

		StringBuffer query;

		query = new StringBuffer("CREATE TYPE " + schema + "." + typeName
				+ " AS RANGE (");
		query.append(" SUBTYPE = " + baseType);

		if (!definition.trim().equals(""))
			query.append(" , " + definition);

		query.append(")");

		QueryExecutor qe = new QueryExecutor(conn);
		return qe.executeUtilityCommand(query.toString());
	}
	
	public String addCheck(int schema, String domainName, String checkName, String expression) 
			throws DatabaseConnectionException, SQLException{
		
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		String domainFullName = schemaName+"."+domainName;
		StringBuffer query;
		
		query = new StringBuffer("ALTER DOMAIN "+domainFullName+" ADD ");
		if(null != checkName && !checkName.isEmpty())
			query.append("CONSTRAINT \""+ checkName+"\" "); 
		query.append("CHECK ("+expression+" )");
		QueryExecutor qe = new QueryExecutor(conn);
		
		return qe.executeUtilityCommand(query.toString());
	}
	
	public String dropCheck(int schema, String domainName, String checkName) 
			throws DatabaseConnectionException, SQLException{
		System.out.println("Entering using : " + checkName);
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		String domainFullName = schemaName+".\""+domainName + "\"";
		StringBuffer query;
		
		query = new StringBuffer("ALTER DOMAIN "+domainFullName+" DROP CONSTRAINT \""+ checkName + "\"");
		System.out.println(query);
		QueryExecutor qe = new QueryExecutor(conn);
		
		return qe.executeUtilityCommand(query.toString());
	}

	public String alter(String connectionToken, int schema, AlterDomainRequest request) throws SQLException {
		StringBuffer query = null;
		Schemas s = new Schemas(conn);
		String schemaName = s.getName(schema);
		
		if(request.getNotNull() != null) {
			query = constructBaseQuery(schemaName, request.getName());
			if(!request.getNotNull()) {
				query.append(" DROP NOT NULL");
			} else {
				query.append(" SET NOT NULL");
			}
		}
		
		QueryExecutor qe = new QueryExecutor(conn);
		System.out.println("QUERY CONSTRUCTED : " + query);
		String result = qe.executeUtilityCommand(query.toString());
		
		if(result.contains("ERROR")) {
			return result;
		}
		
		query = constructBaseQuery(schemaName, request.getName());
		if(request.getDefaultValue() != null && !request.getDefaultValue().isEmpty()) {						
			query.append(" SET DEFAULT ").append(request.getDefaultValue());
		} else {
			query.append(" DROP DEFAULT");
		}			
		
		qe = new QueryExecutor(conn);
		System.out.println("QUERY CONSTRUCTED : " + query);
		result = qe.executeUtilityCommand(query.toString());
		
		return result;
	}
	
	private StringBuffer constructBaseQuery(String schema, String name) {
		return new StringBuffer("ALTER DOMAIN ").append(schema).append(".").append(name);
	}
	
	public DomainDetails getDomainDetails(long item) throws SQLException {
		DomainDetails details = null;
		Database db = new Database(conn);
		String name = db.getItemFullName(item, ITEM_TYPE.TYPE);
		System.out.println("Request received for : " + name);
			PreparedStatement stmt = conn.prepareStatement(DOMAIN_DEFINITION);
			stmt.setLong(1, item);
			
			ResultSet rs = stmt.executeQuery();
			details = new DomainDetails();
			Map<String, String> constraintMap = new HashMap<String, String>();
			if (rs.next()) {
				details.setName(name);
				details.setDefaultValue(rs.getString("typdefault"));
				details.setNotNull(rs.getBoolean("typnotnull"));
				
				String constraintNames[] = rs.getString("constraint_list").split(",");
				String checkConstraints[] = rs.getString("check_list").split(",");
				
				for(int i = 0; i < constraintNames.length; i++) {
					if(constraintNames[i] != null && !constraintNames[i].isEmpty())
					constraintMap.put(constraintNames[i], checkConstraints[i]);
				}
				details.setConstraintMap(constraintMap);
			}
		
		return details;
	}
}
