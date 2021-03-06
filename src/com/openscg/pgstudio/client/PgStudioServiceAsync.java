/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
 * The async counterpart of <code>PgStudioService</code>.
 */
public interface PgStudioServiceAsync {

	void getConnectionInfoMessage(String connectionToken, AsyncCallback<String> callback)
	        throws IllegalArgumentException;
	
	void getList(String connectionToken, DATABASE_OBJECT_TYPE type, AsyncCallback<String> callback)
	        throws IllegalArgumentException;

	void getList(String connectionToken, int schema, ITEM_TYPE type, AsyncCallback<String> callback)
    		throws IllegalArgumentException;

	void getRangeDiffFunctionList(String connectionToken, String schema, String subType, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getTriggerFunctionList(String connectionToken, int schema, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	
	void getItemObjectList(String connectionToken, long item, ITEM_TYPE type, ITEM_OBJECT_TYPE object, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getItemMetaData(String connectionToken, long item, ITEM_TYPE type, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getItemData(String connectionToken, long item, ITEM_TYPE type, int count, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getQueryMetaData(String connectionToken, String query, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void executeQuery(String connectionToken, String query, String queryType, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void dropItem(String connectionToken, long item, ITEM_TYPE type, boolean cascade, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void analyze(String connectionToken, long item, ITEM_TYPE type, boolean vacuum, boolean vacuumFull, boolean reindex, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void renameItem(String connectionToken, long item, ITEM_TYPE type, String newName, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void truncate(String connectionToken, long item, ITEM_TYPE type, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void createIndex(String connectionToken, long item, String indexName, INDEX_TYPE indexType, boolean isUnique, boolean isConcurrently, ArrayList<String> columnList, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void dropItemObject(String connectionToken, long item, ITEM_TYPE type, String objectName, ITEM_OBJECT_TYPE objType, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void renameItemObject(String connectionToken, long item, ITEM_TYPE type, String objectName, ITEM_OBJECT_TYPE objType, String newObjectName, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void renameSchema(String connectionToken, String oldSchema, String schema, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void dropSchema(String connectionToken, String schemaName, boolean cascade, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void createSchema(String connectionToken, String schemaName, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void getExplainResult(String connectionToken, String query, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void createView(String connectionToken, String schema, String viewName, String definition, String comment, boolean isMaterialized, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void createColumn(String connectionToken, long item, String columnName, String datatype, String comment, boolean not_null, String defaultval, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void createTable(String connectionToken, int schema, String tableName, boolean unlogged, boolean temporary, String fill, ArrayList<String> col_list,  HashMap<Integer,String> commentLog, ArrayList<String> col_index, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void createTableLike(String connectionToken, int schema, String tableName, String source, boolean defaults, boolean constraints, boolean indexes, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void createUniqueConstraint(String connectionToken, long item, String constraintName, boolean isPrimaryKey, ArrayList<String> columnList, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	
	void createCheckConstraint(String connectionToken, long item, String constraintName, String definition, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	
	void createForeignKeyConstraint(String connectionToken, long item, String constraintName, ArrayList<String> columnList, String referenceTable, ArrayList<String> referenceList, AsyncCallback<String> callback)
			throws IllegalArgumentException;
			
	void createSequence(String connectionToken, int schema, String sequenceName, boolean temporary, String increment, String minValue, String maxValue, String start, int cache, boolean cycle, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void createFunction(String connectionToken, AlterFunctionRequest funcRequest, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	
	void createType(String connectionToken, String schema, String typeName, TYPE_FORM form, String baseType, String definition, ArrayList<String> attributeList, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void  createForeignTable(String connectionToken, String schema, String tableName, String server, ArrayList<String> columns, HashMap<Integer, String> comments, ArrayList<String> options, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void refreshMaterializedView(String connectionToken, String schema, String viewName, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void createRule(String connectionToken, long item, ITEM_TYPE type, String ruleName, String event, String ruleType, String definition, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void createTrigger(String connectionToken, long item, ITEM_TYPE type, String triggerName, String event, String triggerType, String forEach, String function, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void revoke(String connectionToken, long item, ITEM_TYPE type, String privilege, String grantee, boolean cascade, AsyncCallback<String> callback) 
					throws IllegalArgumentException;
			
	void grant(String connectionToken, long item, ITEM_TYPE type, ArrayList<String> privileges, String grantee, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void getActivity(String connectionToken, AsyncCallback<String> callback)
	        throws IllegalArgumentException;

	void configureRowSecurity(String connectionToken, long item, boolean hasRowSecurity, boolean forceRowSecurity, AsyncCallback<String> callback)
	        throws IllegalArgumentException;

	void createPolicy(String connectionToken, long item, String policyName, String cmd, String role, String using, String withCheck, AsyncCallback<String> callback)
	        throws IllegalArgumentException;
	
	void doLogout(String connectionToken, String source, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;

	void invalidateSession(AsyncCallback<Void> callback);
	
	void alterFunction(String connectionToken, AlterFunctionRequest alterFunctionRequest, AsyncCallback<String> asyncCallback) throws Exception;
	
	void incrementValue(String connectionToken, int schema, String sequenceName, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;
	
	void alterSequence(String connectionToken, int schema, String sequenceName, String increment, String minValue, String maxValue, String start, int cache, boolean cycle, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void changeSequenceValue(String connectionToken, int schema, String sequenceName, String value, AsyncCallback<String> asyncCallback);

	void restartSequence(String connectionToken, int schema, String sequenceName, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;

	void resetSequence(String connectionToken, int schema, String sequenceName, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;

	void getLanguageFullList(String connectionToken, DATABASE_OBJECT_TYPE type, AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getFunctionFullList(String connectionToken, int schema, ITEM_TYPE type, AsyncCallback<String> callback) throws IllegalArgumentException;

	void fetchDictionaryTemplates(String connectionToken, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;
	
	void addDictionary(String connectionToken, int schema, String dictName, String template, String option, String comment, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;

	void createFTSConfiguration(String connectionToken, String schemaName, String configurationName, String templateName, String parserName, String comments, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void getFTSTemplatesList(String connectionToken, AsyncCallback<String> callback) throws Exception;
	
	void getFTSParsersList(String connectionToken, AsyncCallback<String> callback) throws Exception;
	
	void alterFTSConfiguration(String connectionToken, String schemaName, String configurationName, String comments, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void dropFTSConfiguration(String connectionToken, String schemaName, String configurationName, boolean cascade, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void getFTSConfigurations(String connectionToken, String schemaName, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void fetchDictionaryDetails(String connectionToke, int schema, long id, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;

	void alterDictionary(String connectionToken, int schema, String dictName, String newDictName, String options, String comments, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;

	void createFTSMapping(String connectionToken, String schemaName, String configurationName, String tokenType, String dictionary, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void getFTSTokensList(String connectionToken, AsyncCallback<String> callback) throws Exception;

	void getFTSDictionariesList(String connectionToken, AsyncCallback<String> callback) throws Exception;

	void alterFTSMapping(String connectionToken, String schemaName, String configurationName, String tokenType, String oldDict, String newDict, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void dropFTSMapping(String connectionToken, String schemaName, String configurationName, String token, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void getFTSConfigurationDetails(String connectionToken, String schemaName, String configName, AsyncCallback<String> callback) throws DatabaseConnectionException, PostgreSQLException;

	void importData(String connectionToken, List<String> columnList, List<ArrayList<String>> dataRows, int schema, String tableId, String tableName, AsyncCallback<String> callback)   throws DatabaseConnectionException, PostgreSQLException;

	void dropItemData(String connectionToken, int schema, String tableName,AsyncCallback<String> callback) throws IllegalArgumentException, DatabaseConnectionException, PostgreSQLException;

	void connectToDatabase(String connectionToken, String databaseName , AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;

	void fetchDomainDetails(String connectionToken, long item, AsyncCallback<DomainDetails> asyncCallback)  throws DatabaseConnectionException, PostgreSQLException;

	void addCheck(String connectionToken, int schema, String domainName, String checkName, String expression, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;

	void dropCheck(String connectionToken, int schema, String domainName, String checkName, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;

	void alterDomain(String connectionToken, int schema, AlterDomainRequest request, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;

        void createItemData(String connectionToken, int schema, String tableName, ArrayList<String>colNames, ArrayList<String> values, AsyncCallback<String> callback) throws IllegalArgumentException, DatabaseConnectionException, PostgreSQLException;
	
	void alterColumn(String connectionToken, long item, AlterColumnRequest command, AsyncCallback<String> asyncCallback) throws DatabaseConnectionException, PostgreSQLException;
	}
