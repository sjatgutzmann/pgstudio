package com.openscg.pgstudio.server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import com.openscg.pgstudio.server.models.dataimport.InsertData;
import com.openscg.pgstudio.server.util.ConnectionInfo;
import com.openscg.pgstudio.server.util.ConnectionManager;
import com.openscg.pgstudio.server.util.ProcessFile;

public class ImportDataFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImportDataFileServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		doPost( request,  response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	    String tableName = "";
		boolean isMultiPart = ServletFileUpload
				.isMultipartContent(new ServletRequestContext(request));
		
		if(isMultiPart) {
			
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			try {
				
				
			    String schema = request.getParameter("schema");
			    	tableName = request.getParameter("tableName");
				String tableId = request.getParameter("tableId");
				String connectionToken = request.getParameter("connectionToken");
				ConnectionManager connMgr = new ConnectionManager();
				
				String clientIP = ConnectionInfo.remoteAddr(request);
				String userAgent = request.getHeader("User-Agent");
				
				Connection conn = connMgr.getConnection(connectionToken,clientIP, userAgent);
				
				List<FileItem> items = upload.parseRequest(request);
				
				
				Iterator<FileItem> iter = items.iterator();
				
				while (iter.hasNext()) {
				    FileItem item = iter.next();

				    if (item.isFormField()) {
				    	
				    	    /*String fieldName = item.getFieldName();
				    	    String fileName = item.getName();
				    	    String contentType = item.getContentType();
				    	    boolean isInMemory = item.isInMemory();
				    	    long sizeInBytes = item.getSize();*/
				    	    
				    } else {
				        
					InputStream iStream = item.getInputStream();
					List<String> columnList = new ArrayList<String>();
					List<ArrayList<String>> dataRows = new ArrayList<ArrayList<String>>();

					ProcessFile.processCSVFile(iStream, columnList, dataRows);
					
					InsertData insertDataUtil = new InsertData(conn);
					insertDataUtil.insert(columnList, dataRows, schema, tableId, tableName);
				    	
				    }
				}
	 
			} catch(FileUploadException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().print("<html><body>"+e.getMessage()+"</body></html>");
				response.getWriter().close();
			}catch(IOException e){
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().print("<html><body>"+e.getMessage()+"</body></html>");
				response.getWriter().close();
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				
				if(e instanceof  NullPointerException){
				    
				    response.getWriter().print("<html><body>ERROR : Please import valid CSV file for table "+tableName+"</body></html>");
				}else{
				
				    response.getWriter().print("<html><body>"+e.getMessage().replace("Call getNextException to see the cause", "")+"</body></html>");
				}
				response.getWriter().close();
			}finally{
			    
			    
			}
		
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("<html>");
		response.getWriter().print("<body>CSV file import successfully</body>");
		response.getWriter().println("</html>");
	}
		
		
}
