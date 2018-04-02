package com.benbeehler.ignislang.syntax;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.exception.ISyntaxException;
import com.benbeehler.ignislang.objects.ICategory;
import com.benbeehler.ignislang.objects.ICondition;
import com.benbeehler.ignislang.objects.IForLoop;
import com.benbeehler.ignislang.objects.IFunction;
import com.benbeehler.ignislang.objects.IModule;
import com.benbeehler.ignislang.objects.IObject;
import com.benbeehler.ignislang.objects.IRoutine;
import com.benbeehler.ignislang.objects.IVariable;
import com.benbeehler.ignislang.objects.Scope;
import com.benbeehler.ignislang.runtime.ValueHandler;
import com.benbeehler.ignislang.utils.Util;

public class SyntaxHandler {

	public static final String COLON = "colon_tokenized_output";
	public static final String SEMICOLON = "csemi_tokenized_output";
	public static final String OPEN_OBJ_BRACKET = "{_tokenized_output";
	static final String OPEN_BRACKET = "(_tokenized_output";
	public static final String CLOSE_OBJ_BRACKET = "}_tokenized_output";
	static final String CLOSE_BRACKET = ")_tokenized_output";
	public static final String COMMA = ",_tokenized_output";
	public static final String OPEN_ARRAY_BRACKET = "[_tokenized_output";
	public static final String CLOSE_ARRAY_BRACKET = "]_tokenized_output";
	public static final String ENDLINE = "IGNIS_ENDLINE_CHARACTER_☓☓☓☓☓☓☓☓";
	
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
		line = line.replaceFirst("def", "");
		
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
		line = line.replaceFirst("def", "");
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
					if(!ValueHandler.isValid(val.toString(), variable.getType(), parser.getCurrent())) {
						throw new IRuntimeException("Invalid value for specified type");
					}
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
	
	public static IVariable parseVariable(String string, DynamicParser parser) throws IRuntimeException {
		string = string.trim();
		String props = SyntaxHandler.getUntil(string, "=");
		String[] split = props.split(" ");
		
		if(split.length == 2) {
			String vType = split[0];
			String name = split[1];
			
			if(ValueHandler.containsObject(vType)) {
				IVariable variable = new IVariable(name, Scope.GLOBAL);
				variable.setType(ValueHandler.getTypeByName(vType));
				
				String value = string.replace(props, "")
						.replaceFirst("=", "").trim();
				
				if(!value.equals("")) {
					/*if(!ValueHandler.isValid(value, variable.getType()))
						throw new IRuntimeException("Given variable value does not match assigned type.");*/
					//System.out.println(value);
					Object val = ValueHandler.getValue(value, parser).getValue();
					
					if(val != null) {	
						if(!ValueHandler.isValid(val.toString(), variable.getType(), parser.getBlock())) {
							throw new IRuntimeException("Invalid value for specified type");
						}
						
					}
					
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
			
			String value = string.replaceFirst(props, "")
					.replaceFirst("=", "").trim();
			
			Object val = ValueHandler.getValue(value, parser).getValue();
			
			if(!value.equals("")) {
				if(!ValueHandler.isValid(val.toString(), variable.getType(), parser.getBlock()))
					throw new IRuntimeException("Given variable value does not match assigned type.");
				variable.setValue(val);
			}
				
			return variable;
		} else {
			throw new ISyntaxException("Type must be specified in variable"
					+ " declaration", parser);
		}
	}
	
	/*public static boolean containsDefinitionKeyword(String string) {
		String[] array = new String[1000000];
		
		boolean isStr = false;
		int i = 0;
		for(String str : string.split("")) {
			i++;
			if(str.equals("\"")) {
				isStr = !isStr;
			}
			
			if(isStr) {
				array[i] = "☓";
			} else {
				array[i] = str;
			}
			
			i++;
		}
		
		String str = SyntaxHandler.convert(array);
		return str.contains("def") || str.contains("end");
	}*/
	
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
					if(!ValueHandler.isValid(val.toString(), variable.getType(), obj)) {
						throw new IRuntimeException("Invalid value for specified type 3");
					}
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
	
	public static IFunction parseFunctionCall(String line, DynamicParser parser) throws IRuntimeException {
		String[] split = line.split(" ");
		String fName = split[0];
		
		SyntaxBlock block = parser.getBlock().getSubblocks().stream()
				.filter(b -> b.getName().equals(fName))
				.findFirst().get();
		
		IFunction func = (IFunction) block;
		
		line = line.replaceFirst(fName, "").trim();
		String[] spl = line.split(SyntaxHandler.COMMA);
		int i = 0;
		
		//System.out.println(func.getParameters().size() + " : " + func.getName());
		
		if(func.getParameters().size() > 0) {
			for(String str : spl) {
				IVariable obj = ValueHandler.getValue(str, parser.getBlock());
				//System.out.println(obj.getValue() instanceof String);
				func.getParameters().get(i).setValue(obj.getValue());
				
				i++;
			}
		}
		
		func.execute();
		func.getVariables().clear();
		return func;
	}
	
	public static ICategory parseCategoryCall(String line, DynamicParser parser) throws IRuntimeException {
		String[] split = line.split(" ");
		String fName = split[0];
		
		ICategory block = parser.getBlock().getCategories().stream()
				.filter(b -> b.getName().equals(fName))
				.findFirst().get();
		
		line = line.replaceFirst(fName, "").trim();
		String[] spl = line.split(SyntaxHandler.COMMA);
		int i = 0;
		for(IFunction func : block.getFunctions()) {
			if(func.getParameters().size() > 0) {
				for(String str : spl) {
					IVariable obj = ValueHandler.getValue(str, parser.getBlock());
					func.getParameters().get(i).setValue(obj.getValue());
					
					i++;
				}
			}
			
			func.execute();
			func.getParameters().clear();
		}
		return block;
	}
	
	public static ICondition parseCondition(String line, Parser parser) {
		ICondition condition = new ICondition();
		line = line.replaceFirst("if", "").replaceFirst("def", "").trim();
		condition.setRawBoolean(line);
		condition.setId(Util.generateID());
		return condition;
	}
	
	public static ICondition parseECondition(String line, Parser parser) {
		ICondition condition = new ICondition();
		line = line.replaceFirst("else", "").replaceFirst("def", "").trim();
		ICondition cond = (ICondition) parser.getCurrent();
		condition.setRawBoolean(cond.getRawBoolean());
		condition.setNormal(false);
		condition.setId(Util.generateID());
		return condition;
	}
	
	public static IForLoop parseForLoop(String line, Parser parser) {
		IForLoop condition = new IForLoop();
		line = line.replaceFirst("for", "").replaceFirst("def", "").trim();
		condition.setRawBoolean(line);
		condition.setId(Util.generateID());
		return condition;
	}
	
	public static IRoutine parseRoutine(String line, Parser parser) {
		IRoutine routine = new IRoutine();
		routine.setExecute(true);
		routine.setRuntime(parser.getRuntime());
		line = line.replaceFirst("routine", "").replaceFirst("def", "").trim();
		routine.setId(Util.generateID());
		routine.setName("routine: " + routine.getId());
		return routine;
	}
	
	public static String replaceLast(String string, String toRep, String replacement) {
		return new StringBuilder()
				.append(new StringBuilder().append(string)
						.reverse().toString()
						.replaceFirst(toRep, replacement))
				.reverse().toString();
		
		//Yes, I am sane
	}
}
