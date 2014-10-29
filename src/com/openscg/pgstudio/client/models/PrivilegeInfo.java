/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;

public class PrivilegeInfo implements Comparable<PrivilegeInfo> {

    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<PrivilegeInfo> KEY_PROVIDER = new ProvidesKey<PrivilegeInfo>() {
      public Object getKey(PrivilegeInfo IDX) {
        return IDX == null ? null : IDX.getId();
      }
    };

    private static int nextId = 0;

    private final int id;
    
	private String grantee;
    private String grantor;
    private boolean grantable;
    private String type;
    
    public PrivilegeInfo(String grantee, String type) {
      this.id = nextId;
      nextId++;
      
      this.setGrantee(grantee);
      this.setType(type);
    }

    public int compareTo(PrivilegeInfo o) {
    	if (o == null || o.grantee == null || o.type == null)
    		return -1;
    	
    	if (o.grantee.compareTo(grantee) != 0)
    		return -1;
    	
    	if (o.type.compareTo(type) != 0)
    		return -1;
    	
    	return 0;    	
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof PrivilegeInfo) {
        return id == ((PrivilegeInfo) o).id;
      }
      return false;
    }

    /**
     * @return the unique ID of the contact
     */
    public int getId() {
      return this.id;
    }

    @Override
    public int hashCode() {
      return id;
    }

	public String getGrantee() {
		return grantee;
	}

	public void setGrantee(String grantee) {
		this.grantee = grantee;
	}

	public String getGrantor() {
		return grantor;
	}

	public void setGrantor(String grantor) {
		this.grantor = grantor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isGrantable() {
		return grantable;
	}

	public void setGrantable(boolean grantable) {
		this.grantable = grantable;
	}
  }
