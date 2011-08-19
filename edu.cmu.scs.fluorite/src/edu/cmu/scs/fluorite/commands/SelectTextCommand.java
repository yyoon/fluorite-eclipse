package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;



public class SelectTextCommand extends AbstractCommand {
	public static final String XML_SelectText_Type = "SelectTextCommand";
	
	private int mStart;
	private int mEnd;
	private int mCaretOffset;
	
	public SelectTextCommand(int start, int end, int caretOffset) {
		mStart = start;
		mEnd = end;
		mCaretOffset = caretOffset;
	}

	public boolean execute(IEditorPart target) {
		StyledText styledText = Utilities.getStyledText(target);
		if (styledText == null) { return false; }

		if (mStart == mCaretOffset) {
			styledText.setSelection(mEnd, mStart);
		}
		else {
			styledText.setSelection(mStart, mEnd);
		}
		
		return true;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public void persist(Document doc, Element commandElement) {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("start", Integer.toString(mStart));
		attrMap.put("end", Integer.toString(mEnd));
		attrMap.put("caretOffset", Integer.toString(mCaretOffset));
		
		Utilities.persistCommand(doc, commandElement, XML_SelectText_Type, attrMap, null, this);
	}

	public AbstractCommand createFrom(Element commandElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return "Select Text (" + mStart + ", " + mEnd + ", " + mCaretOffset + ")";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategory() {
		return EventRecorder.MacroCommandCategory;
	}

	public String getCategoryID() {
		return EventRecorder.MacroCommandCategoryID;
	}

	public boolean combine(ICommand anotherCommand) {
		return false;
	}
}
