package com.openscg.pgstudio.server.models.export;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.openscg.pgstudio.server.util.ConnectionManager;
import com.openscg.pgstudio.shared.DatabaseConnectionException;

public class CSVDataExporter extends AbstractDataExporter {

	public CSVDataExporter(Connection conn) {
		super(conn);
	}

	@Override
	protected Object exportData(ResultSet rs) {

		StringBuffer sb = new StringBuffer();
		try {

			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				sb.append(rs.getMetaData().getColumnName(i));
				if (i != rs.getMetaData().getColumnCount()) {
					sb.append(",");
				}				
			}

			// Create table data
			while (rs.next()) {
				sb.append("\n");
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					String columnValue = rs.getString(i);
					sb.append(columnValue == null ? "": "\""+columnValue+"\""); // Setting empty when null.					
					if (i != rs.getMetaData().getColumnCount()) {
						sb.append(",");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		
			try {
				ConnectionManager connMgr = new ConnectionManager();
				String token = connMgr.addConnection("localhost", 5432, "postgres", "postgres", "postgres", "127.0.0.1", "mac");
				new CSVDataExporter(connMgr.getConnection(token, "127.0.0.1", "mac")).getData("select * from \"Jobs\" order by ctid");
			} catch (SQLException e) {				
				e.printStackTrace();
			} catch (DatabaseConnectionException e) {
				e.printStackTrace();
			}
		
	}
}
