package edu.cmu.scs.fluorite.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.cmu.scs.fluorite.commands.FileOpenCommand;
import edu.cmu.scs.fluorite.commands.ICommand;

public class LogReaderTest {
	
	private LogReader reader;
	
	@Before
	public void setUp() {
		reader = new LogReader();
	}

	@Test
	public void testReadDocumentChanges() {
		List<ICommand> documentChanges =
				reader.readDocumentChanges("data/Log2012-12-04-03-51-21-084.xml");
		
		assertEquals(9, documentChanges.size());

		assertTrue(documentChanges.get(0) instanceof FileOpenCommand);
		FileOpenCommand foc = (FileOpenCommand) documentChanges.get(0);
		assertEquals("HelloWorld", foc.getProjectName());
		assertEquals(
				"D:\\Programming\\RuntimeWorkspaces\\runtime-Azurite\\HelloWorld\\src\\helloworld\\HelloWorld.java",
				foc.getFilePath());
		
		String expectedSnapshot = "package helloworld;\r\n"
				+ "\r\n"
				+ "public class HelloWorld {\r\n"
				+ "\r\n"
				+ "\t/**\r\n"
				+ "\t * @param args\r\n"
				+ "\t */\r\n"
				+ "\tpublic static void main(String[] args) {\r\n"
				+ "\t\tSystem.out.println(\"Hello, world!\");\r\n"
				+ "\t\tSystem.out.println(\"Hello, Charlie!\");\r\n"
				+ "\t}\r\n"
				+ "\t\r\n"
				+ "\tpublic void foo() {\r\n"
				+ "\t\t// Just a test method..\r\n"
				+ "\t\t// \r\n"
				+ "\t}\r\n"
				+ "\r\n"
				+ "}\r\n"
				+ "";
		
		assertEquals(expectedSnapshot, foc.getSnapshot());
	}

}
