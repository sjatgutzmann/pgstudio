/*
 * PostgreSQL Studio
 */
package com.openscg.pgstudio.client.models;

import com.google.gwt.view.client.ProvidesKey;
import com.openscg.pgstudio.client.PgStudio.ITEM_TYPE;

public class PolicyInfo implements ModelInfo, Comparable<PolicyInfo> {

	public static final ProvidesKey<PolicyInfo> KEY_PROVIDER = new ProvidesKey<PolicyInfo>() {
		public Object getKey(PolicyInfo IDX) {
			return IDX == null ? null : IDX.getId();
		}
	};

	private final long id;
	private final int schema;

	private String name;
	private String command;
	private String roles;
	private String using;
	private String withCheck;

	public PolicyInfo(int schema, long id, String name) {
		this.schema = schema;
		this.id = id;
		this.name = name;
		;
	}

	public int compareTo(PolicyInfo o) {
		if (o == null || o.name == null)
			return -1;

		if (o.name.compareTo(name) != 0)
			return -1;

		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PolicyInfo) {
			return id == ((PolicyInfo) o).id;
		}
		return false;
	}

	public long getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		if (command.equals("*"))
			return "ALL";

		if (command.equals("r"))
			return "SELECT";

		if (command.equals("w"))
			return "UPDATE";

		if (command.equals("a"))
			return "INSERT";

		if (command.equals("d"))
			return "DELETE";

		return "ALL";
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getUsing() {
		return using;
	}

	public void setUsing(String using) {
		this.using = using;
	}

	public String getWithCheck() {
		return withCheck;
	}

	public void setWithCheck(String withCheck) {
		this.withCheck = withCheck;
	}

	@Override
	public String getFullName() {
		return name;
	}

	@Override
	public ITEM_TYPE getItemType() {
		return null;
	}

	@Override
	public String getComment() {
		return "";
	}

	@Override
	public int getSchema() {
		return schema;
	}

}
