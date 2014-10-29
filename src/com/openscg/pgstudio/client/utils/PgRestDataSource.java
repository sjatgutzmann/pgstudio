/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.utils;

import java.util.HashMap;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;

public class PgRestDataSource extends RestDataSource {

	private String query;
	
	public PgRestDataSource() {
		this.setDataFormat(DSDataFormat.JSON);
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	@Override
	public Object transformRequest(DSRequest dsRequest)	{
		
		if (dsRequest.getOperationType().equals(DSOperationType.FETCH)) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("query", query);

			dsRequest.setParams(map);
			dsRequest.setHttpMethod("POST");
			
		}
		
		return super.transformRequest(dsRequest);
	}
}
