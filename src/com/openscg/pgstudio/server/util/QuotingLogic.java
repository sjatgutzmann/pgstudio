/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.util;

public class QuotingLogic {

	//Method to automatically add quotes to the name wherever required
	public String addQuote(String name) {

		StringBuffer temp=new StringBuffer();
		int j=name.length();
		int k=0;

		for(int i=0;i<j;i++) {

			if(name.charAt(i)=='"')
				//Add a quote wherever '"' is encountered
				temp.insert(k++, '"');

			temp.insert(k++,name.charAt(i));
			}
//Finally, wrap the name within double quotes
		return ('"'+temp.toString()+'"');
	}
}