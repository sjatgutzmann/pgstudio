/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.providers;

import com.openscg.pgstudio.client.models.DatabaseObjectInfo;

public interface ModelListProvider extends ListProvider {
	public void setSchema(DatabaseObjectInfo schema);	
}
