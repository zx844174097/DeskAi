package cn.net.mugui.log;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class DPrintStream extends PrintStream {

	public DPrintStream(OutputStream out) {
		super(out);
	}

	boolean isError = false;

	public DPrintStream(OutputStream out, int isError) {
		super(out);
		this.isError = (isError == 1);
	}

	public DPrintStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public DPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
	}

	public DPrintStream(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	public DPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	public DPrintStream(File file) throws FileNotFoundException {
		super(file);
	}

	public DPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}

	String name = this.getClass().getName();

	@Override
	public void print(String s) {
		println(s);
	}

	@Override
	public void print(Object obj) {
		println(obj);
	}

	@Override
	public void print(int i) {
		println(i);
	}

	@Override
	public void print(long l) {
		println(l);
	}

	@Override
	public void print(char c) {
		println(c);
	}

	@Override
	public void print(float f) {
		println(f);
	}

	@Override
	public void print(char[] s) {
		println(s);
	}

	@Override
	public void print(double d) {
		println(d);
	}

	@Override
	public void print(boolean b) {
		println(b);
	}

	@Override
	public void println(int x) {
		println(x + "");
	}

	@Override
	public void println(char x) {
		println(x + "");
	}

	@Override
	public void println(long x) {
		println(x + "");
	}

	@Override
	public void println(float x) {
		println(x + "");
	}

	@Override
	public void println(char[] x) {
		println(Arrays.toString(x));
	}

	@Override
	public void println(double x) {
		println(x + "");
	}

	@Override
	public void println(boolean x) {
		println(x + "");
	}

	@Override
	public void println() {
		println("");
	}

	private static ConcurrentHashMap<String, Log> concurrentHashMap = new ConcurrentHashMap<String, Log>();

	public void println(String s) {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		int i=0;
		String className =stackTrace[++i].getClassName();
		while(className.equals(DPrintStream.class.getName())){
			className=stackTrace[++i].getClassName();
		}
		Log log = concurrentHashMap.get(className);
		if (log == null) {
			log = LogFactory.getCurrentLogFactory().createLog(className);
			concurrentHashMap.put(className, log);
		}
		if (isError) {
			log.error(name, null, s);
		} else
			log.info(name, null, s);
	}

	@Override
	public void println(Object x) {
		println(String.valueOf(x));
	}

}