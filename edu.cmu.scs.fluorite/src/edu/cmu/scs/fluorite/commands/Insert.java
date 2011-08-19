package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;



public class Insert extends BaseDocumentChangeEvent {
	public static final String XML_Insert_Type="Insert";
	
	private int mOffset;
	private int mLength;
	
	private String mText;
	
	public Insert(int offset, String text, IDocument doc) {
		mOffset = offset;
		mLength = text.length();
		
		mText = text;
		
		calcNumericalValues(doc.get());
	}
	
	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public void persist(Document doc, Element commandElement) {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("offset", Integer.toString(mOffset));
		attrMap.put("length", Integer.toString(mLength));
		
		for (Map.Entry<String, Integer> pair : getNumericalValues().entrySet()) {
			attrMap.put(pair.getKey(), Integer.toString(pair.getValue()));
		}
		
		Map<String, String> dataMap = new HashMap<String, String>();
		if (getText() != null) {
			dataMap.put("text", getText());
		}
		
		Utilities.persistCommand(doc, commandElement, XML_Insert_Type, attrMap, dataMap, this);
	}

	public AbstractCommand createFrom(Element commandElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return "Insert text (offset: " + Integer.toString(mOffset)
			+ ", length: " + Integer.toString(mLength)
			+ ")";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategory() {
		return EventRecorder.DocumentChangeCategory;
	}

	public String getCategoryID() {
		return EventRecorder.DocumentChangeCategoryID;
	}
	
	public void setOffset(int offset) {
		mOffset = offset;
	}
	
	public int getOffset() {
		return mOffset;
	}
	
	public void setLength(int length) {
		mLength = length;
	}
	
	public int getLength() {
		return mLength;
	}
	
	public String getText() {
		return mText;
	}
	
	public boolean combine(ICommand anotherCommand) {
		if (!(anotherCommand instanceof Insert)) {
			return false;
		}

		Insert nextCommand = (Insert)anotherCommand;
		
		if (nextCommand.getOffset() < getOffset() || nextCommand.getOffset() > getOffset() + getLength()) {
			return false;
		}
		
		if (nextCommand.getText() == null && getText() != null ||
			nextCommand.getText() != null && getText() == null) {
			return false;
		}
		
		if (getText() != null) {
			mText = getText().substring(0, nextCommand.getOffset() - getOffset()) + nextCommand.getText() + getText().substring(nextCommand.getOffset() - getOffset());
		}
		
		mLength += nextCommand.getLength();
		
		replaceNumericalValues(nextCommand);
		
		return true;
	}
}
