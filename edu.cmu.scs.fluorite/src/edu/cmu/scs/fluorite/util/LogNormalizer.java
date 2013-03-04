package edu.cmu.scs.fluorite.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author YoungSeok Yoon
 * 
 * LogNormalizer takes an existing log file and normalize it in the following
 * ways.
 * 
 * 1) Replace all the CR ('\r') character in a CDATA section into the following
 * string: "]]>&#13;<![CDATA[". In other words, it pulls all the CRs out of
 * CDATA sections and encodes them.
 * 
 * 2) If the XML is not properly closed, close it appropriately.
 *
 */
public class LogNormalizer {
	
	private static final String CDATA_START = "<![CDATA[";
	private static final String CDATA_END = "]]>";
	private static final String REPLACEMENT = CDATA_END + "&#13;" + CDATA_START;
	
	private static final String OPENING_TAG = "<Events";
	private static final String CLOSING_TAG = "</Events>";
	private static final String LINE_SEPERATOR = System.getProperty("line.separator");
	
	public static boolean normalizeLog(String logPath) {
		String content = Utilities.readFile(logPath);
		if (content == null)
			return false;
		
		content = normalizeContent(content);
		
		return writeFile(logPath, content);
	}
	
	public static String getNormalizedContentFromLog(String logPath) {
		return normalizeContent(Utilities.readFile(logPath));
	}

	public static String normalizeContent(String content) {
		if (content.trim().length() == 0) {
			return null;
		}
		
		String result = normalizeCarriageReturns(content);
		result = fixClosingTag(result);
		return result;
	}
	
	private static String normalizeCarriageReturns(String content) {
		StringBuffer buf = new StringBuffer(content);
		
		int pos = 0;
		int index = -1;
		
		while ((index = buf.indexOf(CDATA_START, pos)) != -1) {
			int start = index + CDATA_START.length();
			int end = buf.indexOf(CDATA_END, start);
			
			String replacement = buf.substring(start, end).replaceAll("\\r",
					REPLACEMENT);
			buf.replace(start, end, replacement);
			
			pos = start + replacement.length() + CDATA_END.length();
		}
		
		return buf.toString();
	}
	
	private static String fixClosingTag(String content) {
		String trimmed = content.trim();
		
		if (trimmed.startsWith(OPENING_TAG) && !trimmed.endsWith(CLOSING_TAG)) {
			String tail = LINE_SEPERATOR + CLOSING_TAG + LINE_SEPERATOR;
			return trimmed + tail;
		}
		else {
			// Do nothing.
			return content;
		}
	}
	
	static boolean writeFile(String filePath, String content) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(filePath), LogReader.CHARSET);
			
			writer.write(content);
			writer.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
