package com.benbeehler.ignislang.objects;

import java.util.ArrayList;
import java.util.List;

public class ICategory {

	private int pLength;
	private String name;
	private List<IFunction> functions = 
			new ArrayList<>();

	public ICategory(String name, int len) {
		this.name = name;
		this.pLength = len;
	}
	
	public int getpLength() {
		return pLength;
	}

	public void setpLength(int pLength) {
		this.pLength = pLength;
	}

	public List<IFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<IFunction> functions) {
		this.functions = functions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
