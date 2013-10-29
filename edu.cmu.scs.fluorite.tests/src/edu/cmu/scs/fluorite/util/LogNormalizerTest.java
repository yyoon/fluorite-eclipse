package edu.cmu.scs.fluorite.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogNormalizerTest {
	
	private static final String FILE_PATH = "data/NormalizeTest.txt";

	@Test
	public void testNormalizeCarraigeReturn() {
		String content = Utilities.readFile(FILE_PATH);
		
		assertEquals("Hello world!!\r\n" +
				"\r\n" +
				"\r\n" +
				"<![CDATA[asldkjfalskjdf\r\n" +
				"\r" +
				"]]>\r" +
				"lkjsldkfj\r\n",
				content);
		
		String normalized = LogNormalizer.normalizeContent(content);
		
		assertEquals("Hello world!!\r\n" +
				"\r\n" +
				"\r\n" +
				"<![CDATA[asldkjfalskjdf]]>&#13;<![CDATA[\n" +
				"]]>&#13;<![CDATA[" +
				"]]>\r" +
				"lkjsldkfj\r\n",
				normalized);
	}
	
	@Test
	public void testClosingTag() {
		String content = "<Events something=\"something\">\r\n" +
				"something something";
		
		String normalized = LogNormalizer.normalizeContent(content);
		String lineSeparator = System.getProperty("line.separator");
		
		assertEquals(content + lineSeparator + "</Events>" + lineSeparator,
				normalized);
	}

}
