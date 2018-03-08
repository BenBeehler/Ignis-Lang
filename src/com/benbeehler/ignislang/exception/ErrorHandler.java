package com.benbeehler.ignislang.exception;

import java.io.File;

import com.benbeehler.ignislang.syntax.Parser;

public class ErrorHandler {

	public static String constructUnknownFileError(File file) {
		return "could not find file <" + file.getName() + ">";
	}
	
	public static String constructSyntaxError(String string, Parser parser) {
		return string + " <" + parser.getMain().getName() + ":" + 
				parser.getCurrent().getName() + ":" + parser.getLine() + ">";
	}
}
