/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.server.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class Stats {

	private final Connection conn;

	private final static String ITEM_KIND = "SELECT c.relkind"
			+ "  FROM pg_class c " + " WHERE c.oid = ? ";

	private final static String INDEX_STATS = "SELECT pg_size_pretty(pg_total_relation_size(relid)) as \"Total Index Size\", "
			+ "       idx_scan as \"Index Scans\", "
			+ "       idx_tup_read \"Rows Read\" "
			+ "  FROM pg_stat_user_indexes" + " WHERE indexrelid = ? ";

	private final static String SEQUENCE_STATS = "SELECT last_value as \"Last Value\", min_value as \"Minimum Value\", "
			+ "       max_value as \"Maximum Value\", cache_value as \"Cache Value\", "
			+ "       increment_by as \"Increment By\", is_cycled as \"Is Cycled\", "
			+ "       is_called as \"Is Called\", start_value as \"Start Value\" " + "  FROM ";

	private final static String TABLE_STATS = "SELECT pg_size_pretty(pg_total_relation_size(relid)) as \"Total Table Size\", "
			+ "       seq_scan as \"Full Table Scans\", "
			+ "       idx_scan as \"Index Scans\", "
			+ "       n_tup_ins \"Number of Rows Inserted\", "
			+ "       n_tup_upd + n_tup_hot_upd as \"Number of Rows Updated\", "
			+ "       n_tup_del as \"Number of Rows Deleted\", "
			+ "       n_live_tup as \"Number of Rows\" "
			+ "  FROM pg_stat_user_tables" + " WHERE relid = ? ";
	
	private final static String TABLE_STATS_IO = "SELECT heap_blks_read as \"Heap Bulk Read\", "
			+ "       heap_blks_hit as \"Heap Bulk Hit\", "
			+ "       idx_blks_read as \"Index Bulk Read\", "
			+ "       idx_blks_hit as \"Index Bulk Hit\", "
			+ "       toast_blks_read as \"Toast Bulk Read\", "
			+ "       toast_blks_hit as \"Toast Bulk Hit\", "
			+ "       tidx_blks_read as \"Toast Index Bulk Read\", "
			+ "       tidx_blks_hit as \"Toast Index Bulk Hit\" "
			+ "  FROM pg_statio_all_tables" + " WHERE relid = ? ";
	
	

	public Stats(Connection conn) {
		this.conn = conn;
	}

	public String getList(long item) throws SQLException {
		JSONArray result = new JSONArray();

		PreparedStatement stmt = conn.prepareStatement(ITEM_KIND);
		stmt.setLong(1, item);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			String kind = rs.getString(1);

			ResultSet rsStats = null;

			if (kind.equals("r")) {
				PreparedStatement stmtStats = conn
						.prepareStatement(TABLE_STATS);
				stmtStats.setLong(1, item);
				rsStats = stmtStats.executeQuery();
				
				 processResultSetToJSONArray( rsStats, result ) ;
				
				PreparedStatement stmtStats1 = conn
						.prepareStatement(TABLE_STATS_IO);
				stmtStats1.setLong(1, item);
				rsStats = stmtStats1.executeQuery();
				
				processResultSetToJSONArray( rsStats, result ) ;
				
			} else if (kind.equals("S")) {
				Database db = new Database(conn);
				String name = db.getItemFullName(item, ITEM_TYPE.SEQUENCE);

				String sql = SEQUENCE_STATS + name;
				PreparedStatement stmtStats = conn.prepareStatement(sql);
				rsStats = stmtStats.executeQuery();
				processResultSetToJSONArray( rsStats, result ) ;
			} else if (kind.equals("i")) {
				PreparedStatement stmtStats = conn
						.prepareStatement(INDEX_STATS);
				stmtStats.setLong(1, item);
				rsStats = stmtStats.executeQuery();
				processResultSetToJSONArray( rsStats, result ) ;
			}

			

		}

		return result.toString();
	}
	
	
	private static void processResultSetToJSONArray( ResultSet rsStats,JSONArray result ) throws SQLException{
		
		if (rsStats != null) {
			while (rsStats.next()) {
				for (int i = 1; i <= rsStats.getMetaData().getColumnCount(); i++) {
					JSONObject jsonMessage = new JSONObject();
					jsonMessage.put("name", rsStats.getMetaData()
							.getColumnName(i));

					int t = rsStats.getMetaData().getColumnType(i);
					int x = t;
					switch (rsStats.getMetaData().getColumnType(i)) {
					case -5: /* BIGINT */
					case 4: /* INT */
					case 5: /* SMALLINT */
						jsonMessage.put("value",
								Long.toString(rsStats.getLong(i)));
						break;
					case -7: /* BINARY */
					case 16: /* BOOLEAN */
						if (rsStats.getBoolean(i)) {
							jsonMessage.put("value", "true");
						} else {
							jsonMessage.put("value", "false");
						}
						break;
					case 12: /* VARCHAR */
						jsonMessage.put("value", rsStats.getString(i));
						break;
					}
					result.add(jsonMessage);
				}
			}
		}
		//return result;
	}
}
