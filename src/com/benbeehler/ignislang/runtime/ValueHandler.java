package com.benbeehler.ignislang.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.benbeehler.ignislang.exception.IRuntimeException;
import com.benbeehler.ignislang.exception.ISyntaxException;
import com.benbeehler.ignislang.objects.ICategory;
import com.benbeehler.ignislang.objects.IFunction;
import com.benbeehler.ignislang.objects.IObject;
import com.benbeehler.ignislang.objects.IVariable;
import com.benbeehler.ignislang.objects.Scope;
import com.benbeehler.ignislang.runtime.server.JettyHandler;
import com.benbeehler.ignislang.syntax.DynamicParser;
import com.benbeehler.ignislang.syntax.SyntaxBlock;
import com.benbeehler.ignislang.syntax.SyntaxHandler;
import com.benbeehler.ignislang.utils.Util;

public class ValueHandler {

	public static IObject STRING = new IObject("string");
	public static IObject OBJECT = new IObject("object");
	public static IObject DECIMAL = new IObject("decimal");
	public static IObject BOOLEAN = new IObject("bool");
	public static IObject INTEGER = new IObject("int");
	public static IObject TUPLE = new IObject("tuple");
	public static IObject HASH = new IObject("hash");
	
	public static final List<IObject> objects = 
			new ArrayList<>();
	public static final List<IFunction> functions = 
			new ArrayList<>();
	public static final List<ICategory> categories =
			new ArrayList<>();
	
	public static void init() throws IRuntimeException {
		objects.add(STRING);
		objects.add(OBJECT);
		objects.add(DECIMAL);
		objects.add(BOOLEAN);
		objects.add(INTEGER);
		objects.add(TUPLE);
		objects.add(HASH);
		
		initNative();
	}
	
