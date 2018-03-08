package com.benbeehler.ignislang.syntax;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.objects.IFunction;
import com.benbeehler.ignislang.objects.IVariable;
import com.benbeehler.ignislang.runtime.IRuntime;
import com.benbeehler.ignislang.runtime.ValueHandler;

public class DynamicParser extends Parser {

	/*
	 * Code execution happens here
	 */
	
	private SyntaxBlock block;
	private IRuntime runtime;
	
	public DynamicParser(SyntaxBlock block, IRuntime runtime) {
		super(block, runtime);
		this.setBlock(block);
		this.setRuntime(runtime);
	}

	@Override
	public void start() throws IRuntimeException {
		//System.out.println("parsing " + block.getName());
		for(String line : block.getLines()) {
			line = line.trim();
			String[] split = line.split(" ");
			String first = split[0];
			
			if(first.equalsIgnoreCase("CALL_BLOCK")) {
				if(split.length == 2) {
					String id = split[1];
					
					if(this.getBlock().getSubblocks()
							.stream().filter(bl -> bl.getId()
									.equals(id)).findFirst()
							.isPresent()) {
						
						SyntaxBlock b = this.getBlock().getSubblocks().stream()
								.filter(bl -> bl.getId().equals(id)).findFirst().get();
						b.setMaster(this.getBlock());
						DynamicParser parser = new DynamicParser(b, this.getRuntime());
						parser.start();
					}
				}
			} else if(this.getBlock().getSubblocks()
					.stream().filter(b -> b.getName()
							.equals(first)).findFirst()
					.isPresent()) {
				SyntaxBlock block = this.getBlock().getSubblocks()
						.stream().filter(b -> b.getName()
								.equals(first)).findFirst()
						.get();
				if(block instanceof IFunction) {
					SyntaxHandler.parseFunctionCall(line, this);
				}
			} else if(ValueHandler.objects.stream()
					.filter(t -> t.getName().equals(first))
					.findFirst()
					.isPresent()) {
				IVariable var = SyntaxHandler.parseVariable(line, this);
				this.getBlock().getVariables().add(var);
			}
		}
	}
	
	public SyntaxBlock getBlock() {
		return block;
	}

	public void setBlock(SyntaxBlock block) {
		this.block = block;
	}
	
	@Override
	public IRuntime getRuntime() {
		return this.runtime;
	}

	@Override
	public void setRuntime(IRuntime runtime) {
		this.runtime = runtime;
	}
}
