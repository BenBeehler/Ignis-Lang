package com.benbeehler.ignislang.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.benbeehler.ignislang.exception.ErrorHandler;
import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.objects.IVariable;
import com.benbeehler.ignislang.syntax.SyntaxHandler;

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
		
		/*ArrayList<String> finalResult = new ArrayList<>();
		
		for(String string : result) {
			if(SyntaxHandler.containsDefinitionKeyword(string)) {
				finalResult.add(string + SyntaxHandler.ENDLINE);
			} else if(string.trim().endsWith(";")) {
				System.out.println("no");
				finalResult.add(SyntaxHandler.replaceLast(string, ";", SyntaxHandler.ENDLINE));
			}
		}
		
		String newFinal = SyntaxHandler.convert(finalResult.toArray(new String[finalResult.size()]));
		
		List<String> finished = new ArrayList<>();
		for(String str : newFinal.split(SyntaxHandler.ENDLINE)) {
			finished.add(str);
		}
		
		System.out.println(finished);*/
		
		return result;
	}
	
	public static String readFileData(File file) throws IRuntimeException {
		StringBuilder sb = new StringBuilder();
		
		try {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			throw new IRuntimeException(ErrorHandler
					.constructUnknownFileError(file));
		}
		
		return sb.toString();
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
	
	public static String readIn(FileInputStream str) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		int content;
		while ((content = str.read()) != -1) {
			sb.append((char) content);
		}
		
		return sb.toString();
	}
	
	public static String readIn(InputStream str) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		int content;
		while ((content = str.read()) != -1) {
			sb.append((char) content);
		}
		
		return sb.toString();
	}
	
	public static String read(BufferedReader br) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		String line;
		while((line = br.readLine()) != null) {
			sb.append(line);
		}
		
		return sb.toString();
	}
	
	public static void domReplace(Document document, String keyword, String base) {
		Elements links = document.select("a[href]");
			for (Element link : links)  {
				if (!link.attr("href").toLowerCase().startsWith("http://"))    {
					link.attr("href", link.attr("href").replaceFirst(keyword, base));
				}
			}
		}
}
