package com.benbeehler.ignislang.objects;

import com.benbeehler.ignislang.runtime.ValueHandler;
import com.benbeehler.ignislang.syntax.SyntaxBlock;

public class ICondition extends SyntaxBlock {
	
	private String rawBoolean = "";

	public String getRawBoolean() {
		return rawBoolean;
	}

	public void setRawBoolean(String rawBoolean) {
		this.rawBoolean = rawBoolean;
	}
	
	@Override
	public boolean isExecute() {
		return ValueHandler.getBoolean(rawBoolean, this.getDynParser());
	}
}
