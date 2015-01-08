package edu.cmu.scs.fluorite.commands.document;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class Insert extends DocChange {

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
		return false;
	}

	public void dump() {
	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("offset", Integer.toString(mOffset));
		attrMap.put("length", Integer.toString(mLength));

		if (getNumericalValues() != null) {
			for (Map.Entry<String, Integer> pair : getNumericalValues().entrySet()) {
				attrMap.put(pair.getKey(), Integer.toString(pair.getValue()));
			}
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
			mText = checkTextValidity(textNode.getTextContent(), mLength);
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
		return null;
	}

	public String getCategory() {
		return EventRecorder.DocumentChangeCategory;
	}

	public String getCategoryID() {
		return EventRecorder.DocumentChangeCategoryID;
	}

	public int getOffset() {
		return mOffset;
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
		
		// Do not merge multiple lines of edits.
		if (getText().contains("\r") || getText().contains("\n")
				|| nextCommand.getText().contains("\r") || nextCommand.getText().contains("\n")) {
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
	public void apply(IDocument doc) {
		try {
			doc.replace(getOffset(), 0, getText());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String apply(String original) {
		try {
			return original.substring(0, getOffset()) + getText()
					+ original.substring(getOffset());
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		return original;
	}
	
	@Override
	public void apply(StringBuilder builder) {
		try {
			builder.insert(getOffset(), getText());
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Range apply(Range range) {
		if (!range.contains(getOffset())) {
			throw new IllegalArgumentException();
		}
		
		return new Range(range.getOffset(), range.getOffset() + getLength());
	}

	@Override
	public void applyInverse(IDocument doc) {
		try {
			doc.replace(getOffset(), getLength(), "");
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String applyInverse(String original) {
		try {
			return original.substring(0, getOffset()) + original.substring(getOffset() + getLength());
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		return original;
	}

	@Override
	public void applyInverse(StringBuilder builder) {
		try {
			builder.delete(getOffset(), getOffset() + getLength());
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Range applyInverse(Range range) {
		if (!range.contains(getInsertionRange())) {
			throw new IllegalArgumentException();
		}
		
		return new Range(range.getOffset(), range.getLength() - getLength());
	}

	@Override
	public double getY1() {
		if (getNumericalValues() != null && getNumericalValues().containsKey("docLength")) {
			return 100.0 * getOffset() / getNumericalValues().get("docLength"); 
		}
		
		return 0;
	}

	@Override
	public double getY2() {
		if (getNumericalValues() != null && getNumericalValues().containsKey("docLength")) {
			return 100.0 * (getOffset() + getLength()) / getNumericalValues().get("docLength"); 
		}
		
		return 100;
	}

	@Override
	public Range getDeletionRange() {
		if (mDeletionRange == null) {
			mDeletionRange = new Range(getOffset(), 0);
		}
		
		return mDeletionRange;
	}

	@Override
	public String getDeletedText() {
		return "";
	}

	@Override
	public Range getInsertionRange() {
		if (mInsertionRange == null) {
			mInsertionRange = new Range(getOffset(), getLength());
		}
		
		return mInsertionRange;
	}

	@Override
	public String getInsertedText() {
		return getText();
	}

	@Override
	public String toString() {
		return String.format("[Insert:%d]\n%s", getCommandIndex(), getText());
	}
	
}
