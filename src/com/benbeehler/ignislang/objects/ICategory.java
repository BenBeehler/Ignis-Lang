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
	
	public void call(List<IVariable> list) {
		for(IFunction func : getFunctions()) {
			if(func.getParameters().size() > 0) {
				int i = 0;
				for(IVariable var : list) {
					func.getParameters().get(i).setValue(var.getValue());
					i++;
				}
			}
			
			func.execute();
			func.getParameters().clear();
		}
	}
	
	public void callObjects(List<Object> list) {
		for(IFunction func : getFunctions()) {
			if(func.getParameters().size() > 0) {
				int i = 0;
				for(Object var : list) {
					func.getParameters().get(i).setValue(var);
					i++;
				}
			}
			
			func.execute();
			//func.getVariables().forEach(e->System.out.println(e.getName() + " : " + e.getValue()));
			func.getVariables().clear();
		}
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
