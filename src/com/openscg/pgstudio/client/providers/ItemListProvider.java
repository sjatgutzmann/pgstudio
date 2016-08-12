/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.providers;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public interface ItemListProvider extends ListProvider {
	public void setItem(int schema, long item, ITEM_TYPE type);	
}
