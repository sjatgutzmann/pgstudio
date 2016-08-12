/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public interface ModelInfo {
	public String getFullName();
	
	public String getName();
	
	public long getId();
	
	public ITEM_TYPE getItemType();
	
	public String getComment();
	
	public int getSchema();	
}
