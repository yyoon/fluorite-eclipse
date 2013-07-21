package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;

public class SelectTextCommand extends AbstractCommand {

	public SelectTextCommand() {
	}
	
	public SelectTextCommand(int start, int end, int caretOffset,
			int docStart, int docEnd, int docOffset) {
		mStart = start;
		mEnd = end;
		mCaretOffset = caretOffset;
		
		mDocStart = docStart;
		mDocEnd = docEnd;
		mDocOffset = docOffset;
	}

	private int mStart;
	private int mEnd;
	private int mCaretOffset;
	
	private int mDocStart;
	private int mDocEnd;
	private int mDocOffset;

	public boolean execute(IEditorPart target) {
		StyledText styledText = Utilities.getStyledText(target);
		if (styledText == null) {
			return false;
		}

		if (mStart == mCaretOffset) {
			styledText.setSelection(mEnd, mStart);
		} else {
			styledText.setSelection(mStart, mEnd);
		}

		return true;
	}

	public void dump() {
	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("start", Integer.toString(mStart));
		attrMap.put("end", Integer.toString(mEnd));
		attrMap.put("caretOffset", Integer.toString(mCaretOffset));
		attrMap.put("docStart", Integer.toString(mDocStart));
		attrMap.put("docEnd", Integer.toString(mDocEnd));
		attrMap.put("docOffset", Integer.toString(mDocOffset));
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("start")) != null) {
			mStart = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("end")) != null) {
			mEnd = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("caretOffset")) != null) {
			mCaretOffset = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("docStart")) != null) {
			mDocStart = Integer.parseInt(attr.getValue());
		}
		else {
			mDocStart = -1;
		}
		
		if ((attr = commandElement.getAttributeNode("docEnd")) != null) {
			mDocEnd = Integer.parseInt(attr.getValue());
		}
		else {
			mDocEnd = -1;
		}
		
		if ((attr = commandElement.getAttributeNode("docOffset")) != null) {
			mDocOffset = Integer.parseInt(attr.getValue());
		}
		else {
			mDocOffset = -1;
		}
	}

	public String getCommandType() {
		return "SelectTextCommand";
	}

	public String getName() {
		return "Select Text (" + mStart + ", " + mEnd + ", " + mCaretOffset
				+ ", " + mDocStart + ", " + mDocEnd + ", " + mDocOffset + ")";
	}

	public String getDescription() {
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
