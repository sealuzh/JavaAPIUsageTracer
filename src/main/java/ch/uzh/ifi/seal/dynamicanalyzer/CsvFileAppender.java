package ch.uzh.ifi.seal.dynamicanalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;

public class CsvFileAppender {
	
	private String filename = System.getProperty("tracer.file");
	private String libname = System.getProperty("tracer.libname");
	private String projectname = System.getProperty("tracer.projectname");
	private BufferedWriter appender = null;
	
	private static CsvFileAppender instance = null;
	
	private CsvFileAppender() throws IOException {
		if(!new File(this.filename).exists()) {
			appender = new BufferedWriter(new FileWriter(filename));
			writeFileHeader(appender);
		} else {
			appender = new BufferedWriter(new FileWriter(filename, true));
		}
	}
	
	public synchronized static CsvFileAppender getInstance() throws IOException {
		if(instance == null)
			instance = new CsvFileAppender();
		return instance;
	}
	
	public synchronized void addInvocationEntry(String fullMethodName) throws IOException {
		
		writeEntry(appender, libname, projectname, fullMethodName);
		
	}
	
	private void writeEntry(BufferedWriter writer, String lib, String project,
			String method) throws IOException {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%s;%s;%s\n", lib, project, method);
		writer.write(sb.toString());
		formatter.close();
	}
	
	private void writeFileHeader(BufferedWriter writer) throws IOException {
		String header = "LIB;PROJECT;METHOD\n";
		writer.write(header);
		writer.flush();
	}

}
