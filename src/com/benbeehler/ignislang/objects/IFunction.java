package com.benbeehler.ignislang.objects;

import java.util.ArrayList;
import java.util.List;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.syntax.DynamicParser;
import com.benbeehler.ignislang.syntax.SyntaxBlock;

public class IFunction extends SyntaxBlock {

	private boolean nativ = false;
	private Runnable runnable = () -> {};
	private Object output;
	private List<IVariable> variables = new ArrayList<>();
	private List<IVariable> parameters = new ArrayList<>();
	private SyntaxBlock block;
	private Object returnValue;
	
	public IFunction(SyntaxBlock body) {
		super();
		this.setName(body.getName());
		this.block = body;
		this.setExecute(body.isExecute());
	}
	
	@Override
	public void execute() {
		/*
		 * Concurrently execute method call
		 */
		if(isNativ()) {
			runnable.run(); 
		} else {
			try {
				startParser();
			} catch (IRuntimeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startParser() throws IRuntimeException {
		this.getVariables().addAll(this.getParameters());
		
		if(this.getMaster() != null) {
			this.getVariables().addAll(this.getMaster().getVariables());
		}
		
		DynamicParser parser = new DynamicParser(this, this.getRuntime());
		parser.start();
	}

	public boolean isNativ() {
		return nativ;
	}

	public void setNativ(boolean nativ) {
		this.nativ = nativ;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	public Object getOutput() {
		return output;
	}

	public void setOutput(Object output) {
		this.output = output;
	}

	public List<IVariable> getVariables() {
		return variables;
	}

	public void setVariables(List<IVariable> variables) {
		this.variables = variables;
	}

	public SyntaxBlock getBlock() {
		return block;
	}

	public void setBlock(SyntaxBlock block) {
		this.block = block;
	}

	public List<IVariable> getParameters() {
		return parameters;
	}
	
	public void addParameter(IVariable var) {
		this.parameters.add(var);
	}

	public void setParameters(List<IVariable> parameters) {
		this.parameters = parameters;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}
}
