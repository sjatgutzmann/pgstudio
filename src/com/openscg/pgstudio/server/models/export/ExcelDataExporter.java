package com.openscg.pgstudio.server.models.export;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelDataExporter extends AbstractDataExporter {

	public ExcelDataExporter(Connection conn) {
		super(conn);
	}

	@Override
	protected Object exportData(ResultSet rs) {
		HSSFWorkbook workbook = null;

		try {
			int rowCount = 0;
			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();

			// Create meta data
			HSSFRow row = sheet.createRow(rowCount);
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				row.createCell(i - 1).setCellValue(rs.getMetaData().getColumnName(i));				
			}

			// Create table data
			while (rs.next()) {
				String val = "";
				row = sheet.createRow(++rowCount);
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					if (rs.getObject(i) != null) {
						switch (rs.getMetaData().getColumnType(i)) {
						case -5: /* BIGINT */
						case 4: /* INT */
						case 5: /* SMALLINT */
							val = Long.toString(rs.getLong(i));
							break;
						case -7: /* BINARY */
						case 16: /* BOOLEAN */
							if (rs.getBoolean(i)) {
								val = "true";
							} else {
								val = "false";
							}
							break;
						case 12: /* VARCHAR */
							val = rs.getString(i);
							break;
						case 91: /* DATE */
							val = rs.getDate(i).toString();
							break;
						case 92: /* TIME */
							val = rs.getTime(i).toString();
							break;
						case 93: /* TIMESTAMP */
							val = rs.getTimestamp(i).toString();
							break;
						default:
							val = rs.getObject(i).toString();
						}
					}
					row.createCell(i - 1).setCellValue(val);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return workbook;
	}
}
