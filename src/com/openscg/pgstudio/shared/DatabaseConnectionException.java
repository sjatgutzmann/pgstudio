/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.shared;

public class DatabaseConnectionException extends Exception {
	
	private static final long serialVersionUID = 541471164765459568L;

	public enum DATABASE_ERROR_TYPE {
		INVALID_CONNECTION_STRING, INVALID_TOKEN, TIMED_OUT, UNKNOWN
	}
	
	private DATABASE_ERROR_TYPE type;
	
	public DatabaseConnectionException()
	  {
	    super();            
	    type = DATABASE_ERROR_TYPE.UNKNOWN;
	  }
	
	 public DatabaseConnectionException(String err)
	  {
	    super(err);    
	    type = DATABASE_ERROR_TYPE.UNKNOWN;
	  }

	 public DatabaseConnectionException(String err, DATABASE_ERROR_TYPE type)
	  {
	    super(err);    
	    this.type = type;
	  }

	public DATABASE_ERROR_TYPE getType() {
		return type;
	}
	 
	 
}
