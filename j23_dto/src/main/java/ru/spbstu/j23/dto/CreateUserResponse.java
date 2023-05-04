package ru.spbstu.j23.dto;

import java.io.Serializable;

public class CreateUserResponse implements Serializable {

	private String message;
	private int statusCode;
	private String userId;

	public CreateUserResponse(String message, int statusCode, String userId) {
		super();
		this.message = message;
		this.statusCode = statusCode;
		this.userId = userId;
	}
	
	
	public CreateUserResponse() {
	}



	public String getMessage() {
		return message;
	}

	public CreateUserResponse setMessage(String message) {
		this.message = message;
		return this;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public CreateUserResponse setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public CreateUserResponse setUserId(String userId) {
		this.userId = userId;
		return this;
	}

}
