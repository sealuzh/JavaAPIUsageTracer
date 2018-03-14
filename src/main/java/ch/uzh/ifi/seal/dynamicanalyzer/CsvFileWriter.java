package ch.uzh.ifi.seal.dynamicanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class CsvFileWriter {
	
	private String filename = null;
	private String libname = null;
	private String projectname = null;
	private Map<String, Integer> cache;
	
	private CsvFileWriter(String filename) {
		this.filename = filename;
		this.cache = new HashMap<String, Integer>();
	}
	
	public CsvFileWriter(String filename, String libname, String projectname) {
		this(filename);
		this.libname = libname;
		this.projectname = projectname;
	}
	
	public static CsvFileWriter defrost(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		String line = reader.readLine(); // skip header
		CsvFileWriter obj = new CsvFileWriter(filename);
		while((line = reader.readLine()) != null) {
			String[] tokens = line.split(";");
			if(obj.libname == null)
				obj.libname = tokens[0];
			if(obj.projectname== null)
				obj.projectname = tokens[1];
			obj.cache.put(tokens[2], Integer.parseInt(tokens[3]));
		}
		reader.close();
		return obj;
	}
	
	public synchronized void addInvocationEntry(String fullMethodName) {
		
		if(this.cache.containsKey(fullMethodName)) {
			int prev = this.cache.get(fullMethodName);
			this.cache.put(fullMethodName, ++prev);
		} else {
			this.cache.put(fullMethodName, 1);
		}
		
	}
	
	public synchronized void flushToFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writeFileHeader(writer);
		for(String key : cache.keySet()) {
			writeEntry(writer, this.libname, this.projectname, key, cache.get(key));
		}
		writer.flush();
		writer.close();
	}
	
	private void writeEntry(BufferedWriter writer, String lib, String project,
			String method, int val) throws IOException {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%s;%s;%s;%d\n", lib, project, method, val);
		writer.write(sb.toString());
		formatter.close();
	}
	
	private void writeFileHeader(BufferedWriter writer) throws IOException {
		String header = "LIB;PROJECT;METHOD;COUNT\n";
		writer.write(header);
	}

}
