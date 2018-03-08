package com.benbeehler.ignislang.objects;

import java.util.ArrayList;
import java.util.List;

import com.benbeehler.ignislang.syntax.SyntaxBlock;

public class IModule extends SyntaxBlock {

	private List<IFunction> functions = new ArrayList<>();
	private List<IVariable> variables = new ArrayList<>();
	
	public IModule() {
	}

	public List<IFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<IFunction> functions) {
		this.functions = functions;
	}

	public List<IVariable> getVariables() {
		return variables;
	}

	public void setVariables(List<IVariable> variables) {
		this.variables = variables;
	}
	
	public void addFunction(IFunction function) {
		List<IFunction> list = getFunctions();
		list.add(function);
		setFunctions(list);
	}
	
	public void addVariable(IVariable variable) {
		List<IVariable> list = getVariables();
		list.add(variable);
		setVariables(list);
	}
	
	@Override
	public boolean isExecute() {
		return true;
	}
}
