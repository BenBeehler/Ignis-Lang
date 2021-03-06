package com.benbeehler.ignislang.syntax;

import java.util.ArrayList;
import java.util.List;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.exception.ISyntaxException;
import com.benbeehler.ignislang.objects.ICategory;
import com.benbeehler.ignislang.objects.IForLoop;
import com.benbeehler.ignislang.objects.IFunction;
import com.benbeehler.ignislang.objects.IModule;
import com.benbeehler.ignislang.objects.IRoutine;
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
		this.setCurrent(this.getBlock());
		
		List<SyntaxBlock> all = new ArrayList<>();
		all.addAll(getRuntime().getNecessary());
		all.addAll(getRuntime().getImported());
		all.addAll(block.getSubblocks());
		this.getRuntime().getNecessary().forEach(e -> {
			all.addAll(SyntaxBlock.extractAll(e));
		});
		
		for(String line : block.getLines()) {
			line = line.trim();
			String[] split = line.split(" ");
			String[] spl = line.split("=");
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
						
						//System.out.println(b.getName());
						
						if(b.isExecute()) {
							if(b instanceof IForLoop) {
								b.execute();
							} else if(b instanceof IRoutine) {
								new Thread(() -> {
									DynamicParser parser = new DynamicParser(b, this.getRuntime());
									try {
										parser.start();
									} catch (IRuntimeException e1) {
										e1.printStackTrace();
									}
								}).start();;
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
			} else if(split[0].equals("category")) {
				if(split.length == 3) {
					String name = split[1];
					
					if(this.getBlock() instanceof IModule) {
						name = this.getBlock().getName() + "." + name;
					}
					
					line = line.replaceFirst(split[0], "").replaceFirst(split[1], "").trim();
					line = line.replaceFirst("void", "0");
					if(ValueHandler.isInteger(line)) {
						int count = ValueHandler.getInteger(line);
						ICategory cat = new ICategory(name, count);
						this.getRuntime().getNecessaryCats().add(cat);
						this.getBlock().getCategories().add(cat);
					} else {
						throw new IRuntimeException("3rd argument in category statement must be explicitely"
								+ "an int.");
					}
				} else {
					throw new IRuntimeException("Category statement must"
							+ "be formatted as \"category [name] [parameter_count]\"");
				}
			} else if(this.getBlock().getVariables()
					.stream().filter(e -> e.getName().equals(spl[0].trim()))
					.findFirst().isPresent()) {
				this.getBlock().getVariables()
						.stream().filter(e -> e.getName().equals(spl[0].trim()))
						.findFirst().get().setValue(SyntaxHandler.remapVariable(line, this).getValue());
			} else if(this.getBlock().getCategories().stream()
					.filter(t -> t.getName().equals(first))
					.findFirst()
					.isPresent()) {
				
				ICategory cat = this.getBlock().getCategories().stream()
						.filter(t -> t.getName().equals(first))
						.findFirst()
						.get();
				
				if(split.length == 3) {
					if(split[1].equals("=>")) {
						String fName = split[2];
						
						if(this.getBlock().getSubblocks()
						.stream().filter(b -> b.getName()
								.equals(fName)).findFirst()
						.isPresent()) {
							SyntaxBlock block = this.getBlock().getSubblocks()
									.stream().filter(b -> b.getName()
											.equals(fName)).findFirst()
									.get();
							if(block instanceof IFunction) {
								IFunction func = (IFunction) block;
								if(((IFunction) block).getParameters().size() == cat.getpLength()) {
									cat.getFunctions().add(func);
								} else {
									throw new IRuntimeException("Category-Function Addition requires that "
											+ "function parameter count matches category parameter count.");
								}
							} else {
								throw new IRuntimeException("Category-Function Addition must include a function.");
							}
						} else if(fName.split(".").length != 0) {
							if(all.stream().filter(b -> b.getName()
									.equals(fName.split(".")[0])).findFirst()
							.isPresent()) {
								if(fName.split(".").length == 2) {
									if(fName.split(".")[1].equals("auto*")) {
										for(SyntaxBlock block : all) {
											if(block instanceof IFunction) {
												IFunction func = (IFunction) block;
												if(func.isExecute()) {
													cat.getFunctions().add(func);
												}
											}
										}
									}
								} else {
									throw new ISyntaxException("Category-Statement: Module must have functions specified", this);
								}
							}
						} else {
							//throw new IRuntimeException("Category-Function Addition must include a function.");
						}
					} else {
						SyntaxHandler.parseCategoryCall(line, this);
					}
				} else {
					SyntaxHandler.parseCategoryCall(line, this);
				}
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
			} else if(line.trim().equals("stop")) {
				break;
			} else if(line.startsWith("#") || line.trim().equals("")) {
				//do nothing
			} else {
				throw new ISyntaxException("Unknown disturbance", this);
			}
			
			this.addLine();
		}
		
		this.getBlock().getVariables().clear();
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