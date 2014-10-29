/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;

public class ActivityInfo implements Comparable<ActivityInfo> {

    public static final ProvidesKey<ActivityInfo> KEY_PROVIDER = new ProvidesKey<ActivityInfo>() {
      public Object getKey(ActivityInfo IDX) {
        return IDX == null ? null : IDX.getPid();
      }
    };

    private final int pid;

    private String datName;  
    private String userName;
    private String state;
    private String clientAddr;
    private String backendStart;
    private String xactStart;
    private String queryTime;
    private boolean waiting;
    private String query;
    
    public ActivityInfo(int pid) {
    	this.pid = pid;
    }
    

    @Override
    public boolean equals(Object o) {
      if (o instanceof ActivityInfo) {
        return pid == ((ActivityInfo) o).pid;
      }
      return false;
    }


	public String getDatName() {
		return datName;
	}


	public void setDatName(String datName) {
		this.datName = datName;
	}

	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getClientAddr() {
		return clientAddr;
	}


	public void setClientAddr(String clientAddr) {
		this.clientAddr = clientAddr;
	}


	public String getBackendStart() {
		return backendStart;
	}


	public void setBackendStart(String backendStart) {
		this.backendStart = backendStart;
	}


	public String getXactStart() {
		return xactStart;
	}


	public void setXactStart(String xactStart) {
		this.xactStart = xactStart;
	}


	public String getQueryTime() {
		return queryTime;
	}


	public void setQueryTime(String queryTime) {
		this.queryTime = queryTime;
	}


	public boolean isWaiting() {
		return waiting;
	}


	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}


	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}


	public int getPid() {
		return pid;
	}


	@Override
	public int compareTo(ActivityInfo o) {
		// TODO Auto-generated method stub
		return 0;
	}

  }
