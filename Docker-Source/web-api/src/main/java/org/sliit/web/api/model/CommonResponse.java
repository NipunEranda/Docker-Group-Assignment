package org.sliit.web.api.model;

public class CommonResponse {

	private String status;
	private String message;
	private String data;

	public String getCode() {
		return status;
	}

	public void setCode(String code) {
		this.status = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setResponse(String code, String message) {
		this.status = code;
		this.message = message;
	}
	
	public void defaultErrorMessage() {
		this.status = "500";
		this.message = "something went wrong";
	}
	
	public void defaultSuccessMessage(String data) {
		this.status = "200";
		this.message = "success";
		this.data = data;
	}
	
	public void defaultNotFoundMessage() {
		this.status = "404";
		this.message = "NOT FOUND";
	}
	
	public void defaultExceptionMessage(Exception e) {
		this.status = "500";
		this.message = e.getMessage();
		e.printStackTrace();
		System.out.println(e.getMessage());
	}

	public void defaultAccessDeniedMessage() {
		this.status = "400";
		this.message = "Access Denied";
	}
	
	public void defaultEmptyMessage() {
		this.status = "500";
		this.message = "Object is empty";
	}
	
}
