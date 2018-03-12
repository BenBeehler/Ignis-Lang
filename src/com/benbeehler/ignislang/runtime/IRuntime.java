package com.benbeehler.ignislang.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.objects.IModule;
import com.benbeehler.ignislang.objects.IObject;
import com.benbeehler.ignislang.syntax.DynamicParser;
import com.benbeehler.ignislang.syntax.Parser;
import com.benbeehler.ignislang.syntax.SyntaxBlock;
import com.benbeehler.ignislang.syntax.SyntaxHandler;

public class IRuntime {

	/*
	 * Runtime instance
	 */
	
	private File file;
	private SyntaxBlock main;
	private List<SyntaxBlock> imported = new ArrayList<>();
	private List<IModule> necessary = new ArrayList<>();
	
	public IRuntime(File file) {
		this.file = file;
	}
	
	public void start() throws IRuntimeException {
		main = SyntaxBlock.importFileIntoBody(this.file);
		
		Parser parser = new Parser(main, this);
		parser.start();
		
		/*for(IFunction func : parser.getFunctions()) {
			if(func.isExecute()) {
				func.execute();
			}
		}*/
		
		main = parser.getMain();
		main.getSubblocks().addAll(ValueHandler.functions);
		
		developObjectProps();
	}
	
	public void developObjectProps() throws IRuntimeException {
		for(SyntaxBlock block : main.getSubblocks()) {
			if(block instanceof IObject) {
				IObject obj = (IObject) block;
				
				for(String line : obj.getLines()) {
					obj.addVariable(SyntaxHandler.parseVariable(line, obj));
				}
			} else {
			}
		}
		
		DynamicParser parser = new DynamicParser(main, this);
		parser.start();
	}

	public List<SyntaxBlock> getImported() {
		return imported;
	}

	public List<IModule> getNecessary() {
		return necessary;
	}

	public void setNecessary(List<IModule> necessary) {
		this.necessary = necessary;
	}
}
