package com.benbeehler.ignislang.syntax;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.objects.IForLoop;
import com.benbeehler.ignislang.objects.IFunction;
import com.benbeehler.ignislang.objects.IModule;
import com.benbeehler.ignislang.objects.IVariable;
import com.benbeehler.ignislang.runtime.IRuntime;
import com.benbeehler.ignislang.runtime.ValueHandler;

public class DynamicParser extends Parser {

	/*
	 * Code execution happens here
	 */
	
	private SyntaxBlock block;
	private IRuntime runtime;
	private boolean running = true;
	
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
						b.setRuntime(runtime);
						b.setMaster(this.getBlock());
						b.setDynParser(this);
						
						if(b.isExecute()) {
							if(b instanceof IForLoop) {
								b.execute();
							} else {
								DynamicParser parser = new DynamicParser(b, this.getRuntime());
								parser.start();
							}
						}
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
				if(block instanceof IModule) {
					var.setName(block.getName() + "." + var.getName());
				}
				this.getBlock().getVariables().add(var);
			} else if(split[0].equals("Return")) {
				if(split.length == 2) {
					Object val = ValueHandler.getValue(line.replaceFirst(split[0], "").trim(), this).getValue();
					
					if(this.getBlock() instanceof IFunction) {
						//System.out.println(val);
						IFunction function = (IFunction) this.getBlock();
						function.setReturnValue(val);
						this.setRunning(false);
					} else {
						throw new IRuntimeException("Return statement must ONLY be included within a function.");
					}
				} else {
					throw new IRuntimeException("Return statement must contain a value.");
				}
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

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}