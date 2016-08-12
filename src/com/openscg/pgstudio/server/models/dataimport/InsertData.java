package com.openscg.pgstudio.server.models.dataimport;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.openscg.pgstudio.server.models.Columns;

public class InsertData {

    private final static String QUERY_SEPERATOR = ".";
    private final Connection conn;

    public InsertData(Connection conn) {
	super();
	this.conn = conn;
    }

    public String insert(List<String> columnList, List<ArrayList<String>> dataRows, String schema, String tableId,
	    String tableName) throws Exception {

	PreparedStatement pStatement = null;

	String query = null;

	int[] output = null;

	int batchCount = 0;

	try {

	    Columns columns = new Columns(conn);
	    Map<String, String> columnNameDataTypeMap = columns.getColumnDataTypeMap(new Long(tableId).longValue());
	   
	    conn.setAutoCommit(false);

	    query = constructQuery(columnList, schema, tableName, columnNameDataTypeMap, columnList);
	    
	    System.out.println("create table record query :: "+query);

	    pStatement = conn.prepareStatement(query);

	    /* Add data row to statement */

	    for (List<String> dataRow : dataRows) {

		batchCount += 1;
		int index = 0;
		for (String data : dataRow) {

		    String columnName = columnList.get(index);
		    index += 1;

		    String dataType = getDataType(columnName, columnNameDataTypeMap);

		    /*
		     * Handle cvs related different formats with name having
		     * double quotes...
		     */

		    if (data != null) {

			if (data.startsWith("\"")) {

			    data = data.replaceAll("\"", "");
			}
		    }

		    try{
			setData(dataType, pStatement, index, data);
		    }catch(Exception e){
			
			throw new Exception("ERROR:  invalid input syntax for "+columnName+" type "+dataType+ " : "+data);
			
		    }

		}
		pStatement.addBatch();

		if ((batchCount % 1000) == 0) { // if batch count is 1000 insert
						// data to db

		    output = pStatement.executeBatch();
		   
		}
	    }

	    // set batch limit
	    output = pStatement.executeBatch();
	  
	    conn.commit();

	} catch (SQLException sqle) {

	    conn.rollback();
	    sqle.printStackTrace();
	    sqle.getNextException().printStackTrace();
	    throw new Exception(sqle.getMessage() + " " + sqle.getNextException().getMessage());

	} catch (Exception e) {

	    e.printStackTrace();
	    
	    throw e;

	} finally {

	    if (pStatement != null) {
		pStatement.close();
	    }

	}
	return Arrays.toString(output);
    }

    private String getDataType(String columnName, Map<String, String> columnNameDataTypeMap) {

	String dataType = columnNameDataTypeMap.get(columnName.trim());

	if (dataType == null) {

	    /*if column name is in double quotes*/
	    dataType = (columnNameDataTypeMap.get(columnName.trim().replaceAll("\"", "")));

	}

	return dataType;
    }

    private String constructQuery(List<String> firstRow, String schema, String tableName,
	    Map<String, String> columnNameDataTypeMap, List<String> columnList) throws IOException {

	StringBuilder query = new StringBuilder("INSERT INTO ");

	query.append(schema).append(QUERY_SEPERATOR);
	query.append(tableName);

	StringBuilder num_of_parameters = new StringBuilder();

	if (firstRow != null) {
	    // String[] row = firstLine.split(CVS_SPLIT_BY);

	    query.append(" ( ");
	    int index = 0;
	    for (String column : firstRow) {

		String columnName = columnList.get(index);
		String dataType = getDataType(columnName, columnNameDataTypeMap);
		index += 1;
		query.append(column).append(",");
		
		num_of_parameters.append("?::"+dataType).append(",");

	    }
	    /* remove last extra comma */
	    query.replace(query.length() - 1, query.length(), "");
	    num_of_parameters.replace(num_of_parameters.length() - 1, num_of_parameters.length(), "");
	    num_of_parameters.append(" )");

	    query.append(" ) ").append("VALUES (");

	}

	return query.append(num_of_parameters.toString()).toString();
    }


    private void setData(String dataType, PreparedStatement pStatement, int parameterIndex, String data)
	    throws NumberFormatException, SQLException, ParseException {

	switch (dataType) {

	case "integer":
	case "oid":
	    if (data != null && !data.isEmpty())
		pStatement.setLong(parameterIndex, new Long(data).longValue());
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.INTEGER);
	    break;

	case "smallint":
	    if (data != null && !data.isEmpty())
		pStatement.setShort(parameterIndex, new Short(data).shortValue());
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.SMALLINT);
	    break;

	case "date":
	    if (data != null && !data.isEmpty())
		pStatement.setDate(parameterIndex, Dateutil.convertStringToSqlDate(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.DATE);

	    break;
	case "numeric":
	case "decimal":
	    if (data != null && !data.isEmpty())
		pStatement.setBigDecimal(parameterIndex, new BigDecimal(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.DECIMAL);
	    break;

	case "bigint":
	    if (data != null && !data.isEmpty())
		pStatement.setLong(parameterIndex, new Long(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.BIGINT);
	    break;

	case "double precision":
	    if (data != null && !data.isEmpty())
		pStatement.setDouble(parameterIndex, new Double(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.DOUBLE);
	    break;

	case "boolean":
	    if (data != null && !data.isEmpty())
		pStatement.setBoolean(parameterIndex, new Boolean(data).booleanValue());
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.BOOLEAN);
	    break;

	case "bit":
	    if (data != null && !data.isEmpty())
		pStatement.setBoolean(parameterIndex, new Boolean(data).booleanValue());
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.BIT);
	    break;

	case "timestamp without time zone":
	    if (data != null && !data.isEmpty())
		pStatement.setTimestamp(parameterIndex, Dateutil.convertStringToSqlTimestamp(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.TIMESTAMP);
	    break;

	case "time without time zone":
	    if (data != null && !data.isEmpty())
		pStatement.setTime(parameterIndex, Dateutil.convertStringToSqlTime(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.TIME);
	    break;

	case "timestamp with time zone":
	    if (data != null && !data.isEmpty())
		pStatement.setTimestamp(parameterIndex, Dateutil.convertStringToSqlTimestampWithTimeZone(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.TIMESTAMP);
	    break;

	case "time with time zone":
	    if (data != null && !data.isEmpty())
		pStatement.setTime(parameterIndex, Dateutil.convertStringToSqlTime(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.TIME);
	    break;

	case "real":
	    if (data != null && !data.isEmpty())
		pStatement.setFloat(parameterIndex, new Float(data));
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.FLOAT);
	    break;

	default: /*
		  * bit varying, char, character, inet, macaddr, "path": case
		  * "point": case "box": case "circle": case "polygon": etc
		  */
	    if (data != null && !data.isEmpty())
		pStatement.setString(parameterIndex, data);
	    else
		pStatement.setNull(parameterIndex, java.sql.Types.NULL);
	}

    }

}
