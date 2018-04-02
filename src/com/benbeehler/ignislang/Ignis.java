package com.benbeehler.ignislang;

import java.io.File;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.runtime.IRuntime;
import com.benbeehler.ignislang.runtime.ValueHandler;

public class Ignis {

	public static void main(String[] args) throws IRuntimeException {
		/*
		 * program starting point
		 */

		if(args.length > 0) {
			ValueHandler.init();
			
			File file = new File(args[0]);
			if(file.exists()) {
				System.setProperty("user.dir", file.getAbsolutePath());
				
				IRuntime main = 
						new IRuntime(file);
				
				try {
					main.start();
				} catch(IRuntimeException e) {
					e.printStackTrace();
				}
			} else {
				throw new IRuntimeException("Specified File does not exist.");
			}
		} else {
			System.out.println("The Ignis Programming Language " + Ref.VERSION);
		}
	}
	
	public static long time() {
		return System.nanoTime();
	}
	
	public static long c(long start, long end) {
		return end-start;
	}
}
