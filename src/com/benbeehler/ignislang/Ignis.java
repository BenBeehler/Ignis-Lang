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
			
			IRuntime main = 
					new IRuntime(new File(args[0]));
			
			try {
				main.start();
			} catch(IRuntimeException e) {
				e.printStackTrace();
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
