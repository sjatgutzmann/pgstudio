/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.openscg.pgstudio.server.util.ConnectionInfo;
import com.openscg.pgstudio.server.util.ConnectionManager;
import com.openscg.pgstudio.shared.DatabaseConnectionException;

public class LoginHandler extends HttpServlet {

	private static final long serialVersionUID = -3598253864439571563L;

public void doPost(HttpServletRequest req, HttpServletResponse res)
                                throws ServletException, IOException {
    res.setContentType("text/html");

    String dbURL = req.getParameter("dbURL");
    int dbPort = Integer.parseInt(req.getParameter("dbPort"));
    String dbName = req.getParameter("dbName");
    if(dbName==null)
	dbName="";
    String username = req.getParameter("username");
    String passwd = req.getParameter("password");

	String clientIP = ConnectionInfo.remoteAddr(req);
    String userAgent = req.getHeader("User-Agent");
    String queryString = req.getQueryString();
    
    String token = "";
    
    ConnectionManager connMgr = new ConnectionManager();

	HttpSession session = req.getSession(true);
    try {
		token = connMgr.addConnection(dbURL, dbPort, dbName, username, passwd, clientIP, userAgent);
	} catch (DatabaseConnectionException e) {
		session.setAttribute("err_msg", e.getMessage());
		session.setAttribute("dbName", dbName);
		session.setAttribute("dbURL", dbURL);
		session.setAttribute("username", username);
	}
    
	if (!token.equals("")) {
		session.setAttribute("dbToken", token);
		session.setAttribute("dbVersion", Integer.toString(connMgr.getDatabaseVersion()));
	}

    // Try redirecting the client to the page he first tried to access
	try {
		String target = this.getServletContext().getInitParameter("home_url");

		if (queryString != null)
			target = target + "?"+ queryString;

		res.sendRedirect(target);
		return;
	} catch (Exception ignored) {
	}

  }
}
