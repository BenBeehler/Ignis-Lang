package com.benbeehler.ignislang.objects;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.runtime.ValueHandler;
import com.benbeehler.ignislang.syntax.DynamicParser;

public class IForLoop extends ICondition {
	
	private String rawBoolean = "";

	public String getRawBoolean() {
		return rawBoolean;
	}

	public void setRawBoolean(String rawBoolean) {
		this.rawBoolean = rawBoolean;
	}
	
	@Override
	public boolean isExecute() {
		return ValueHandler.getBoolean(rawBoolean);
	}
	
	@Override
	public void execute() {
		while(isExecute()) {
			try {
				startParser();
			} catch (IRuntimeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startParser() throws IRuntimeException {
		if(this.getMaster() != null) {
			this.getVariables().addAll(this.getMaster().getVariables());
		}
		
		DynamicParser parser = new DynamicParser(this, this.getRuntime());
		parser.start();
	}
}
