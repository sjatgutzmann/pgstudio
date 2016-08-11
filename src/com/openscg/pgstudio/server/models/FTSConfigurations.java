package com.openscg.pgstudio.server.models;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio;
import com.openscg.pgstudio.server.util.QueryExecutor;
import com.openscg.pgstudio.server.util.SQLConstants;


public class FTSConfigurations {
	private final Connection conn;
	private static final String DEFAULT_RETURN_MESSAGE = "SUCCESS";
	public FTSConfigurations(Connection conn) {
		this.conn = conn;
	}

	public String getFTSConfigurations(String schemaName) throws Exception {
		JSONArray result = new JSONArray();
		if(conn == null) {
			System.out.println("FTSConfigurations.getFTSConfigurations conn is null....");	
		} else {
			System.out.println("FTSConfigurations.getFTSConfigurations conn is not null....");
		}


		String sql = SQLConstants.SELECT_FTS_CONFIG_BY_SCHEMA_NAME.replace(":schema", schemaName);
		System.out.println("FTSConfigurations.getFTSConfigurations SQL::"+sql);
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			processResultSetToJSONArray( rs, result ) ;
			/*if(rs != null && rs.next())
			result = rs.getString("cfg_names");*/
			System.out.println("result is called...."+result);
		} catch (Exception e) {
			System.out.println("exception occurred" + e.getMessage());
			return e.getMessage();
		}
		return result.toString();
	}

	public String getFTSConfigurationDetails(String schemaName, String configName) throws Exception {
		JSONArray result = new JSONArray();
		System.out.println("FTSConfigurations.getFTSConfigurationDetails is called....");
		String sql = SQLConstants.SELECT_FTS_TOKEN_DICT_LIST_BY_TEMPLATE.replace(":cfgTemplate", configName);
		System.out.println("FTSConfigurations.getFTSConfigurationDetails SQL::"+sql);
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			processResultSetToJSONArray( rs, result ) ;
			/*if(rs != null && rs.next())
			result = rs.getString("cfg_names");*/
			System.out.println("result is called...."+result);
		} catch (Exception e) {
			System.out.println("exception occurred" + e.getMessage());
			return e.getMessage();
		}
		return result.toString();
	}


	public String createConfig(String schemaName, String configurationName, String templateName, String parserName, String comments) throws Exception {
		String result ="";
		System.out.println("schemaName:::"+schemaName+ "  configurationName:::"+configurationName+"   templateName::::"+templateName+" parserName:::"+parserName+"  comments::: "+comments);
		try {
			if(templateName != null && !"".equals(templateName)) {
				System.out.println("createConfigWithTemplate");
				result = createConfigWithTemplate(schemaName, configurationName, templateName, comments);
			} else if(parserName != null && !"".equals(parserName)) {
				System.out.println("createConfigWithParser");
				result = createConfigWithParser(schemaName, configurationName, parserName, comments);
			}
			//result = "Success";
		} catch (Exception e) {
			System.out.println("exception occurred" + e.getMessage());
			return e.getMessage();
		}
		return result;
	}

	public String dropConfig(String schemaName, String configurationName, boolean cascade) throws Exception {
		String result ="";
		QueryExecutor qe = new QueryExecutor(conn);
		String sql = SQLConstants.DROP_FTS_CONFIG + schemaName+"."+configurationName;
		if(cascade)
			sql = sql + " CASCADE";
		System.out.println("FTS Configurations dropConfig:::"+sql);
		result = qe.executeUtilityCommand(sql, DEFAULT_RETURN_MESSAGE);
		/*try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			result = "Configuration "+configurationName + " is deleted successfully";
			System.out.println("result is called...."+result);
		} catch (Exception e) {
			System.out.println("exception occurred" + e.getMessage());
			return e.getMessage();
		}*/
		return result;
	}
	
	public String dropMapping(String schemaName, String configurationName, String token) throws Exception {
		String result ="";
		QueryExecutor qe = new QueryExecutor(conn);
		String confName = schemaName + "." +configurationName;
		
		String sql = ("alter text search configuration :configName drop mapping for :tokenType".replace(":configName", confName )).replace(":tokenType", token);
		//String sql = (SQLConstants.DROP_FTS_MAPPING.replace(":configName", confName )).replace(":tokenType", token);
		System.out.println("FTS Mapping dropMapping:::"+sql);
		result = qe.executeUtilityCommand(sql, DEFAULT_RETURN_MESSAGE);
		return result;
	}

	public String alterConfig(String schemaName, String configurationName, String comments) throws Exception {
		QueryExecutor qe = new QueryExecutor(conn);

		String result ="";
		String sql = (SQLConstants.ALTER_FTS_CONFIG_COMMENT.replace(":configName", schemaName+"."+configurationName)).replace(":comment", comments);
		//sql = SQLConstants.ALTER_FTS_CONFIG_COMMENT.replace(":configName", schemaName+"."+configurationName);
		//System.out.println("FTSConfigurations.....sql::::"+sql);
		//sql = sql.replace(":comment", comments);
		System.out.println("FTSConfigurations::::alterConfig:::::sql:::"+sql);
		result = qe.executeUtilityCommand(sql, DEFAULT_RETURN_MESSAGE);
		/*try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			result="success";
		} catch (Exception e) {

			return e.getMessage();
		}*/
		return result;
	}
	
	public String alterMapping(String schemaName, String configurationName, String tokenType, String oldDict, String newDict) throws Exception {
		QueryExecutor qe = new QueryExecutor(conn);

		String result ="";
		String sql = (SQLConstants.ALTER_FTS_MAPPING.replace(":configName", schemaName+"."+configurationName)).replace(":tokenType", tokenType).replace(":oldDict", oldDict).replace(":newDict", newDict);
		System.out.println("FTSConfigurations::::alterMapping:::::sql:::"+sql);
		result = qe.executeUtilityCommand(sql, DEFAULT_RETURN_MESSAGE);
		/*try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			result="success";
		} catch (Exception e) {

			return e.getMessage();
		}*/
		return result;
	}

	public String getParsersList() throws Exception {
		JSONArray result = new JSONArray();
		String parsersStr = "";
		String sql = SQLConstants.SELECT_FTS_CONFIG_PARSERS_LIST;
		System.out.println("getParsersList SQL:::"+sql);
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			processResultSetToJSONArray( rs, result ) ;
		} catch (Exception e) {
			System.out.println("exception occurred" + e.getMessage());
			return e.getMessage();
		}
		return result.toString();
	}
	
	public String getDictionariesList() throws Exception {
		JSONArray result = new JSONArray();
		
		String sql = SQLConstants.SELECT_FTS_DICT_NAMES;
		System.out.println("getDictionariesList SQL:::"+sql);
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			processResultSetToJSONArray( rs, result ) ;
		} catch (Exception e) {
			System.out.println("exception occurred" + e.getMessage());
			return e.getMessage();
		}
		return result.toString();
	}
	
	public String getTokensList() throws Exception {
		JSONArray result = new JSONArray();
		String sql = SQLConstants.SELECT_FTS_TOKEN_DESCRIPTION_LIST;
		System.out.println("getTokensList SQL:::"+sql);
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			processResultSetToJSONArray( rs, result ) ;
		} catch (Exception e) {
			System.out.println("exception occurred" + e.getMessage());
			return e.getMessage();
		}
		return result.toString();
	}


	public String getTemplatesList() throws Exception {
		JSONArray result = new JSONArray();

		String sql = SQLConstants.SELECT_FTS_CONFIG_TEMPLATES_LIST;
		System.out.println("getTemplatesList SQL:::"+sql);
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			processResultSetToJSONArray( rs, result ) ;
			System.out.println("result is called...."+result);
		} catch (Exception e) {
			System.out.println("exception occurred" + e.getMessage());
			return e.getMessage();
		}
		return result.toString();
	}

	private static void processResultSetToJSONArray( ResultSet rsStats,JSONArray result ) throws SQLException{

		if (rsStats != null) {

			while (rsStats.next()) {
				JSONObject jsonMessage = new JSONObject();
				for (int i = 1; i <= rsStats.getMetaData().getColumnCount(); i++) {

					jsonMessage.put(rsStats.getMetaData()
							.getColumnName(i), rsStats.getString(i));

				}
				result.add(jsonMessage);
			}
		}
		//return result;
	}

	/*private static String processResultSetToJSONString( ResultSet rs) throws SQLException{
		JSONArray result = new JSONArray();
		while (rs.next()) {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("name", rs.getString(1));
			result.add(jsonMessage);
		}
		return result.toString();

	}*/

	private String createConfigWithTemplate(String schemaName, String configurationName, String templateName, String comments) {

		List<String> commandsList = new ArrayList<String>();
		String alterSQL = "";
		String configName = schemaName +"."+configurationName;
		String createCfgSql = SQLConstants.ADD_FTS_CONFIG.replace(":configName", configName);
		System.out.println("createCfgSql SQL:::"+createCfgSql);
		String createCfgComment =SQLConstants.ADD_FTS_CONFIG_COMMENT.replace(":configName", configName).replace(":comments", comments);
		System.out.println("createCfgComment SQL:::"+createCfgComment);
		String alterCfgMapping =SQLConstants.ADD_FTS_MAPPING_FOR_CONFIG.replace(":configName", configName);
		System.out.println("alterCfgMapping SQL:::"+alterCfgMapping);
		commandsList.add(createCfgSql);
		commandsList.add(createCfgComment);
		List<String> tokensDictList = getTokensDictsList(templateName);
		for(String tokenKey:tokensDictList) {
			String[] tokenKeyArr= tokenKey.split(":");
			alterSQL = alterCfgMapping.replace(":token", tokenKeyArr[0]).replace(":dict", tokenKeyArr[1]);
			System.out.println("alterSQL SQL:::"+alterSQL);
			commandsList.add(alterSQL);
		}

		QueryExecutor qe = new QueryExecutor(conn);
		String result = qe.executeBatchUtilityCommand(commandsList, DEFAULT_RETURN_MESSAGE);
		return result;
	}
	
	public String createMapping(String schemaName, String configurationName, String tokenType, String dictionary) {
		String configName = schemaName +"."+configurationName;
		String alterCfgMapping =SQLConstants.ADD_FTS_MAPPING_FOR_CONFIG.replace(":configName", configName).replace(":token", tokenType).replace(":dict",dictionary);
		System.out.println("alterCfgMapping SQL:::"+alterCfgMapping);
		QueryExecutor qe = new QueryExecutor(conn);
		String result = qe.executeUtilityCommand(alterCfgMapping, DEFAULT_RETURN_MESSAGE);
		return result;
	}


	private String createConfigWithParser(String schemaName, String configurationName, String parserName, String comments) {
		List<String> commandsList = new ArrayList<String>();
		String configName = schemaName +"."+configurationName;
		String createCfgSql = SQLConstants.ADD_FTS_CONFIG.replace(":configName", configName);
		System.out.println("createCfgSql SQL:::"+createCfgSql);
		String createCfgComment =SQLConstants.ADD_FTS_CONFIG_COMMENT.replace(":configName", configName).replace(":comments", comments);
		System.out.println("createCfgComment SQL:::"+createCfgComment);
		commandsList.add(createCfgSql);
		commandsList.add(createCfgComment);
		QueryExecutor qe = new QueryExecutor(conn);
		String result = qe.executeBatchUtilityCommand(commandsList, DEFAULT_RETURN_MESSAGE);
		return result;
	}

	private List<String> getTokensDictsList(String templateName) {
		List<String> tokenDictList = new ArrayList<String>();
		if(templateName.indexOf(".") > 0) {
			templateName = templateName.substring(templateName.indexOf(".") +1);
		}
		String sql = SQLConstants.SELECT_FTS_TOKEN_DICT_LIST_BY_TEMPLATE.replace(":cfgTemplate", templateName);
		System.out.println("getTokensDictsList::::sql:::"+sql);
		String tokenVal = null;
		String dictVal = null;
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				tokenVal = rs.getString("token");
				dictVal = rs.getString("dictionaries");
				/*if(dictVal.indexOf("_") > 0) {
					dictVal = dictVal.substring(0,dictVal.indexOf("_"));
				}*/
				System.out.println("getTokensDictsList::::tokenVal:::"+tokenVal);
				System.out.println("getTokensDictsList::::dictVal:::"+dictVal);
				tokenDictList.add(tokenVal+":"+dictVal);
			}
		} catch(Exception ex) {
			System.out.print(ex.getMessage());
		}
		return tokenDictList;
	}

	public static void main(String[] args) {
		System.out.println(SQLConstants.SELECT_FTS_CONFIG_BY_SCHEMA_NAME.replace(":schema", "audit"));
	}

}
