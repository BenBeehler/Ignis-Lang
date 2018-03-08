package com.benbeehler.ignislang.exception;

import com.benbeehler.ignislang.objects.IObject;
import com.benbeehler.ignislang.syntax.Parser;

public class ISyntaxException extends IRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public ISyntaxException(String message, Parser parser) {
		super(message);
		this.message = "Syntax Error: " + message + " <" + parser.getMain().getName() + ":" +
					parser.getCurrent().getName() + ":" + parser.getLine() + ">";
	}
	
	public ISyntaxException(String message, IObject object) {
		super(message);
		this.message = "Syntax Error: " + message + " <" + 
					object.getName() + ">";
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
