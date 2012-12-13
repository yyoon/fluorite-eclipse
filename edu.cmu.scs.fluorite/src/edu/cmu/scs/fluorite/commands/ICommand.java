package edu.cmu.scs.fluorite.commands;

import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface ICommand {
	public boolean execute(IEditorPart target);

	public void dump();

	public String persist();

	public void persist(Document doc, Element commandElement);

	public void createFrom(Element commandElement);

	public Map<String, String> getAttributesMap();

	public Map<String, String> getDataMap();

	public String getCommandType();

	public String getName();

	public String getDescription();

	public String getCategory();

	public String getCategoryID();

	public void setTimestamp(long timestamp);

	public long getTimestamp();

	public void setTimestamp2(long timestamp);

	public long getTimestamp2();

	public void increaseRepeatCount();

	public int getRepeatCount();

	public int getCommandIndex();
	
	public boolean areTopBottomLinesRecorded();
	
	public int getTopLineNumber();
	
	public int getBottomLineNumber();

	public boolean combineWith(ICommand anotherCommand);

	public String getCommandTag();
}
