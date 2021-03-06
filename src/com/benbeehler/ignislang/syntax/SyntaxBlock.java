package com.benbeehler.ignislang.syntax;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.objects.ICategory;
import com.benbeehler.ignislang.objects.IVariable;
import com.benbeehler.ignislang.runtime.IRuntime;
import com.benbeehler.ignislang.utils.Util;

public class SyntaxBlock {

	protected String name = "main";
	protected String id = Util.generateID();
	private boolean execute = false;
	private List<String> lines = 
				new ArrayList<>();
	private ArrayList<IVariable> variables = 
			new ArrayList<>();
	private SyntaxBlock master;
	private DynamicParser dynParser;
	private List<ICategory> categories = 
			new ArrayList<>();
	
	private IRuntime runtime;
	private List<SyntaxBlock> subblocks = new ArrayList<>();
	//private SyntaxBlock masterBlock = new SyntaxBlock();
	private Parser parser;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isExecute() {
		return execute;
	}
	public void setExecute(boolean execute) {
		this.execute = execute;
	}
	public List<String> getLines() {
		return lines;
	}
	public void setLines(List<String> lines) {
		this.lines = lines;
	}
	public void setLines(String[] lines) {
		this.lines.addAll(Arrays.asList(lines));
	}
	public void addLine(String line) {
		this.lines.add(line);
	}
	
	public void execute() {
		Parser parser = new Parser(this.getLines(), runtime);
		try {
			parser.start();
		} catch (IRuntimeException e) {
			e.printStackTrace();
		}
	}
	
	public void execute(SyntaxBlock main) {
		/*Parser parser = new Parser(this.getLines(), runtime);
		parser.setMain(main);
		parser.start();*/
	}
	
	public static SyntaxBlock importFileIntoBody(File file) throws IRuntimeException {
		List<String> list = Util.readFile(file);
		String[] input = list
				.toArray(new String[list.size()]);
		
		Tokenizer tok = new Tokenizer(input);
		SyntaxBlock block = new SyntaxBlock();
		block.setLines(tok.getOutput());
		
		return block;
	}
	
	public static String generateUniqueID() {
		return String.valueOf(new Random()
				.nextInt(Integer.MAX_VALUE));
	}

	public IRuntime getRuntime() {
		return runtime;
	}

	public void setRuntime(IRuntime runtime) {
		this.runtime = runtime;
	}
	
	public List<SyntaxBlock> getSubblocks() {
		return subblocks;
	}
	
	public void setSubblocks(List<SyntaxBlock> subblocks) {
		this.subblocks = subblocks;
	}
	
	public void addBlock(SyntaxBlock block) { this.subblocks.add(block); }
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<IVariable> getVariables() {
		return variables;
	}
	public SyntaxBlock getMaster() {
		return master;
	}
	public void setMaster(SyntaxBlock master) {
		this.getSubblocks().addAll(master.getSubblocks());
		this.getVariables().addAll(master.getVariables());
		
		if(this.getRuntime() != null) {
			this.getRuntime().getNecessary().forEach(e -> {
				this.getSubblocks().addAll(SyntaxBlock.extractAll(e));
			});
			
			for(SyntaxBlock b : this.getRuntime().getNecessary()) {
				this.getVariables().addAll(b.getVariables());
			}
		}
		
		this.getCategories().addAll(master.getCategories());
		this.getCategories().addAll(this.getRuntime().getNecessaryCats());
		
		this.master = master;
	}
	
	public static List<SyntaxBlock> extractAll(SyntaxBlock block) {
		List<SyntaxBlock> val = new ArrayList<>();
		val.add(block);
		for(SyntaxBlock b : block.getSubblocks()) {
			val.add(b);
		}
		return val;
	}
	
	public Parser getParser() {
		return parser;
	}
	
	public void setParser(Parser parser) {
		this.parser = parser;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public DynamicParser getDynParser() {
		return dynParser;
	}
	
	public void setDynParser(DynamicParser dynParser) {
		this.dynParser = dynParser;
	}
	public List<ICategory> getCategories() {
		return categories;
	}
	public void setCategories(List<ICategory> categories) {
		this.categories = categories;
	}
}
