package com.benbeehler.ignislang.objects;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.runtime.ValueHandler;
import com.benbeehler.ignislang.syntax.SyntaxBlock;

public class ICondition extends SyntaxBlock {
	
	private String rawBoolean = "";
	private boolean normal = true;

	public String getRawBoolean() {
		return rawBoolean;
	}

	public void setRawBoolean(String rawBoolean) {
		this.rawBoolean = rawBoolean;
	}
	
	@Override
	public boolean isExecute() {
		if(isNormal()) {
			if(ValueHandler.isFunctionCall(rawBoolean, this.getDynParser())) {
				try {
					return ValueHandler.getBoolean(ValueHandler.getFunctionCall(rawBoolean, this.getDynParser()).toString(), this.getDynParser());
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
			
			return ValueHandler.getBoolean(rawBoolean, this.getDynParser());
		} else {
			return !ValueHandler.getBoolean(rawBoolean, this.getDynParser());
		}
	}

	public boolean isNormal() {
		return normal;
	}

	public void setNormal(boolean normal) {
		this.normal = normal;
	}
}
