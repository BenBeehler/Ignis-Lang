package com.benbeehler.ignislang.objects;

import java.util.ArrayList;
import java.util.List;

import com.benbeehler.ignislang.syntax.SyntaxBlock;

public class IVariable {

	private Object value;
	private Scope scope;
	private String name;
	private SyntaxBlock block;
	private IObject type;
	private List<IVariable> subVars = new ArrayList<>();

	public IVariable(String name, Scope scope) {
		this.name = name;
		this.scope = scope;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SyntaxBlock getBlock() {
		return block;
	}

	public void setBlock(SyntaxBlock block) {
		this.block = block;
	}

	public IObject getType() {
		return type;
	}

	public void setType(IObject type) {
		this.type = type;
	}

	public List<IVariable> getSubVars() {
		return subVars;
	}

	public void setSubVars(List<IVariable> subVars) {
		this.subVars = subVars;
	}
	
}
