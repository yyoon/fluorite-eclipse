package edu.cmu.scs.fluorite.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import edu.cmu.scs.fluorite.commands.AbstractCommand;
import edu.cmu.scs.fluorite.commands.AnnotateCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class LogReader {

	/**
	 * Initializes a new LogReader instance.
	 * Does nothing special.
	 */
	public LogReader() {
	}
	
	/**
	 * Reads the given log file and returns the list of deserialized commands.
	 * All commands will be included.
	 * @param logPath the file path to the log file.
	 * @return deserialized list of commands
	 */
	public List<ICommand> readAll(String logPath) {
		return readFilter(logPath, null);
	}
	
	/**
	 * Reads the given log file and returns the list of deserialized commands.
	 * Only the FileOpenCommand and all the DocumentChanges will be included.
	 * @param logPath the file path to the log file.
	 * @return deserialized list of commands
	 */
	public List<ICommand> readDocumentChanges(String logPath) {
		return readFilter(logPath, new IFilter() {
			@Override
			public boolean filter(Element element) {
				if (isCommandTyped(element, "FileOpenCommand") || isDocumentChange(element)) {
					return true;
				}

				return false;
			}
		});
	}
	
	/**
	 * Reads the given log file and returns the list of deserialized commands.
	 * Only the filtered commands will be included.
	 * @param logPath the file path to the log file.
	 * @return deserialized list of commands
	 * @throws DocumentException
	 */
	public List<ICommand> readFilter(String logPath, IFilter filter) {
		if (logPath == null) {
			throw new IllegalArgumentException();
		}
		
		List<ICommand> result = new ArrayList<ICommand>();

		Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(logPath);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        Element root = doc.getDocumentElement();

        boolean prevState = AbstractCommand.getIncrementCommandID();
        AbstractCommand.setIncrementCommandID(false);
        
        for ( Node node = root.getFirstChild(); node != null; node = node.getNextSibling() ) {
        	if (!(node instanceof Element)) { continue; }
        
        	Element child = (Element) node;
        	
        	if (filter == null || filter.filter(child)) {
        		result.add(parse(child));
        	}
        }
        
        AbstractCommand.setIncrementCommandID(prevState);
        
        return result;
	}
	
	private static boolean isCommandTyped(Element element, String typeName) {
		return isCommand(element) && isType(element, typeName);
	}
	
	private static boolean isType(Element element, String typeName) {
		Attr attr = element.getAttributeNode(EventRecorder.XML_CommandType_ATTR);
		return attr != null && attr.getValue().equals(typeName);
	}
	
	private static boolean isCommand(Element element) {
		return element.getTagName().equals(EventRecorder.XML_Command_Tag);
	}
	
	private static boolean isDocumentChange(Element element) {
		return element.getTagName().equals(EventRecorder.XML_DocumentChange_Tag);
	}
	
	private static boolean isAnnotation(Element element) {
		return element.getTagName().equals(EventRecorder.XML_Annotation_Tag);
	}

	private static ICommand parse(Element element) {
		if (isCommand(element) || isDocumentChange(element)) {
			String typeName = element.getAttribute(EventRecorder.XML_CommandType_ATTR);
			if (typeName == null) {
				throw new IllegalArgumentException();
			}
			
			Package commandsPackage = ICommand.class.getPackage();
			String fullyQualifiedName = commandsPackage.getName() + "." + typeName;
			
			try {
				Class<?> c = Class.forName(fullyQualifiedName);
				ICommand command = (ICommand)c.newInstance();
				command.createFrom(element);
				
				return command;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		else if (isAnnotation(element)) {
			AnnotateCommand annotateCommand = new AnnotateCommand();
			annotateCommand.createFrom(element);
			
			return annotateCommand;
		}
		else {
			throw new IllegalArgumentException();
		}
		
		return null;
	}
}
