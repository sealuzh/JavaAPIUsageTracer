package ch.uzh.ifi.seal.dynamicanalyzer;

import java.io.IOException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class LogAspect {
	
	private CsvFileAppender writer;
	
	public LogAspect() throws IOException {
		writer = CsvFileAppender.getInstance();
	}
	
	@Before("execution(public * *.*(..))")
	public void interceptAllCalls(JoinPoint jp) throws Throwable {
		
		String fullname = jp.getSignature().toLongString();
//		String[] splitted = fullname.split("\\.");
//		String method = splitted[splitted.length - 1];
//		String fqClassname =  getFQN(splitted);
		
		writer.addInvocationEntry(fullname);
		
//		writer.flushToFile();
		
	}
	
//	private String getFQN(String[] parts) {
//		StringBuilder sb = new StringBuilder();
//		for(int i = 0; i<parts.length-1; i++)
//			sb.append(parts[i]);
//		return sb.toString();
//	}
	
	
}
