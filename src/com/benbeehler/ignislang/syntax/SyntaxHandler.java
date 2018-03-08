package com.benbeehler.ignislang.syntax;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.exception.ISyntaxException;
import com.benbeehler.ignislang.objects.IFunction;
import com.benbeehler.ignislang.objects.IModule;
import com.benbeehler.ignislang.objects.IObject;
import com.benbeehler.ignislang.objects.IVariable;
import com.benbeehler.ignislang.objects.Scope;
import com.benbeehler.ignislang.runtime.ValueHandler;

public class SyntaxHandler {

	static String COLON = "colon_tokenized_output";
	static String OPEN_OBJ_BRACKET = "{_tokenized_output";
	static String OPEN_BRACKET = "(_tokenized_output";
	static String CLOSE_OBJ_BRACKET = "}_tokenized_output";
	static String CLOSE_BRACKET = ")_tokenized_output";
	static String COMMA = ",_tokenized_output";
	
	public static String reverse(String str) {
		return new StringBuilder(str).reverse().toString();
	}
	
	public static String convert(String[] str) {
		StringBuilder builder = new StringBuilder();
		
		for(String string : str) { 
			builder.append(string);
		}
		
		return builder.toString();
	}
	
	public static String getUntil(String string, String until) {
		return string.split(until)[0];
	}
	
	public static IFunction parseFunction(String line, Parser parser) throws IRuntimeException {
		line = line.trim();
		line = line.replaceFirst("ref", "");
		
		String[] spl = line.split(COLON);
		
		if(spl.length == 2) {
			SyntaxBlock fblock = new SyntaxBlock();
			String props = spl[0];
			String parameters = spl[1].trim();
			props = props.replaceFirst("fn", "");
			
			if(props.contains("auto")) {
				props = props.replace("auto", "").trim();
				fblock.setExecute(true);
			} else {
				fblock.setExecute(false);
			}
			
			if(parser.getCurrentModule() == null) 
				throw new IRuntimeException("Function must belong to a module!");
			
			fblock.setName(parser.getCurrentModule().getName() + "." + props.trim());
			IFunction func = new IFunction(fblock);
			
			if(!parameters.equals("void")) {
				String[] split = parameters.split(SyntaxHandler.COMMA);
				
				for(String param : split) {
					param = param.trim();
					String[] psplit = param.split(" ");
					
					if(psplit.length == 2) {
						IVariable var = SyntaxHandler.parseVariable(param, parser);
						
						func.addParameter(var);
					} else {
						throw new ISyntaxException("Function parameters are malformed", parser);
					}
				}
			}
			
			return func;
		} else {
			throw new ISyntaxException("Invalid function declaration", parser);
		}
	}
	
	public static IModule parseModule(String line, Parser parser) {
		line = line.trim();
		line = line.replaceFirst("ref", "");
		line = line.replaceFirst("module", "");
		
		IModule module = new IModule();
		module.setName(line.trim());
		return module;
	}
	
	public static IVariable parseVariable(String string, Parser parser) throws IRuntimeException {
		string = string.trim();
		String props = SyntaxHandler.getUntil(string, "=");
		String[] split = props.split(" ");
		
		if(split.length == 2) {
			String vType = split[0];
			String name = split[1];
			
			if(ValueHandler.containsObject(vType)) {
				IVariable variable = new IVariable(name, Scope.GLOBAL);
				variable.setType(ValueHandler.getType(vType));
				
				String value = string.replace(props, "")
						.replaceFirst("=", "").trim();
				
				if(!value.equals("")) {
					/*if(!ValueHandler.isValid(value, variable.getType()))
						throw new IRuntimeException("Given variable value does not match assigned type.");*/
					Object val = ValueHandler.getValue(value).getValue();
					variable.setValue(val);
				}
				
				return variable;
			} else {
				throw new ISyntaxException("Unknown type in variable "
						+ "declaration", parser);
			}
		} else {
			throw new ISyntaxException("Type must be specified in variable"
					+ " declaration", parser);
		}
	}
	
	public static IVariable remapVariable(String string, DynamicParser parser) throws IRuntimeException {
		//string name = "Ben"
		string = string.trim();
		String props = SyntaxHandler.getUntil(string, "=");
		String[] split = props.split(" ");
		
		if(split.length == 1) {
			String name = split[0];
			
			IVariable variable = parser.getBlock().getVariables()
					.stream().filter(obj -> obj.getName()
							.equalsIgnoreCase(name))
					.findAny().get();
				
			String value = string.replace(props, "")
					.replaceFirst("=", "").trim();
				
			if(!value.equals("")) {
				/*if(!ValueHandler.isValid(value, variable.getType()))
					throw new IRuntimeException("Given variable value does not match assigned type.");*/
				Object val = ValueHandler.getValue(value).getValue();
				variable.setValue(val);
			}
				
			return variable;
		} else {
			throw new ISyntaxException("Type must be specified in variable"
					+ " declaration", parser);
		}
	}
	
	public static IVariable parseVariable(String string, IObject obj) throws IRuntimeException {
		string = string.trim();
		String props = SyntaxHandler.getUntil(string, "=");
		String[] split = props.split(" ");
		
		if(split.length == 2) {
			String vType = split[0];
			String name = split[1];
			
			if(ValueHandler.containsObject(vType)) {
				IVariable variable = new IVariable(name, Scope.GLOBAL);
				variable.setType(ValueHandler.getType(vType));
				
				String value = string.replace(props, "")
						.replaceFirst("=", "").trim();
				
				if(!value.equals("")) {
					Object val = ValueHandler.getValue(value).getValue();
					variable.setValue(val);
				}
				
				return variable;
			} else {
				throw new ISyntaxException("Unknown type in variable "
						+ "declaration", obj);
			}
		} else {
			throw new ISyntaxException("Type must be specified in variable"
					+ " declaration", obj);
		}
	}
	
	public static void parseFunctionCall(String line, DynamicParser parser) throws IRuntimeException {
		String[] split = line.split(" ");
		String fName = split[0];
		
		SyntaxBlock block = parser.getBlock().getSubblocks().stream()
				.filter(b -> b.getName().equals(fName))
				.findFirst().get();
		
		IFunction func = (IFunction) block;
		
		line = line.replaceFirst(fName, "").trim();
		String[] spl = line.split(SyntaxHandler.COMMA);
		int i = 0;
		for(String str : spl) {
			IVariable obj = ValueHandler.getValue(str, parser.getBlock());
			func.getParameters().clear();
			func.getParameters().add(i, obj);
			
			i++;
		}
		
		func.execute();
	}
}
