package com.benbeehler.ignislang.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.benbeehler.ignislang.exception.ErrorHandler;
import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.objects.IVariable;

public class Util {
	
	public static List<String> readFile(File file) throws IRuntimeException {
		List<String> result = new ArrayList<>();
		
		try {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine()) {
				result.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			throw new IRuntimeException(ErrorHandler
					.constructUnknownFileError(file));
		}
		
		return result;
	}
	
	public static String generateID() {
		return String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
	}
	
	public static List<IVariable> removeDuplicates(List<IVariable> list) {
		List<String> names = new ArrayList<>();
		
		List<IVariable> valid = new ArrayList<>();
		
		list.forEach(var -> {
			if(!names.contains(var.getName())) {
				valid.add(var);
				names.add(var.getName());
			}
		});
		
		return valid;
	}
	
	public static String readIn() {
		StringBuilder sb = new StringBuilder();
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
			
		while(scanner.hasNextLine()) {
			sb.append(scanner.nextLine());
			break;
		}
		
		return sb.toString();
	}
}
