package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;


import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;



public class MoveCaretCommand extends AbstractCommand {
	public static final String XML_MoveCaret_Type="MoveCaretCommand";
	
	private int mCaretOffset;
	private int mDocOffset;
	
	public MoveCaretCommand(int caretOffset, int docOffset) {
		mCaretOffset = caretOffset;
		mDocOffset = docOffset;
	}

	public boolean execute(IEditorPart target) {
		StyledText styledText = Utilities.getStyledText(target);
		if (styledText == null) {
			return false;
		}

		styledText.setCaretOffset(this.mCaretOffset);
		return true;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public void persist(Document doc, Element commandElement) {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("caretOffset", Integer.toString(mCaretOffset));
		attrMap.put("docOffset", Integer.toString(mDocOffset));
		
		Utilities.persistCommand(doc, commandElement, XML_MoveCaret_Type, attrMap, null, this);
	}

	public AbstractCommand createFrom(Element commandElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return "Move Caret (caret offset: " + mCaretOffset + ", document offset: " + mDocOffset + ")";
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
