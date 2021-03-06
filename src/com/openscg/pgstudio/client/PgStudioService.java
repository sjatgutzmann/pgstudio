/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openscg.pgstudio.client.PgStudio.DATABASE_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudio.INDEX_TYPE;
import com.openscg.pgstudio.client.PgStudio.ITEM_OBJECT_TYPE;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.client.PgStudio.TYPE_FORM;
import com.openscg.pgstudio.shared.DatabaseConnectionException;
import com.openscg.pgstudio.shared.PostgreSQLException;
import com.openscg.pgstudio.shared.dto.AlterColumnRequest;
import com.openscg.pgstudio.shared.dto.AlterDomainRequest;
import com.openscg.pgstudio.shared.dto.AlterFunctionRequest;
import com.openscg.pgstudio.shared.dto.DomainDetails;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("studio")
public interface PgStudioService extends RemoteService {
	String getConnectionInfoMessage(String connectionToken) throws IllegalArgumentException, DatabaseConnectionException;

	String getList(String connectionToken, DATABASE_OBJECT_TYPE type) throws IllegalArgumentException, DatabaseConnectionException;

	String getList(String connectionToken, int schema, ITEM_TYPE type) throws IllegalArgumentException, DatabaseConnectionException;

	String getRangeDiffFunctionList(String connectionToken, String schema, String subType)  throws IllegalArgumentException, DatabaseConnectionException;
	
	String getTriggerFunctionList(String connectionToken, int schema)  throws IllegalArgumentException, DatabaseConnectionException;
	
	String getItemObjectList(String connectionToken, long item, ITEM_TYPE type, ITEM_OBJECT_TYPE object) throws IllegalArgumentException, DatabaseConnectionException, PostgreSQLException;

	String getItemMetaData(String connectionToken, long item, ITEM_TYPE type) throws IllegalArgumentException, DatabaseConnectionException, PostgreSQLException;

	String getItemData(String connectionToken, long item, ITEM_TYPE type, int count) throws IllegalArgumentException, DatabaseConnectionException, PostgreSQLException;

	String getQueryMetaData(String connectionToken, String query) throws IllegalArgumentException, DatabaseConnectionException;

	String executeQuery(String connectionToken, String query, String queryType) throws IllegalArgumentException, DatabaseConnectionException;
	
	String dropItem(String connectionToken, long item, ITEM_TYPE type, boolean cascade) throws DatabaseConnectionException, PostgreSQLException;
	
	String analyze(String connectionToken, long item, ITEM_TYPE type, boolean vacuum, boolean vacuumFull, boolean reindex) throws DatabaseConnectionException, PostgreSQLException;
	
	String renameItem(String connectionToken, long item, ITEM_TYPE type, String newName) throws DatabaseConnectionException, PostgreSQLException;

	String truncate(String connectionToken, long item, ITEM_TYPE type) throws DatabaseConnectionException, PostgreSQLException;

	String createColumn(String connectionToken, long item, String columnName, String datatype, String comment, boolean not_null, String defaultval) throws DatabaseConnectionException, PostgreSQLException;

	String createIndex(String connectionToken, long item, String indexName, INDEX_TYPE indexType, boolean isUnique, boolean isConcurrently, ArrayList<String> columnList) throws DatabaseConnectionException, PostgreSQLException;

	String dropItemObject(String connectionToken, long item, ITEM_TYPE type, String objectName, ITEM_OBJECT_TYPE objType) throws DatabaseConnectionException, PostgreSQLException;

	String renameItemObject(String connectionToken, long item, ITEM_TYPE type, String objectName, ITEM_OBJECT_TYPE objType, String newObjectName) throws DatabaseConnectionException, PostgreSQLException;

	String renameSchema(String connectionToken, String oldSchema, String schema) throws DatabaseConnectionException;
	
	String dropSchema(String connectionToken, String schemaName, boolean cascade) throws DatabaseConnectionException;

