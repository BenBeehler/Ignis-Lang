package com.benbeehler.ignislang.syntax;

public class Tokenizer {

	private String[] input;
	private String[] output;
	
	public Tokenizer(String[] input) {
		this.setInput(input);
		output = new String[input.length];
		start();
	}
	
	public void start() {
		for(int x = 0; x < input.length; x++) {
			String in = input[x];
			String[] array = in.split("");
			
			boolean string = false;
			
			for(int i = 0; i < array.length; i++) {
				String ch = array[i];
				
				if(!string) {
					if(ch.equals(":")) {
						array[i] = SyntaxHandler.COLON;
					} else if(ch.equals(";")) {
						array[i] = SyntaxHandler.SEMICOLON;
					} else if(ch.equals("(")) {
						array[i] = SyntaxHandler.OPEN_BRACKET;
					} else if(ch.equals(")")) {
						array[i] = SyntaxHandler.CLOSE_BRACKET;
					} else if(ch.equals("{")) {
						array[i] = SyntaxHandler.OPEN_OBJ_BRACKET;
					} else if(ch.equals("}")) {
						array[i] = SyntaxHandler.CLOSE_OBJ_BRACKET;
					} else if(ch.equals("[")) {
						array[i] = SyntaxHandler.OPEN_ARRAY_BRACKET;
					} else if(ch.equals("]")) {
						array[i] = SyntaxHandler.CLOSE_ARRAY_BRACKET;
					} else if(ch.equals(",")) {
						array[i] = SyntaxHandler.COMMA;
					}
				}
				
				if(ch.equals("\"") || ch.equals("\'")) {
					string = !string;
				}
			}
			
			output[x] = SyntaxHandler.convert(array);
		}
	}

	public String[] getInput() {
		return input;
	}

	public void setInput(String[] input) {
		this.input = input;
	}

	public String[] getOutput() {
		return output;
	}
}
