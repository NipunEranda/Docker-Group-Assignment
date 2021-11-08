package org.sliit.web.api.model;

import org.json.JSONObject;

public class Role {

	private long roleId;
	private String roleName;
	private String roleDesc;
	
	public Role(long roleId, String roleName, String roleDesc) {
		super();
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleDesc = roleDesc;
	}

	public long getRoleId() {
		return roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}
	
	public JSONObject createRoleJsonObject() {
		JSONObject role = new JSONObject();
		role.put("roleId", this.roleId);
		role.put("roleName", this.roleName);
		role.put("roleDesc", this.roleDesc);
		return role;
	}
	
}