	String createSchema(String connectionToken, String schemaName) throws DatabaseConnectionException;

	String getExplainResult(String connectionToken, String query) throws IllegalArgumentException, DatabaseConnectionException;

	String createView(String connectionToken, String schema, String viewName, String definition, String comment, boolean isMaterialized) throws DatabaseConnectionException;

	String createTable(String connectionToken, int schema, String tableName, boolean unlogged, boolean temporary, String fill,	ArrayList<String> col_list, HashMap<Integer, String> commentLog, ArrayList<String> col_index) throws DatabaseConnectionException, PostgreSQLException;

	String createTableLike(String connectionToken, int schema, String tableName, String source, boolean defaults, boolean constraints, boolean indexes)  throws DatabaseConnectionException, PostgreSQLException;

	String createUniqueConstraint(String connectionToken, long item, String constraintName, boolean isPrimaryKey, ArrayList<String> columnList) throws DatabaseConnectionException, PostgreSQLException;

	String createCheckConstraint(String connectionToken, long item, String constraintName, String definition) throws DatabaseConnectionException, PostgreSQLException;
	
	String createForeignKeyConstraint(String connectionToken, long item, String constraintName, ArrayList<String> columnList, String referenceTable, ArrayList<String> referenceList) throws DatabaseConnectionException, PostgreSQLException;
	
	String createSequence(String connectionToken, int schema, String sequenceName, boolean temporary, String increment, String minValue, String maxValue, String start, int cache, boolean cycle) throws DatabaseConnectionException, PostgreSQLException;
	
	String createFunction(String connectionToken, AlterFunctionRequest funcRequest) throws DatabaseConnectionException, PostgreSQLException;
	
	String createType(String connectionToken, String schema, String typeName, TYPE_FORM form, String baseType, String definition, ArrayList<String> attributeList) throws DatabaseConnectionException;

	String createForeignTable(String connectionToken, String schema, String tableName, String server, ArrayList<String> columns, HashMap<Integer, String> comments, ArrayList<String> options) throws DatabaseConnectionException;
	
	String refreshMaterializedView(String connectionToken, String schema, String viewName) throws DatabaseConnectionException;

	String createRule(String connectionToken, long item, ITEM_TYPE type, String ruleName, String event, String ruleType, String definition) throws DatabaseConnectionException, PostgreSQLException;
	
	String createTrigger(String connectionToken, long item, ITEM_TYPE type, String triggerName, String event, String triggerType, String forEach, String function) throws DatabaseConnectionException, PostgreSQLException;
	
	String revoke(String connectionToken, long item, ITEM_TYPE type, String privilege, String grantee, boolean cascade) throws DatabaseConnectionException, PostgreSQLException;
	
	String grant(String connectionToken, long item, ITEM_TYPE type, ArrayList<String> privileges, String grantee) throws DatabaseConnectionException, PostgreSQLException;

	String getActivity(String connectionToken) throws IllegalArgumentException, DatabaseConnectionException;

	String configureRowSecurity(String connectionToken, long item, boolean hasRowSecurity, boolean forceRowSecurity) throws DatabaseConnectionException, PostgreSQLException;

	public String createPolicy(String connectionToken, long item, String policyName, String cmd, String role, String using, String withCheck) throws DatabaseConnectionException, PostgreSQLException;
	
	void doLogout(String connectionToken, String source) throws IllegalArgumentException, DatabaseConnectionException;

	void invalidateSession();
	
	String alterFunction(String connectionToken, AlterFunctionRequest alterFunctionRequest) throws Exception;
	
	String incrementValue(String connectionToken, int schema, String sequenceName) throws DatabaseConnectionException, PostgreSQLException;
	
	String alterSequence(String connectionToken, int schema, String sequenceName, String increment, String minValue,String maxValue, String start, int cache, boolean cycle) throws DatabaseConnectionException, PostgreSQLException;

	String changeSequenceValue(String connectionToken, int schema, String sequenceName, String value) throws DatabaseConnectionException, PostgreSQLException;

