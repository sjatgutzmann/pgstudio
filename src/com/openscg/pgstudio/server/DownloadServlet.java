package com.openscg.pgstudio.server;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;
import com.openscg.pgstudio.server.models.export.AbstractDataExporter;
import com.openscg.pgstudio.server.models.export.CSVDataExporter;
import com.openscg.pgstudio.server.models.export.ExcelDataExporter;
import com.openscg.pgstudio.server.util.ConnectionInfo;
import com.openscg.pgstudio.server.util.ConnectionManager;
import com.openscg.pgstudio.shared.DatabaseConnectionException;

/**
 * Servlet implementation class DownloadServlet.
 * This servlet extracts the data and exports it in the user desired format.
 */
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String connectionToken = request.getParameter("connectionToken");
			String item =request.getParameter("item");
			String fileType = request.getParameter("fileType");
			String type = request.getParameter("type");
			response.setContentType("application/octet-stream");

			String clientIP = ConnectionInfo.remoteAddr(request);
			String userAgent = request.getHeader("User-Agent");
			String query = request.getParameter("query");
			generateFile(response, connectionToken, clientIP, userAgent, item, type, fileType, query);
		} catch (Exception e) {
			e.printStackTrace();
			response.getOutputStream().println("There was an error during export, please try again");
			response.getOutputStream().close();
		}
	}

	private void generateFile(HttpServletResponse response, String connectionToken, String clientIP, String userAgent,
			String item, String type, String fileType, String query) throws Exception {
		ConnectionManager connMgr = new ConnectionManager();

		AbstractDataExporter dataExporter = null;
		try {
			if ("csv".equalsIgnoreCase(fileType)) {
				dataExporter = new CSVDataExporter(connMgr.getConnection(connectionToken, clientIP, userAgent));
				String resp = (String) getData(dataExporter, item, type, query);
				response.setHeader("Content-Disposition", "inline; filename=\"Download.csv\"");
				response.getOutputStream().write(resp.getBytes());				
			} else {
				dataExporter = new ExcelDataExporter(connMgr.getConnection(connectionToken, clientIP, userAgent));
				HSSFWorkbook workbook = (HSSFWorkbook) getData(dataExporter, item, type, query);
				response.setHeader("Content-Disposition", "inline; filename=\"Download.xls\"");
				workbook.write(response.getOutputStream());
			}
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		catch (DatabaseConnectionException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	private Object getData(AbstractDataExporter dataExporter, String item, String type, String query)
			throws SQLException {
		if (query == null)
			return dataExporter.getData(item, type);
		return dataExporter.getData(query);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
