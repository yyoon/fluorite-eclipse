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

public class Replace extends BaseDocumentChangeEvent {

	public Replace() {
	}
	
	@SuppressWarnings("unchecked")
	public Replace(int offset, int length, int startLine, int endLine,
			int insertionLength, String deletedText, String insertedText,
			IDocument document) {
		mOffset = offset;
		mLength = length;
		mStartLine = startLine;
		mEndLine = endLine;
		mInsertionLength = insertionLength;

		mDeletedText = deletedText;
		mInsertedText = insertedText;
		
		if (document != null) {

			StringBuffer documentContent = new StringBuffer(document.get());
			documentContent.replace(offset, offset + length, "");
			calcNumericalValues(documentContent.toString());

			mIntermediateNumericalValues = (Map<String, Integer>) ((HashMap<String, Integer>) getNumericalValues())
					.clone();

			documentContent.replace(offset, offset, insertedText);
			calcNumericalValues(documentContent.toString());

		}
	}

	private int mOffset;
	private int mLength;
	private int mStartLine;
	private int mEndLine;
	private int mInsertionLength;

	private String mDeletedText;
	private String mInsertedText;

	private Map<String, Integer> mIntermediateNumericalValues;

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
		attrMap.put("startLine", Integer.toString(mStartLine));
		attrMap.put("endLine", Integer.toString(mEndLine));
		attrMap.put("insertionLength", Integer.toString(mInsertionLength));

		for (Map.Entry<String, Integer> pair : getNumericalValues().entrySet()) {
			attrMap.put(pair.getKey(), Integer.toString(pair.getValue()));
		}

		for (Map.Entry<String, Integer> pair : mIntermediateNumericalValues
				.entrySet()) {
			attrMap.put("int_" + pair.getKey(),
					Integer.toString(pair.getValue()));
		}

		return attrMap;
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (getDeletedText() != null) {
			dataMap.put("deletedText", getDeletedText());
		}
		if (getInsertedText() != null) {
			dataMap.put("insertedText", getInsertedText());
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
		
		if ((attr = commandElement.getAttributeNode("startLine")) != null) {
			mStartLine = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("endLine")) != null) {
			mEndLine = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("insertionLength")) != null) {
			mInsertionLength = Integer.parseInt(attr.getValue());
		}
		
		if ((nodeList = commandElement.getElementsByTagName("deletedText")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mDeletedText = normalizeText(textNode.getTextContent(), mLength);
		}
		
		if ((nodeList = commandElement.getElementsByTagName("insertedText")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mInsertedText = normalizeText(textNode.getTextContent(), mInsertionLength);
		}
		
		mIntermediateNumericalValues = new HashMap<String, Integer>();
		
		if ((attr = commandElement.getAttributeNode("int_docLength")) != null) {
			mIntermediateNumericalValues.put("docLength", Integer.parseInt(attr.getValue()));
		}
		
		if ((attr = commandElement.getAttributeNode("int_docActiveCodeLength")) != null) {
			mIntermediateNumericalValues.put("docActiveCodeLength", Integer.parseInt(attr.getValue()));
		}
		
		if ((attr = commandElement.getAttributeNode("int_docASTNodeCount")) != null) {
			mIntermediateNumericalValues.put("docASTNodeCount", Integer.parseInt(attr.getValue()));
		}
		
		if ((attr = commandElement.getAttributeNode("int_docExpressionCount")) != null) {
			mIntermediateNumericalValues.put("docExpressionCount", Integer.parseInt(attr.getValue()));
		}
		
		if (mIntermediateNumericalValues.isEmpty()) {
			mIntermediateNumericalValues = null;
		}
	}

	public String getCommandType() {
		return "Replace";
	}

	public String getName() {
		return "Replace text (offset: " + Integer.toString(mOffset)
				+ ", length: " + Integer.toString(mLength) + ", startLine: "
				+ Integer.toString(mStartLine) + ", endLine: "
				+ Integer.toString(mEndLine) + ")";
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

	public int getOffset() {
		return mOffset;
	}

	public void setOffset(int offset) {
		this.mOffset = offset;
	}

	public int getLength() {
		return mLength;
	}

	public void setLength(int length) {
		this.mLength = length;
	}

	public int getStartLine() {
		return mStartLine;
	}

	public void setStartLine(int startLine) {
		this.mStartLine = startLine;
	}

	public int getEndLine() {
		return mEndLine;
	}

	public void setEndLine(int endLine) {
		this.mEndLine = endLine;
	}

	public String getDeletedText() {
		return mDeletedText;
	}

	public void setDeletedText(String deletedText) {
		this.mDeletedText = deletedText;
	}

	public String getInsertedText() {
		return mInsertedText;
	}

	public void setInsertedText(String insertedText) {
		this.mInsertedText = insertedText;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		return false;
	}

	public int getInsertionLength() {
		return mInsertionLength;
	}

	@Override
	public void applyToDocument(IDocument doc) {
		try {
			doc.replace(getOffset(), getLength(), getInsertedText());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
