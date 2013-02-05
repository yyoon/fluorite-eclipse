package edu.cmu.scs.fluorite.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

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
		String content = readFile(logPath);
		if (content == null)
			return false;
		
		content = normalizeContent(content);
		
		return writeFile(logPath, content);
	}
	
	public static String getNormalizedContentFromLog(String logPath) {
		return normalizeContent(readFile(logPath));
	}

	public static String normalizeContent(String content) {
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
	
	static String readFile(String filePath) {
		try {
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filePath), LogReader.CHARSET);

			StringBuffer buf = new StringBuffer();

			while (reader.ready()) {
				char[] charBuf = new char[1024];
				int count = reader.read(charBuf, 0, charBuf.length);

				buf.append(new String(charBuf, 0, count));
			}

			reader.close();
			
			return buf.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
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
