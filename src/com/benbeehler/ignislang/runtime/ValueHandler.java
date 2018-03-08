package com.benbeehler.ignislang.runtime;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.objects.IFunction;
import com.benbeehler.ignislang.objects.IObject;
import com.benbeehler.ignislang.objects.IVariable;
import com.benbeehler.ignislang.objects.Scope;
import com.benbeehler.ignislang.syntax.SyntaxBlock;
import com.benbeehler.ignislang.syntax.SyntaxHandler;

public class ValueHandler {

	public static IObject STRING = new IObject("string");
	public static IObject DECIMAL = new IObject("decimal");
	public static IObject BOOLEAN = new IObject("bool");
	public static IObject INTEGER = new IObject("int");
	public static IObject TUPLE = new IObject("tuple");
	public static IObject HASH = new IObject("hash");
	
	public static final List<IObject> objects = 
			new ArrayList<>();
	public static final List<IFunction> functions = 
			new ArrayList<>();
	
	public static void init() throws IRuntimeException {
		objects.add(STRING);
		objects.add(DECIMAL);
		objects.add(BOOLEAN);
		objects.add(INTEGER);
		objects.add(TUPLE);
		objects.add(HASH);
		
		initNative();
	}
	
	public static void initNative() throws IRuntimeException {
		SyntaxBlock block = new SyntaxBlock();
		IFunction function = new IFunction(block);
		function.setName("IO.Puts");
		function.setNativ(true);
		function.addParameter(new IVariable("param_1", Scope.PRIVATE));
		function.addParameter(new IVariable("param_2", Scope.PRIVATE));
		function.setRunnable(() -> {
			if(function.getParameters().size() == 2) {
				Object out = function.getParameters().get(0).getValue();
				Object value = function.getParameters().get(1).getValue();
				
				if(out instanceof PrintWriter) {
					@SuppressWarnings("resource")
					PrintWriter stream = (PrintWriter) out;
					stream.print(value.toString());
					stream.flush();
				} else {
					try {
						throw new IRuntimeException("Invalid Value Type for Function");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count for Function");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(function);
		
		SyntaxBlock pBlock = new SyntaxBlock();
		IFunction println = new IFunction(pBlock);
		println.setName("IO.Println");
		println.setNativ(true);
		println.addParameter(new IVariable("param_1", Scope.PRIVATE));
		println.setRunnable(() -> {
			if(println.getParameters().size() == 1) {
				Object value = println.getParameters().get(0).getValue();
				System.out.println(value);
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count for println " + println.getParameters());
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println);
	}
	
	public static boolean containsObject(String name) {
		return objects.stream().filter(obj -> obj.getName()
				.equals(name)).findAny().isPresent();
	}
	
	public static IObject getType(String name) {
		return objects.stream().filter(obj -> obj.getName()
				.equals(name)).findAny().get();
	}
	
	public static String calc(String in, List<IVariable> variables) throws ScriptException {
		for(IVariable v : variables) {
			if(in.contains(v.getName())) {
				in = in.replace(v.getName(), v.getValue().toString());
			}
		}
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		Object result = engine.eval(in);
		
		return result.toString();
	}
	
	public static String calc(String in) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		Object result = engine.eval(in);
		
		return result.toString();
	}
	
	public static boolean isInteger(String str, List<IVariable> var) {
		try {
			String s = String.valueOf(Integer.parseInt(calc(str, var)));
			if(s.contains(".")) return false;
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isDecimal(String str, List<IVariable> var) {
		try {
			Double.parseDouble(calc(str, var));
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isInteger(String str) {
		try {
			String s = String.valueOf(Integer.parseInt(calc(str)));
			if(s.contains(".")) return false;
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isDecimal(String str) {
		try {
			Double.parseDouble(calc(str));
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isString(String str) {
		str = str.trim();
		
		if(str.startsWith("\"")) 
			if(str.endsWith("\"")) return true;
		
		return false;
	}
	
	public static boolean isBoolean(String str) {
		str = str.trim(); 
		if(str.equals("true") 
				|| str.equals("false")) return true;
		return false;
	}
	
	public static int getInteger(String str, List<IVariable> var) throws IRuntimeException {
		try {
			return Integer.parseInt(calc(str, var));
		} catch (NumberFormatException | ScriptException e) {
			throw new IRuntimeException("Failed to parse integer, invalid input");
		}
	}
	
	public static double getDecimal(String str) throws IRuntimeException {
		try {
			return Double.parseDouble(calc(str));
		} catch (NumberFormatException | ScriptException e) {
			throw new IRuntimeException("Failed to parse double, invalid input");
		}
	}
	
	public static int getInteger(String str) throws IRuntimeException {
		try {
			return Integer.parseInt(calc(str));
		} catch (NumberFormatException | ScriptException e) {
			throw new IRuntimeException("Failed to parse integer, invalid input");
		}
	}
	
	public static double getDecimal(String str, List<IVariable> var) throws IRuntimeException {
		try {
			return Double.parseDouble(calc(str, var));
		} catch (NumberFormatException | ScriptException e) {
			throw new IRuntimeException("Failed to parse double, invalid input");
		}
	}
	
	public static boolean getBoolean(String str) {
		str = str.trim();
		if(str.trim().equals("true")) return true;
		return false;
	}
	
	public static String getString(String str) {
		str = str.replaceFirst("\"", "");
		str = SyntaxHandler.reverse(SyntaxHandler
				.reverse(str.replaceFirst("\"", "")));
		return str;
	}
	
	public static IVariable getValue(String str) throws IRuntimeException {
		IVariable variable = new IVariable("var", Scope.PRIVATE);
		variable.setValue(new Object());
		
		if(isInteger(str)) {
			variable.setValue(getInteger(str));
		} else if(isDecimal(str)) {
			variable.setValue(getDecimal(str));
		} else if(isBoolean(str)) {
			variable.setValue(getBoolean(str));
		} else if(isString(str)) {
			variable.setValue(getString(str));
		}
		
		return variable;
	}
	
	public static IVariable getValue(String str, SyntaxBlock block) throws IRuntimeException {
		IVariable variable = new IVariable("var", Scope.PRIVATE);
		variable.setValue(new Object());
		
		if(isInteger(str, block.getVariables())) {
			variable.setValue(getInteger(str, block.getVariables()));
		} else if(isDecimal(str, block.getVariables())) {
			variable.setValue(getDecimal(str, block.getVariables()));
		} else if(isBoolean(str)) {
			variable.setValue(getBoolean(str));
		} else if(isString(str)) {
			variable.setValue(getString(str));
		} else if(block.getVariables().stream()
				.filter(b -> b.getName().equals(str)).findAny().isPresent()) {
			IVariable b = block.getVariables().stream()
					.filter(bl -> bl.getName().equals(str)).findAny().get();
			variable = b;
		} else if(str.startsWith("new")) {
			String inst = str.replaceFirst("new", "").trim();
			
			if(block.getSubblocks().stream()
					.filter(b -> b.getName().equals(inst)).findAny().isPresent()) {
				SyntaxBlock bl = block.getSubblocks().stream()
						.filter(b -> b.getName().equals(inst)).findAny().get();
				if(bl instanceof IObject) {
					IObject obje = (IObject) bl;
					variable = instantiate(obje, variable);
				}
			} else {
				throw new IRuntimeException("Type specified does not exist.");
			}
		}
		
		return variable;
	}
	
	private static IVariable instantiate(IObject obj, IVariable variable) {
		for(IVariable var : obj.getVariables()) {
			variable.getSubVars().add(var);
		}
		
		return variable;
	}
	
	public static boolean isValid(String pVal, IObject type, SyntaxBlock block) throws IRuntimeException {
		if(type == ValueHandler.BOOLEAN) {
			return isBoolean(getValue(pVal).toString());
		} else if(type == ValueHandler.DECIMAL) {
			return isDecimal(getValue(pVal, block).toString(), block.getVariables());
		} else if(type == ValueHandler.INTEGER) {
			return isInteger(getValue(pVal, block).toString(), block.getVariables());
		} else if(type == ValueHandler.STRING) {
			return isString(getValue(pVal).toString());
		} else {
			return false;
		}
	}
}
