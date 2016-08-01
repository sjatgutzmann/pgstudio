package com.openscg.pgstudio.shared;

/**
 * Interface to declare shared constants between client and server components.
 * @author abhimanyu
 *
 */
public interface Constants {
	public static final String CREATE_FUNCTION = "CREATE FUNCTION " ;
	public static final String CREATE_OR_REPLACE_FUNCTION = "CREATE OR REPLACE FUNCTION ";
	public static final String RETURNS = " RETURNS ";
	public static final String AS_$$ = " AS $$ ";
	public static final String LANGUAGE_$$ = "$$ LANGUAGE ";
	
	public static final String EXCEL = "excel";
	public static final String CSV = "csv";	
}