	public static void initNative() throws IRuntimeException {
		SyntaxBlock block = new SyntaxBlock();
		IFunction function = new IFunction(block);
		function.setName("IO.Puts");
		function.setNativ(true);
		function.addParameter(new IVariable("param_1", Scope.PRIVATE));
		function.addParameter(new IVariable("param_2", Scope.PRIVATE));
		function.setRunnable(() -> {
			if(function.getParameters().size() == 2) {
				Object out = function.getParameters().get(0).getValue();
				Object value = function.getParameters().get(1).getValue();
				
				if(out instanceof PrintWriter) {
					@SuppressWarnings("resource")
					PrintWriter stream = (PrintWriter) out;
					stream.print(value.toString());
					stream.flush();
				} else {
					try {
						throw new IRuntimeException("Invalid Parameter Count");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(function);
		
		SyntaxBlock eBlock = new SyntaxBlock();
		IFunction equals = new IFunction(eBlock);
		equals.setName("Equals");
		equals.setNativ(true);
		equals.addParameter(new IVariable("param_1", Scope.PRIVATE));
		equals.addParameter(new IVariable("param_2", Scope.PRIVATE));
		equals.setRunnable(() -> {
			if(equals.getParameters().size() == 2) {
				Object one = equals.getParameters().get(0).getValue();
				Object two = equals.getParameters().get(1).getValue();
				
				equals.setReturnValue(one.equals(two));
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(equals);
		
		SyntaxBlock pBlock = new SyntaxBlock();
		IFunction println = new IFunction(pBlock);
		println.setName("IO.Println");
		println.setNativ(true);
		println.addParameter(new IVariable("param_1", Scope.PRIVATE));
		println.setRunnable(() -> {
			if(println.getParameters().size() == 1) {
				Object value = println.getParameters().get(0).getValue();
				System.out.println(value);
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println);
		
		SyntaxBlock pBlock1 = new SyntaxBlock();
		IFunction println1 = new IFunction(pBlock1);
		println1.setName("TypeOf");
		println1.setNativ(true);
		println1.addParameter(new IVariable("param_1", Scope.PRIVATE));
		println1.setRunnable(() -> {
			if(println1.getParameters().size() == 1) {
				Object value = println1.getParameters().get(0).getValue();
				try {
					println1.setReturnValue(getType(value.toString(), pBlock1).getName());
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println1);
		
		SyntaxBlock pBlock11 = new SyntaxBlock();
		IFunction println11 = new IFunction(pBlock11);
		println11.setName("ToString");
		println11.setNativ(true);
		println11.addParameter(new IVariable("param_1", Scope.PRIVATE));
		println11.setRunnable(() -> {
			if(println11.getParameters().size() == 1) {
				Object value = println11.getParameters().get(0).getValue();
				println11.setReturnValue(getString(value.toString()));
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println11);
		
		SyntaxBlock pBlock111 = new SyntaxBlock();
		IFunction println111 = new IFunction(pBlock111);
		println111.setName("ToInt");
		println111.setNativ(true);
		println111.addParameter(new IVariable("param_1", Scope.PRIVATE));
		println111.setRunnable(() -> {
			if(println111.getParameters().size() == 1) {
				Object value = println111.getParameters().get(0).getValue();
				try {
					println111.setReturnValue(getInteger(value.toString(), pBlock111.getVariables()));
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println111);
		
		SyntaxBlock pBlock1111 = new SyntaxBlock();
		IFunction println1111 = new IFunction(pBlock1111);
		println1111.setName("ToDecimal");
		println1111.setNativ(true);
		println1111.addParameter(new IVariable("param_1", Scope.PRIVATE));
		println1111.setRunnable(() -> {
			if(println1111.getParameters().size() == 1) {
				Object value = println1111.getParameters().get(0).getValue();
				try {
					println1111.setReturnValue(getDecimal(value.toString(), pBlock1111.getVariables()));
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println1111);
		
		SyntaxBlock pBlock11111 = new SyntaxBlock();
		IFunction println11111 = new IFunction(pBlock11111);
		println11111.setName("ToBool");
		println11111.setNativ(true);
		println11111.addParameter(new IVariable("param_1", Scope.PRIVATE));
		println11111.setRunnable(() -> {
			if(println11111.getParameters().size() == 1) {
				Object value = println11111.getParameters().get(0).getValue();
				println11111.setReturnValue(getBoolean(value.toString(), pBlock11111.getDynParser()));
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println11111);
		
		SyntaxBlock pBlock111111 = new SyntaxBlock();
		IFunction println111111 = new IFunction(pBlock111111);
		println111111.setName("IO.In");
		println111111.setNativ(true);
		println111111.setRunnable(() -> {
			if(println111111.getParameters().size() == 0) {
				println111111.setReturnValue(com.benbeehler.ignislang.utils.Util.readIn());
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println111111);
		
		SyntaxBlock pBlock1111111 = new SyntaxBlock();
		IFunction println1111111 = new IFunction(pBlock1111111);
		println1111111.setName("Tuple.Get");
		println1111111.addParameter(new IVariable("param_1", Scope.PRIVATE));
		println1111111.addParameter(new IVariable("param_2", Scope.PRIVATE));
		println1111111.setNativ(true);
		println1111111.setRunnable(() -> {
			if(println1111111.getParameters().size() == 2) {
				Object one = println1111111.getParameters().get(0).getValue();
				Object two = println1111111.getParameters().get(1).getValue();
				
				if(one instanceof ArrayList) {
					@SuppressWarnings("unchecked")
					List<Object> list = (ArrayList<Object>) one;
					if(isInteger(two.toString())) {
						try {
							println1111111.setReturnValue(list.get(getInteger(two.toString())));
						} catch (IRuntimeException e) {
							e.printStackTrace();
						}
					} else {
						try {
							throw new IRuntimeException("Value must be an int.");
						} catch (IRuntimeException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						throw new IRuntimeException("Value must be a tuple.");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(println1111111);
		
		SyntaxBlock tuple_index_block = new SyntaxBlock();
		IFunction tuple_index = new IFunction(tuple_index_block);
		tuple_index.setName("Tuple.Index");
		tuple_index.addParameter(new IVariable("param_1", Scope.PRIVATE));
		tuple_index.addParameter(new IVariable("param_2", Scope.PRIVATE));
		tuple_index.setNativ(true);
		tuple_index.setRunnable(() -> {
			if(tuple_index.getParameters().size() == 2) {
				Object one = tuple_index.getParameters().get(0).getValue();
				Object two = tuple_index.getParameters().get(1).getValue();
				
				if(one instanceof ArrayList<?>) {
					@SuppressWarnings("unchecked")
					List<Object> list = (ArrayList<Object>) one;
					tuple_index.setReturnValue(list.indexOf(two));
				} else {
					try {
						throw new IRuntimeException("Value must be a tuple.");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(tuple_index);
		
		SyntaxBlock tuple_add_block = new SyntaxBlock();
		IFunction tuple_add = new IFunction(tuple_add_block);
		tuple_add.setName("Tuple.Add");
		tuple_add.addParameter(new IVariable("param_1", Scope.PRIVATE));
		tuple_add.addParameter(new IVariable("param_2", Scope.PRIVATE));
		tuple_add.setNativ(true);
		tuple_add.setRunnable(() -> {
			if(tuple_add.getParameters().size() == 2) {
				Object one = tuple_add.getParameters().get(0).getValue();
				Object two = tuple_add.getParameters().get(1).getValue();
				
				if(one instanceof ArrayList<?>) {
					@SuppressWarnings("unchecked")
					List<Object> list = (ArrayList<Object>) one;
					list.add(two);
					tuple_add.setReturnValue(list);
				} else {
					try {
						throw new IRuntimeException("Value must be a tuple.");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(tuple_add);
		
		SyntaxBlock tuple_add_block1 = new SyntaxBlock();
		IFunction tuple_add1 = new IFunction(tuple_add_block1);
		tuple_add1.setName("Tuple.Remove");
		tuple_add1.addParameter(new IVariable("param_1", Scope.PRIVATE));
		tuple_add1.addParameter(new IVariable("param_2", Scope.PRIVATE));
		tuple_add1.setNativ(true);
		tuple_add1.setRunnable(() -> {
			if(tuple_add1.getParameters().size() == 2) {
				Object one = tuple_add1.getParameters().get(0).getValue();
				Object two = tuple_add1.getParameters().get(1).getValue();
				
				if(one instanceof ArrayList<?>) {
					@SuppressWarnings("unchecked")
					List<Object> list = (ArrayList<Object>) one;
					list.remove(two);
					tuple_add1.setReturnValue(list);
				} else {
					try {
						throw new IRuntimeException("Value must be a tuple.");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(tuple_add1);
		
		SyntaxBlock size_block = new SyntaxBlock();
		IFunction size_func = new IFunction(size_block);
		size_func.setName("Size");
		size_func.addParameter(new IVariable("param_1", Scope.PRIVATE));
		size_func.setNativ(true);
		size_func.setRunnable(() -> {
			if(size_func.getParameters().size() == 1) {
				Object one = size_func.getParameters().get(0).getValue();
				
				if(one instanceof ArrayList<?>) {
					@SuppressWarnings("unchecked")
					List<Object> list = (ArrayList<Object>) one;
					size_func.setReturnValue(list.size());
				} else if(one instanceof String) {
					size_func.setReturnValue(one.toString().length());
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(size_func);
		
		SyntaxBlock size_block1 = new SyntaxBlock();
		IFunction size_func1 = new IFunction(size_block1);
		size_func1.setName("ToTuple");
		size_func1.addParameter(new IVariable("param_1", Scope.PRIVATE));
		size_func1.setNativ(true);
		size_func1.setRunnable(() -> {
			if(size_func1.getParameters().size() == 1) {
				Object one = size_func1.getParameters().get(0).getValue();
				
				if(one instanceof ArrayList<?>) {
					@SuppressWarnings("unchecked")
					List<Object> list = (ArrayList<Object>) one;
					size_func1.setReturnValue(list);
				} else if(one instanceof String) {
					List<String> list = new ArrayList<>();
					String[] spl = one.toString().split("");
					for(String str : spl)
						list.add(str);
					
					size_func1.setReturnValue(list);
				} else {
					List<Object> list = new ArrayList<>();
					list.add(one);
					
					size_func1.setReturnValue(list);
				}
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(size_func1);
		
		SyntaxBlock size_block11 = new SyntaxBlock();
		IFunction size_func11 = new IFunction(size_block11);
		size_func11.setName("Http.StartAndListen");
		size_func11.addParameter(new IVariable("param_1", Scope.PRIVATE));
		size_func11.setNativ(true);
		size_func11.setRunnable(() -> {
			if(size_func11.getParameters().size() == 1) {
				Object one = size_func11.getParameters().get(0).getValue();
				int portNumber = Integer.parseInt(one.toString());
				
				try {
					new JettyHandler().execute(portNumber);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(size_func11);
		
		SyntaxBlock http_serve_block = new SyntaxBlock();
		IFunction http_serve = new IFunction(http_serve_block);
		http_serve.setName("Http.Serve");
		http_serve.addParameter(new IVariable("param_1", Scope.PRIVATE));
		http_serve.addParameter(new IVariable("param_2", Scope.PRIVATE));
		http_serve.addParameter(new IVariable("param_3", Scope.PRIVATE));
		http_serve.setNativ(true);
		http_serve.setRunnable(() -> {
			if(http_serve.getParameters().size() == 3) {
				if(http_serve.getParameters().get(0).getValue() instanceof Request
						&& http_serve.getParameters().get(1).getValue() instanceof HttpServletResponse) {
					Request baseRequest = (Request) http_serve.getParameters().get(0).getValue();
					HttpServletResponse response = (HttpServletResponse) http_serve.getParameters().get(1).getValue();
					String output = http_serve.getParameters().get(2).getValue().toString();
					
					response.setContentType("text/html; text/css");
				    response.setStatus(HttpServletResponse.SC_OK);
				    
					try {
					    PrintWriter out = response.getWriter();
	
					    out.println(output);
	
					    baseRequest.setHandled(true);
					} catch (IOException e) {
					}
				} else {
					try {
						throw new IRuntimeException("The given parameters do not match the required types to call this function.");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		functions.add(http_serve);
		
		SyntaxBlock io_filein_block = new SyntaxBlock();
		IFunction file_instr = new IFunction(io_filein_block);
		file_instr.setName("IO.FileInStream");
		file_instr.addParameter(new IVariable("param_1", Scope.PRIVATE));
		file_instr.setNativ(true);
		file_instr.setRunnable(() -> {
			if(file_instr.getParameters().size() == 1) {
				String fPath = file_instr.getParameters().get(0).getValue().toString();
				File f = new File(fPath);
				try {
					file_instr.setReturnValue(new FileInputStream(f));
				} catch (FileNotFoundException e) {
					try {
						throw new IRuntimeException("Specified value must be a valid file.");
					} catch (IRuntimeException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		functions.add(file_instr);
		
		SyntaxBlock io_fileout_block = new SyntaxBlock();
		IFunction file_outstr = new IFunction(io_fileout_block);
		file_outstr.setName("IO.FileOutStream");
		file_outstr.addParameter(new IVariable("param_1", Scope.PRIVATE));
		file_outstr.setNativ(true);
		file_outstr.setRunnable(() -> {
			if(file_outstr.getParameters().size() == 1) {
				String fPath = file_outstr.getParameters().get(0).getValue().toString();
				File f = new File(fPath);
				try {
					file_outstr.setReturnValue(new PrintStream(f));
				} catch (FileNotFoundException e) {
					try {
						throw new IRuntimeException("Specified value must be a valid file.");
					} catch (IRuntimeException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		functions.add(file_outstr);
		
		SyntaxBlock io_write_block = new SyntaxBlock();
		IFunction io_write = new IFunction(io_write_block);
		io_write.setName("IO.Write");
		io_write.addParameter(new IVariable("param_1", Scope.PRIVATE));
		io_write.addParameter(new IVariable("param_2", Scope.PRIVATE));
		io_write.setNativ(true);
		io_write.setRunnable(() -> {
			if(io_write.getParameters().size() == 2) {
				Object potentionalWriter = io_write.getParameters().get(0).getValue();
				String data = io_write.getParameters().get(1).getValue().toString();
				
				if(potentionalWriter instanceof PrintStream) {
					@SuppressWarnings("resource")
					PrintStream stream = (PrintStream) potentionalWriter;
					stream.print(data);
					stream.flush();
				} else {
					try {
						throw new IRuntimeException("Writer requires that the first parameter be a FileOutStream.");
					} catch (IRuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		functions.add(io_write);
		
		SyntaxBlock io_read_block = new SyntaxBlock();
		IFunction io_read = new IFunction(io_read_block);
		io_read.setName("IO.Read");
		io_read.addParameter(new IVariable("param_1", Scope.PRIVATE));
		io_read.setNativ(true);
		io_read.setRunnable(() -> {
			if(io_read.getParameters().size() == 1) {
				Object reader = io_read.getParameters().get(0).getValue();
				
				if(reader instanceof InputStream) {
					try {
						io_read.setReturnValue(Util.readIn((InputStream) reader));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						throw new IRuntimeException("Writer requires that the first parameter be a FileOutStream.");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		functions.add(io_read);
	
		SyntaxBlock io_read_block1 = new SyntaxBlock();
		IFunction io_read1 = new IFunction(io_read_block1);
		io_read1.setName("IO.Files");
		io_read1.addParameter(new IVariable("param_1", Scope.PRIVATE));
		io_read1.setNativ(true);
		io_read1.setRunnable(() -> {
			if(io_read1.getParameters().size() == 1) {
				String fPath = io_read1.getParameters().get(0).getValue().toString();
				
				File file = new File(fPath);
				
				if(file.exists()) {
					List<File> list = Arrays.asList(file.listFiles());
					List<String> val = new ArrayList<>();
					for(File f : list) {
						val.add(f.getAbsolutePath());
					}
					
					io_read1.setReturnValue(val);
				} else {
					io_read1.setReturnValue(new ArrayList<String>());
				}
			}
		});
		
		functions.add(io_read1);
		
		SyntaxBlock io_filein_block1 = new SyntaxBlock();
		IFunction file_instr1 = new IFunction(io_filein_block1);
		file_instr1.setName("Http.Load");
		file_instr1.addParameter(new IVariable("param_1", Scope.PRIVATE));
		file_instr1.addParameter(new IVariable("param_2", Scope.PRIVATE));
		file_instr1.setNativ(true);
		file_instr1.setRunnable(() -> {
			if(file_instr1.getParameters().size() == 2) {
				String fPath = file_instr1.getParameters().get(0).getValue().toString();
				String publicPath = file_instr1.getParameters().get(1).getValue().toString();
				
				File f = new File(fPath);
				try {
					String string = Util.readFileData(f);
					
					Document doc = Jsoup.parse(string, "");

					Elements mouse = doc.select("link[href]");
					for(Element e : mouse) {
						e.attr("link", publicPath + "/" + e.attr("link"));
						e.attr("href", publicPath + "/" + e.attr("href"));
					}
					
					Elements mouse1 = doc.select("script[src]");
					for(Element e : mouse1) {
						e.attr("src", publicPath + "/" + e.attr("src"));
					}
					
					file_instr1.setReturnValue(doc.toString());
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(file_instr1);
		
		SyntaxBlock io_filein_block11 = new SyntaxBlock();
		IFunction file_instr11 = new IFunction(io_filein_block11);
		file_instr11.setName("Http.GetParameters");
		file_instr11.addParameter(new IVariable("param_1", Scope.PRIVATE));
		file_instr11.setNativ(true);
		file_instr11.setRunnable(() -> {
			if(file_instr11.getParameters().size() == 1) {
				Object obj = file_instr11.getParameters().get(0).getValue();
				
				if(obj instanceof HttpServletRequest) {
					HttpServletRequest req = (HttpServletRequest) obj;
					try {
						String read = Util.read(req.getReader());
						file_instr11.setReturnValue(read);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		functions.add(file_instr11);
		
		SyntaxBlock io_filein_block111 = new SyntaxBlock();
		IFunction file_instr111 = new IFunction(io_filein_block111);
		file_instr111.setName("Http.GetQuery");
		file_instr111.addParameter(new IVariable("param_1", Scope.PRIVATE));
		file_instr111.setNativ(true);
		file_instr111.setRunnable(() -> {
			if(file_instr111.getParameters().size() == 1) {
				Object obj = file_instr111.getParameters().get(0).getValue();
				
				if(obj instanceof HttpServletRequest) {
					HttpServletRequest req = (HttpServletRequest) obj;
					file_instr111.setReturnValue(req.getQueryString());
				}
			}
		});
		
		functions.add(file_instr111);
		
		SyntaxBlock pBlock11111111111111 = new SyntaxBlock();
		IFunction tuple_index1 = new IFunction(pBlock11111111111111);
		tuple_index1.setName("Numbers.Rand");
		tuple_index1.setNativ(true);
		tuple_index1.setRunnable(() -> {
			if(tuple_index1.getParameters().size() == 0) {
				tuple_index1.setReturnValue(new Random().nextInt(Integer.MAX_VALUE));
			} else {
				try {
					throw new IRuntimeException("Invalid Parameter Count");
				} catch (IRuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		
		functions.add(tuple_index1);
		
		SyntaxBlock io_filein_block1111 = new SyntaxBlock();
		IFunction file_instr1111 = new IFunction(io_filein_block1111);
		file_instr1111.setName("Hash.Get");
		file_instr1111.addParameter(new IVariable("param_1", Scope.PRIVATE));
		file_instr1111.addParameter(new IVariable("param_2", Scope.PRIVATE));
		file_instr1111.setNativ(true);
		file_instr1111.setRunnable(() -> {
			if(file_instr1111.getParameters().size() == 2) {
				Object one = file_instr1111.getParameters().get(0).getValue();
				Object two = file_instr1111.getParameters().get(1).getValue();
				
				if(one instanceof Map) {
					@SuppressWarnings("unchecked")
					HashMap<Object, Object> map = (HashMap<Object, Object>) one;
					file_instr1111.setReturnValue(map.get(two));
				} else {
					try {
						throw new IRuntimeException("First Given Parameter is required to be a hash.");
					} catch (IRuntimeException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		functions.add(file_instr1111);
		
		ICategory cat = new ICategory("Http.Handler", 4);
		ValueHandler.categories.add(cat);
	}
	
	public static boolean containsObject(String name) {
		return objects.stream().filter(obj -> obj.getName()
				.equals(name)).findAny().isPresent();
	}
	
	public static ICategory getCategoryFromName(String name, List<ICategory> list) {
		return list.stream().filter(e -> e.getName().equals(name)).findFirst().get();
	}
	
	public static IObject getTypeByName(String name) {
		return objects.stream().filter(obj -> obj.getName()
				.equals(name)).findAny().get();
	}
	
	public static String calc(String in, List<IVariable> variables) throws ScriptException {
		try {
			for(IVariable v : variables) {
				if(in.contains(v.getName())) {
					in = in.replace(v.getName(), v.getValue().toString());
				}
			}
		} catch(ConcurrentModificationException e) {
			
		}
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		Object result = engine.eval(in);
		
		return result.toString();
	}
	
	public static String calc(String in) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		Object result = engine.eval(in);
		
		
		return result.toString();
	}
	
	public static boolean isInteger(String str, List<IVariable> var) {
		try {
			String s = String.valueOf(Integer.parseInt(calc(str, var)));
			if(s.contains(".")) return false;
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isDecimal(String str, List<IVariable> var) {
		try {
			Double.parseDouble(calc(str, var));
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isInteger(String str) {
		try {
			String s = String.valueOf(Integer.parseInt(calc(str)));
			if(s.contains(".")) return false;
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isDecimal(String str) {
		try {
			Double.parseDouble(calc(str));
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isString(String str) {
		str = str.trim();
		
		if(str.startsWith("\"")) 
			if(str.endsWith("\"")) return true;
		
		return false;
	}
	
	public static boolean isBoolean(String str, DynamicParser parser) {
		str = str.trim();
		String s = str;
		if(str.equals("true") 
				|| str.equals("false")) return true;
		if(parser.getBlock().getVariables().stream().filter(e -> e.getName()
				.equals(s)).findAny().isPresent()) {
			return isBoolean(parser.getBlock().getVariables().stream().filter(e -> e.getName()
					.equals(s)).findAny().get().getValue().toString(), parser);
		} else if(ValueHandler.isFunctionCall(str, parser)) {
			try {
				if(ValueHandler.getFunctionCall(str, parser) != null) {
					return isBoolean(ValueHandler.getFunctionCall(str, parser).toString());
				}
			} catch (IRuntimeException e1) {
				e1.printStackTrace();
			}
		} else if(str.split(" ")[0].equals("not")) {
			return isBoolean(str.replace("not", "").trim(), parser);
		}
		return false;
	}
	
	public static boolean isBoolean(String str) {
		str = str.trim();
		if(str.equals("true") 
				|| str.equals("false")) return true;
		else if(str.split(" ")[0].equals("not")) return isBoolean(str.replace("not", "").trim());
		return false;
	}
	
	public static int getInteger(String str, List<IVariable> var) throws IRuntimeException {
		try {
			return Integer.parseInt(calc(str, var));
		} catch (NumberFormatException | ScriptException e) {
			throw new IRuntimeException("Failed to parse integer, invalid input");
		}
	}
	
	public static double getDecimal(String str) throws IRuntimeException {
		try {
			return Double.parseDouble(calc(str));
		} catch (NumberFormatException | ScriptException e) {
			throw new IRuntimeException("Failed to parse double, invalid input");
		}
	}
	
	public static List<Object> getRawList(String string, DynamicParser parser) throws IRuntimeException {
		List<Object> value = new ArrayList<>(); 
		string = string.trim();
		if(string.startsWith(SyntaxHandler.OPEN_ARRAY_BRACKET) 
				&& string.endsWith(SyntaxHandler.CLOSE_ARRAY_BRACKET)) {
			string = string.replace(SyntaxHandler.OPEN_ARRAY_BRACKET, "")
					.replace(SyntaxHandler.CLOSE_ARRAY_BRACKET, "").trim();
			
			String[] split = string.split(SyntaxHandler.SEMICOLON);
			for(String str : split) {
				Object obj = getValue(str, parser).getValue();
				value.add(obj);
			}
		}
		
		return value;
	}
	
	public static Map<Object, Object> getRawHash(String string, DynamicParser parser) throws IRuntimeException {
		Map<Object, Object> value = new HashMap<>(); 
		string = string.trim();
		if(string.startsWith(SyntaxHandler.OPEN_OBJ_BRACKET) 
				&& string.endsWith(SyntaxHandler.CLOSE_OBJ_BRACKET)) {
			string = string.replace(SyntaxHandler.OPEN_OBJ_BRACKET, "")
					.replace(SyntaxHandler.CLOSE_OBJ_BRACKET, "").trim();
			
			String[] split = string.split(SyntaxHandler.SEMICOLON);
			for(String str : split) {
				String[] spl = str.split(SyntaxHandler.COLON);
				
				if(spl.length == 2) {
					Object one = ValueHandler.getValue(spl[0], parser).getValue();
					Object two = ValueHandler.getValue(spl[1], parser).getValue();
					
					value.put(one, two);
				} else {
					throw new ISyntaxException("Hash declaration requires a key/value pair.", parser);
				}
			}
		}
		
		return value;
	}
	
	public static boolean isRawList(String string) {
		string = string.trim();
		return (string.startsWith(SyntaxHandler.OPEN_ARRAY_BRACKET) 
				&& string.endsWith(SyntaxHandler.CLOSE_ARRAY_BRACKET));
	}
	
	public static boolean isRawHash(String string) {
		string = string.trim();
		return (string.startsWith(SyntaxHandler.OPEN_OBJ_BRACKET) 
				&& string.endsWith(SyntaxHandler.CLOSE_OBJ_BRACKET));
	}
	
	public static boolean isList(String string) {
		string = string.trim();
		return (string.startsWith("[") 
				&& string.endsWith("]"));
	}
	
	public static boolean isMap(String string) {
		string = string.trim();
		return (string.startsWith("{") 
				&& string.endsWith("}"));
	}
	
	public static HashMap<Object, Object> getMap(String s) {
		HashMap<Object, Object> map = new HashMap<>();
		s = s.replaceFirst("{", "");
		s = SyntaxHandler.replaceLast(s, "}", "").trim();
		String[] pairs = s.split(",");
		for (int i=0;i<pairs.length;i++) {
		    String pair = pairs[i];
		    String[] keyValue = pair.split("=");
		    map.put(keyValue[0], keyValue[1]);
		}
		
		return map;
	}
	
	public static int getInteger(String str) throws IRuntimeException {
		try {
			return Integer.parseInt(calc(str));
		} catch (NumberFormatException | ScriptException e) {
			throw new IRuntimeException("Failed to parse integer, invalid input");
		}
	}
	
	public static double getDecimal(String str, List<IVariable> var) throws IRuntimeException {
		try {
			return Double.parseDouble(calc(str, var));
		} catch (NumberFormatException | ScriptException e) {
			throw new IRuntimeException("Failed to parse double, invalid input");
		}
	}
	
	public static boolean getBoolean(String str, DynamicParser parser) {
		str = str.trim();
		String s = str;
		if(str.trim().equals("true")) return true;
		if(str.trim().split(" ")[0].equals("not")) return !getBoolean(str.replace("not", "").trim(), parser);
		if(parser.getBlock().getVariables().stream().filter(e -> e.getName()
				.equals(s)).findAny().isPresent()) {
			return getBoolean(parser.getBlock().getVariables().stream().filter(e -> e.getName()
					.equals(s)).findAny().get().getValue().toString(), parser);
		} else if(ValueHandler.isFunctionCall(str, parser)) {
			try {
				return getBoolean(ValueHandler.getFunctionCall(str, parser).toString(), parser);
			} catch (IRuntimeException e1) {
				e1.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static boolean getBoolean(String str) {
		str = str.trim();
		if(str.trim().equals("true")) return true;
		if(str.trim().split(" ")[0].equals("not")) return !getBoolean(str.replace("not", "").trim());
		return false;
	}
	
	public static String getString(String str) {
		str = str.trim();
		str = str.replaceFirst("\"", "");
		str = SyntaxHandler.reverse(SyntaxHandler
				.reverse(str.replaceFirst("\"", "")));
		return str;
	}
	
	public static IVariable getValue(String str) throws IRuntimeException {
		IVariable variable = new IVariable("var", Scope.PRIVATE);
		variable.setValue("");
		
		if(isInteger(str)) {
			variable.setValue(getInteger(str));
		} else if(isDecimal(str)) {
			variable.setValue(getDecimal(str));
		} else if(isBoolean(str)) {
			variable.setValue(getBoolean(str));
		} else if(isString(str)) {
			variable.setValue(getString(str));
		} else if(isRawHash(str)) {
			variable.setValue(getRawHash(str, null));
		}
		
		return variable;
	}
	
	public static IVariable getValue(String str, SyntaxBlock block) throws IRuntimeException {
		str = str.trim();
		//System.out.println(str);
		String string = str;
		IVariable variable = new IVariable("var", Scope.PRIVATE);
		variable.setValue("");
		
		//block.getVariables().forEach(e -> System.out.println(e.getName()));
		
		if(block.getVariables().stream()
				.filter(b -> b.getName().equals(string)).findFirst().isPresent()) {
			IVariable b = block.getVariables().stream()
					.filter(bl -> bl.getName().equals(string)).findFirst().get();
			//System.out.println("meep " + string);
			variable = b;
		} else if(isInteger(string, block.getVariables())) {
			variable.setValue(getInteger(string, block.getVariables()));
		} else if(isDecimal(string, block.getVariables())) {
			variable.setValue(getDecimal(string, block.getVariables()));
		} else if(isBoolean(string)) {
			variable.setValue(getBoolean(string));
		} else if(isString(string)) {
			variable.setValue(getString(string));
		} else if(isRawList(string)) {
			variable.setValue(getRawList(string, block.getDynParser()));
		} else if(isRawHash(string)) {
			variable.setValue(getRawHash(string, block.getDynParser()));
		} else if(string.startsWith("new")) {
			String inst = string.replaceFirst("new", "").trim();
			
			if(block.getSubblocks().stream()
					.filter(b -> b.getName().equals(inst)).findAny().isPresent()) {
				SyntaxBlock bl = block.getSubblocks().stream()
						.filter(b -> b.getName().equals(inst)).findAny().get();
				if(bl instanceof IObject) {
					IObject obje = (IObject) bl;
					variable = instantiate(obje, variable);
				}
			} else {
				throw new IRuntimeException("Type specified does not exist.");
			}
		}
		
		return variable;
	}
	
	public static boolean isFunctionCall(String str, DynamicParser parser) {
		if(parser.getBlock().getSubblocks()
				.stream().filter(b -> b.getName()
						.equals(str.split(" ")[0]))
				.findAny().isPresent()) {
			return true;
		}
		
		return false;
	}
	
	public static Object getFunctionCall(String str, DynamicParser parser) throws IRuntimeException {
		//System.out.println(str);
		IFunction func = SyntaxHandler.parseFunctionCall(str, parser);
		return func.getReturnValue();
	}
	
	public static IVariable getValue(String str, DynamicParser parser) throws IRuntimeException {
		IVariable variable = new IVariable("var", Scope.PRIVATE);
		variable.setValue(null);
		
		if(isInteger(str, parser.getBlock().getVariables())) {
			variable.setValue(getInteger(str, parser.getBlock().getVariables()));
		} else if(isDecimal(str, parser.getBlock().getVariables())) {
			variable.setValue(getDecimal(str, parser.getBlock().getVariables()));
		} else if(isBoolean(str, parser)) {
			variable.setValue(getBoolean(str, parser));
		} else if(isString(str)) {
			variable.setValue(getString(str));
		} else if(parser.getBlock().getVariables().stream()
				.filter(b -> b.getName().equals(str)).findAny().isPresent()) {
			IVariable b = parser.getBlock().getVariables().stream()
					.filter(bl -> bl.getName().equals(str)).findAny().get();
			variable = b;
		} else if(isRawList(str)) {
			variable.setValue(getRawList(str, parser));
		} else if(isFunctionCall(str, parser)) {
			variable.setValue(getFunctionCall(str, parser));
		} else if(isRawHash(str)) {
			variable.setValue(getRawHash(str, parser));
		} else if(str.startsWith("new")) {
			String inst = str.replaceFirst("new", "").trim();
			
			if(parser.getBlock().getSubblocks().stream()
					.filter(b -> b.getName().equals(inst)).findAny().isPresent()) {
				SyntaxBlock bl = parser.getBlock().getSubblocks().stream()
						.filter(b -> b.getName().equals(inst)).findAny().get();
				if(bl instanceof IObject) {
					IObject obje = (IObject) bl;
					variable = instantiate(obje, variable);
				}
			} else {
				throw new IRuntimeException("Type specified does not exist.");
			}
		}
		
		return variable;
	}
	
	private static IVariable instantiate(IObject obj, IVariable variable) {
		for(IVariable var : obj.getVariables()) {
			variable.getSubVars().add(var);
		}
		
		return variable;
	}
	
	public static IObject getType(String str, SyntaxBlock block) throws IRuntimeException {
		if(isInteger(str, block.getVariables())) {
			return INTEGER;
		} else if(isDecimal(str, block.getVariables())) {
			return DECIMAL;
		} else if(isBoolean(str)) {
			return BOOLEAN;
		} else if(isString(str)) {
			return STRING;
		} else if(block.getVariables().stream()
				.filter(b -> b.getName().equals(str)).findAny().isPresent()) {
			IVariable b = block.getVariables().stream()
					.filter(bl -> bl.getName().equals(str)).findAny().get();
			return b.getType();
		} else if(isRawList(str)) {
			return TUPLE;
		} else if(block.getSubblocks()
				.stream().filter(b -> b.getName()
						.equals(str.split(" ")[0]))
				.findAny().isPresent()) {
			IFunction func = SyntaxHandler.parseFunction(str, block.getParser());
			return getType(func.getReturnValue().toString(), block);
		} else if(isString("\"" + str + "\"")) {
			return STRING;
		} else if(isRawHash(str)) {
			return HASH;
		}
		
		return OBJECT;
	}
	
	public static IObject getType(String str) throws IRuntimeException {
		if(isInteger(str)) {
			return INTEGER;
		} else if(isDecimal(str)) {
			return DECIMAL;
		} else if(isBoolean(str)) {
			return BOOLEAN;
		} else if(isString(str)) {
			return STRING;
		} else if(isRawList(str)) {
			return TUPLE;
		} else if(isRawHash(str)) {
			return HASH;
		}
		
		return OBJECT;
	}
	
	public static boolean isValid(String pVal, IObject type, SyntaxBlock block) throws IRuntimeException {
		if(type == ValueHandler.BOOLEAN) {
			return isBoolean(pVal);
		} else if(type == ValueHandler.DECIMAL) {
			return isDecimal(pVal);
		} else if(type == ValueHandler.INTEGER) {
			return isInteger(pVal);
		} else if(type == ValueHandler.TUPLE) {
			return isList(pVal);
		} else if(type == ValueHandler.HASH) {
			return isMap(pVal);
		} else if(type == ValueHandler.STRING) {
			return isString("\"" + pVal + "\"");
		} else if(type == ValueHandler.OBJECT) {
			return true;
		} else {
			return false;
		}
	}
}
