package com.benbeehler.ignislang;

import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Ref {

	public static final String VERSION = "0.1.2";
	
	public static final Executor TH_HANDLER = Executors.newCachedThreadPool(); 
	//new thread pool for concurrency, final for safety
	
	public static final Scanner STDIN = new Scanner(System.in);
	
}
