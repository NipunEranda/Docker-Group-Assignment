package org.sliit.web.api.model;

public class ResetPassword {
	
	private String email;
	private String currentPassword;
	private String newPassword;
	
	public ResetPassword(String email, String currentPassword, String newPassword) {
		super();
		this.email = email;
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
	}

	public String getEmail() {
		return email;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
}


