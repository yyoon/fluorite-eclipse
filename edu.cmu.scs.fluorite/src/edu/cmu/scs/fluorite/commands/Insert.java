package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class Insert extends BaseDocumentChangeEvent {

	public Insert() {
	}
	
	public Insert(int offset, String text, IDocument doc) {
		mOffset = offset;
		mLength = text.length();

		mText = text;

		if (doc != null) {
			calcNumericalValues(doc.get());
		}
	}

	private int mOffset;
	private int mLength;

	private String mText;

	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("offset", Integer.toString(mOffset));
		attrMap.put("length", Integer.toString(mLength));

		for (Map.Entry<String, Integer> pair : getNumericalValues().entrySet()) {
			attrMap.put(pair.getKey(), Integer.toString(pair.getValue()));
		}

		return attrMap;
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (getText() != null) {
			dataMap.put("text", getText());
		}

		return dataMap;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		NodeList nodeList = null;
		
		if ((attr = commandElement.getAttributeNode("offset")) != null) {
			mOffset = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("length")) != null) {
			mLength = Integer.parseInt(attr.getValue());
		}
		
		if ((nodeList = commandElement.getElementsByTagName("text")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mText = normalizeText(textNode.getTextContent(), mLength);
		}
	}

	public String getCommandType() {
		return "Insert";
	}

	public String getName() {
		return "Insert text (offset: " + Integer.toString(mOffset)
				+ ", length: " + Integer.toString(mLength) + ")";
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

		Insert nextCommand = (Insert) anotherCommand;

		if (nextCommand.getOffset() < getOffset()
				|| nextCommand.getOffset() > getOffset() + getLength()) {
			return false;
		}

		if (nextCommand.getText() == null && getText() != null
				|| nextCommand.getText() != null && getText() == null) {
			return false;
		}

		if (getText() != null) {
			mText = getText().substring(0,
					nextCommand.getOffset() - getOffset())
					+ nextCommand.getText()
					+ getText()
							.substring(nextCommand.getOffset() - getOffset());
		}

		mLength += nextCommand.getLength();

		replaceNumericalValues(nextCommand);

		return true;
	}

	@Override
	public void applyToDocument(IDocument doc) {
		try {
			doc.replace(getOffset(), 0, getText());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
