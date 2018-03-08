package com.benbeehler.ignislang.exception;

public class IRuntimeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * standard level of inheritance
	 * does not inherit from any other custom-exceptions
	 */
	
	private String message;
	
	public IRuntimeException(String message) {
		super(message);
		this.message = "Runtime Error: " + message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public void printStackTrace() {
		System.err.println(getMessage());
	}
}
