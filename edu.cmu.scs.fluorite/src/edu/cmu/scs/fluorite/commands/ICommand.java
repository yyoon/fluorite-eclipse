package edu.cmu.scs.fluorite.commands;

import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface ICommand {
	boolean execute(IEditorPart target);

	void dump();

	String persist();

	void persist(Document doc, Element commandElement);

	void createFrom(Element commandElement);

	Map<String, String> getAttributesMap();

	Map<String, String> getDataMap();

	String getCommandType();

	String getName();

	String getDescription();

	String getCategory();

	String getCategoryID();

	void setTimestamp(long timestamp);

	long getTimestamp();

	void setTimestamp2(long timestamp);

	long getTimestamp2();

	void increaseRepeatCount();

	int getRepeatCount();
	
	long getSessionId();
	
	void setSessionId(long sessionId);

	int getCommandIndex();
	
	void setCommandIndex(int id);
	
	boolean areTopBottomLinesRecorded();
	
	int getTopLineNumber();
	
	int getBottomLineNumber();

	boolean combineWith(ICommand anotherCommand);

	String getCommandTag();
}
