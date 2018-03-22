package com.benbeehler.ignislang.objects;

import com.benbeehler.ignislang.syntax.SyntaxBlock;

public class IRoutine extends SyntaxBlock {

	public void startRoutine() {
		new Thread(() -> {
			execute();
		}).start();
	}
	
}
