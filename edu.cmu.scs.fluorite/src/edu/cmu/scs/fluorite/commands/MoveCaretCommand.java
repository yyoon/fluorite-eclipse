package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;

public class MoveCaretCommand extends AbstractCommand {
	
	public MoveCaretCommand() {
	}

	public MoveCaretCommand(int caretOffset, int docOffset) {
		mCaretOffset = caretOffset;
		mDocOffset = docOffset;
	}

	private int mCaretOffset;
	private int mDocOffset;

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

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("caretOffset", Integer.toString(mCaretOffset));
		attrMap.put("docOffset", Integer.toString(mDocOffset));
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public void createFrom(Element commandElement) {
		throw new RuntimeException("not implemented");
	}

	public String getCommandType() {
		return "MoveCaretCommand";
	}

	public String getName() {
		return "Move Caret (caret offset: " + mCaretOffset
				+ ", document offset: " + mDocOffset + ")";
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
