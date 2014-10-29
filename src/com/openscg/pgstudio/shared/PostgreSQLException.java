/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.shared;

public class PostgreSQLException extends Exception {
	
	private static final long serialVersionUID = 4608050881199782708L;

	public PostgreSQLException()
	  {
	    super();            
	  }
	
	 public PostgreSQLException(String err)
	  {
	    super(err);    
	  }
}
