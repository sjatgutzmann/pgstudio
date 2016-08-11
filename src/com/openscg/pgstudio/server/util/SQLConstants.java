package com.openscg.pgstudio.server.util;

public interface SQLConstants {
	
	String SELECT_FTS_CONFIG_PARSERS_LIST = "SELECT n.nspname || '.' || p.prsname as name "
				+ " FROM pg_catalog.pg_ts_parser p "
				+ " LEFT JOIN pg_catalog.pg_namespace n ON n.oid = p.prsnamespace"
				+ " WHERE pg_catalog.pg_ts_parser_is_visible(p.oid)"
				+ " ORDER BY 1";
	
	String SELECT_FTS_CONFIG_TEMPLATES_LIST = "SELECT   n.nspname || '.' ||  c.cfgname as name"
			+ " FROM pg_catalog.pg_ts_config c  LEFT JOIN pg_catalog.pg_namespace n "
			+ " ON n.oid = c.cfgnamespace "
			+ " WHERE pg_catalog.pg_ts_config_is_visible(c.oid) "
			+ " ORDER BY c.cfgname";
	
	String SELECT_FTS_CONFIG_BY_SCHEMA_NAME = "SELECT c.cfgname as name,  pg_catalog.obj_description(c.oid, 'pg_ts_config') as comment"
			+ " FROM pg_catalog.pg_ts_config c  LEFT JOIN pg_catalog.pg_namespace n "
			+ " ON n.oid = c.cfgnamespace "
			+ " WHERE pg_catalog.pg_ts_config_is_visible(c.oid) "
			+ " and c.cfgnamespace != 11 "
			+ " and n.nspname = ':schema'";
	
	String SELECT_FTS_TOKEN_DICT_LIST_BY_TEMPLATE = "SELECT "
			+ "( SELECT t.alias FROM "
			+ " pg_catalog.ts_token_type(c.cfgparser) AS t "
			+ " WHERE t.tokid = m.maptokentype ) AS token, "
			+ " pg_catalog.btrim("
			+ " ARRAY( SELECT mm.mapdict::pg_catalog.regdictionary "
			+ " FROM pg_catalog.pg_ts_config_map AS mm "
			+ " WHERE mm.mapcfg = m.mapcfg AND mm.maptokentype = m.maptokentype "
			+ " ORDER BY mapcfg, maptokentype, mapseqno "
			+ " ) :: pg_catalog.text , "
			+ "  '{}') AS Dictionaries , t.description as comment"
			+ "	FROM pg_catalog.pg_ts_config AS c, pg_catalog.pg_ts_config_map AS m, ts_token_type('default') t"
			+ "	WHERE c.oid = (select oid from pg_ts_config where cfgname= ':cfgTemplate') AND m.mapcfg = c.oid AND m.maptokentype=t.tokid"
			+ "	GROUP BY m.mapcfg, m.maptokentype, c.cfgparser , t.description	ORDER BY 1";
	
	String SELECT_FTS_TOKEN_DESCRIPTION_LIST = "SELECT (t.alias  || ' - ' || description) as name FROM pg_catalog.ts_token_type('default') AS t ";
	
	String SELECT_FTS_DICT_NAMES = "select dictname as name from pg_ts_dict order by dictname";
	
	String ADD_FTS_CONFIG_COMMENT = "COMMENT ON TEXT SEARCH CONFIGURATION :configName IS ':comments';";
	
	String ADD_FTS_CONFIG="CREATE TEXT SEARCH CONFIGURATION :configName (PARSER = 'default')";
	
	String ADD_FTS_MAPPING_FOR_CONFIG = "ALTER TEXT SEARCH CONFIGURATION :configName ADD MAPPING FOR :token WITH :dict";
	
	String DROP_FTS_CONFIG = "DROP TEXT SEARCH CONFIGURATION IF EXISTS "; 
	
	String DROP_FTS_MAPPING = "alter text search configuration :configName drop mapping for :tokenType";
	
	String ALTER_FTS_MAPPING = "ALTER TEXT SEARCH CONFIGURATION :configName ALTER MAPPING for :tokenType REPLACE :oldDict WITH :newDict";
	
	String ALTER_FTS_CONFIG_COMMENT = "COMMENT ON TEXT SEARCH CONFIGURATION :configName IS ':comment'";

	

}
