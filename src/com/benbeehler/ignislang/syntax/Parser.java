package com.benbeehler.ignislang.syntax;

import java.util.ArrayList;
import java.util.List;

import com.benbeehler.ignislang.exception.ErrorHandler;
import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.objects.ICondition;
import com.benbeehler.ignislang.objects.IFunction;
import com.benbeehler.ignislang.objects.IModule;
import com.benbeehler.ignislang.objects.IObject;
import com.benbeehler.ignislang.runtime.IRuntime;

public class Parser {

	private List<String> lines = new ArrayList<>();
	private List<SyntaxBlock> bodies = new ArrayList<>();
	
	private List<SyntaxBlock> finished = 
			new ArrayList<>();
	
	private SyntaxBlock main = new SyntaxBlock();
	private SyntaxBlock current;
	private IFunction current_function = null;
	private IModule current_module = null;
	
	private List<IFunction> functions = 
			new ArrayList<>();
	private List<IModule> modules = 
			new ArrayList<>();
	private List<ICondition> conditions = 
			new ArrayList<>();
	
	private IRuntime runtime;
	
	private int line = 1;
	
	public Parser(List<String> list, IRuntime runtime) {
		this.lines = list;
		this.main.setName("main");
		this.runtime = runtime;
		main = new SyntaxBlock();
	}
	
	public Parser(SyntaxBlock body, IRuntime runtime) {
		this.lines = body.getLines();
		this.main = body;
		this.runtime = runtime;
		main = new SyntaxBlock();
	}
	
	public void start() throws IRuntimeException {
		this.current = main;
		bodies.add(main);
		
		for(String ln : lines) {
			String[] split = ln.split(" ");
			int len = split.length;
			
			if(split[0].trim().equals("ref")) {
				//new reference
				if(len >= 2) {
					String[] spl = ln.split(SyntaxHandler.COLON);
					String dec = spl[0];
					
					SyntaxBlock block = null;
						
					if(dec.contains("fn")) {
						IFunction func = SyntaxHandler.parseFunction(ln, this);
						functions.add(func);
						current_function = func;
							
						this.getCurrentModule().addFunction(func);
							
						block = func;
					} else if(dec.contains("module")) {
						//new module 
						IModule module = SyntaxHandler.parseModule(ln, this);
						modules.add(module);
						this.setCurrentModule(module);
						this.getRuntime().getNecessary().add(module);
						block = module;
					} else if(dec.contains("object")) {
						//new object
						dec = dec
								.replaceFirst("ref", "")
								.replaceFirst("object", "").trim();
							
						IObject obj = new IObject(dec);
						block = obj;
					} else if(dec.contains("if")) {
						ICondition obj = SyntaxHandler.parseCondition(ln, this);
						obj.setName("if-statement:" + obj.getId());
						block = obj;
					}
						
					if(block == null) {
						throw new IRuntimeException(ErrorHandler
								.constructSyntaxError("Unknown reference type", this));
					}
					
					current.addLine("CALL_BLOCK " + block.getId());
						
					current.addBlock(block);
					
					current = block;
					bodies.add(0, block);
				}
			} else if(ln.trim().equals("end")) {
				finished.add(current);
				bodies.remove(0);
				current = bodies.get(0);
			} else {
				current.addLine(ln);
			}
			
			line++;
		}
		
		finished.add(main);
	}
	
	public List<SyntaxBlock> getFinished() {
		return finished;
	}
	
	public SyntaxBlock getCurrent() {
		return this.current;
	}
	
	public SyntaxBlock getMain() {
		return this.main;
	}
	
	public void setMain(SyntaxBlock block) {
		this.main = block;
	}
	
	public int getLine() {
		return this.line;
	}

	public IRuntime getRuntime() {
		return runtime;
	}

	public void setRuntime(IRuntime runtime) {
		this.runtime = runtime;
	}

	public List<IFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<IFunction> functions) {
		this.functions = functions;
	}

	public IModule getCurrentModule() {
		return current_module;
	}

	public void setCurrentModule(IModule current_module) {
		this.current_module = current_module;
	}

	public IFunction getCurrentFunction() {
		return current_function;
	}

	public void setCurrentFunction(IFunction current_function) {
		this.current_function = current_function;
	}
}