	String restartSequence(String connectionToken, int schema, String sequenceName) throws DatabaseConnectionException, PostgreSQLException;

	String resetSequence(String connectionToken, int schema, String sequenceName) throws DatabaseConnectionException, PostgreSQLException;
	
	String getLanguageFullList(String connectionToken, DATABASE_OBJECT_TYPE type) throws IllegalArgumentException, DatabaseConnectionException;

	String getFunctionFullList(String connectionToken, int schema, ITEM_TYPE type) throws IllegalArgumentException, DatabaseConnectionException;

	String fetchDictionaryTemplates(String connectionToken) throws DatabaseConnectionException, PostgreSQLException;

	String addDictionary(String connectionToken, int schema, String dictName, String template, String option, String comments) throws DatabaseConnectionException, PostgreSQLException;

	String createFTSConfiguration(String connectionToken, String schemaName, String configurationName, String templateName, String parserName, String comments) throws DatabaseConnectionException, PostgreSQLException;

	String getFTSTemplatesList(String connectionToken) throws Exception;
	
	String getFTSParsersList(String connectionToken) throws Exception;
	
	String alterFTSConfiguration(String connectionToken, String schemaName, String configurationName, String comments) throws DatabaseConnectionException, PostgreSQLException;

	String dropFTSConfiguration(String connectionToken, String schemaName, String configurationName, boolean cascade) throws DatabaseConnectionException, PostgreSQLException;
	
	String getFTSConfigurations(String connectionToken, String schemaName) throws DatabaseConnectionException, PostgreSQLException;

	String fetchDictionaryDetails(String connectionToken, int schema, long id) throws DatabaseConnectionException, PostgreSQLException;

	String alterDictionary(String connectionToken, int schema, String dictName, String newDictName, String options, String comments) throws DatabaseConnectionException, PostgreSQLException;

	String createFTSMapping(String connectionToken, String schemaName, String configurationName, String tokenType, String dictionary) throws DatabaseConnectionException, PostgreSQLException;

	String getFTSTokensList(String connectionToken) throws Exception;
	
	String getFTSDictionariesList(String connectionToken) throws Exception;

	String alterFTSMapping(String connectionToken, String schemaName, String configurationName, String tokenType, String oldDict, String newDict) throws DatabaseConnectionException, PostgreSQLException;

	String dropFTSMapping(String connectionToken, String schemaName, String configurationName, String tokenType) throws DatabaseConnectionException, PostgreSQLException;

	String getFTSConfigurationDetails(String connectionToken, String schemaName, String configName) throws DatabaseConnectionException, PostgreSQLException;

	String importData(String connectionToken, List<String> columnList, List<ArrayList<String>> dataRows, int schema, String tableId, String tableName)  throws DatabaseConnectionException, PostgreSQLException;

	String dropItemData(String connectionToken, int schema, String tableName) throws IllegalArgumentException, DatabaseConnectionException, PostgreSQLException;

	void connectToDatabase(String connectionToken, String database) throws DatabaseConnectionException, PostgreSQLException;

	DomainDetails fetchDomainDetails(String connectionToken, long item)  throws DatabaseConnectionException, PostgreSQLException;

	String addCheck(String connectionToken, int schema, String domainName, String checkName, String expression) throws DatabaseConnectionException, PostgreSQLException;

	String dropCheck(String connectionToken, int schema, String domainName, String checkName) throws DatabaseConnectionException, PostgreSQLException;

	String alterDomain(String connectionToken, int schema, AlterDomainRequest request) throws DatabaseConnectionException, PostgreSQLException;

	public String createItemData(String connectionToken, int schema, String tableName, ArrayList<String>colNames, ArrayList<String> values) throws DatabaseConnectionException, PostgreSQLException;

	String alterColumn(String connectionToken, long item, AlterColumnRequest alterColumnRequest) throws DatabaseConnectionException, PostgreSQLException;
}
