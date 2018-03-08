package com.benbeehler.ignislang.objects;

import java.util.ArrayList;
import java.util.List;

import com.benbeehler.ignislang.syntax.SyntaxBlock;

public class IObject extends SyntaxBlock {

	private List<IVariable> variables = new ArrayList<>();
	private String name = "";
	private Object value;
	
	public IObject(String name) {
		this.setName(name);
	}
	
	public List<IVariable> getVariables() {
		return variables;
	}

	public void addVariable(IVariable var) {
		this.variables.add(var);
	}
	
	public void setVariables(List<IVariable> variables) {
		this.variables = variables;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public boolean isExecute() {
		return true;
	}
}
