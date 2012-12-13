package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.dialogs.AddAnnotationDialog;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class AnnotateCommand extends AbstractCommand {
	
	public AnnotateCommand() {
	}

	public AnnotateCommand(int id, String comment) {
		mId = id;
		mComment = comment;
	}

	private int mId;
	private String mComment;

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getmComment() {
		return mComment;
	}

	public void setmComment(String mComment) {
		this.mComment = mComment;
	}

	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		String selectionString = getSelectionString();
		attrMap.put("selection", selectionString);
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("comment", mComment);
		return dataMap;
	}

	@Override
	public void createFrom(Element commandElement) {
		throw new RuntimeException("not implemented");
	}

	public String getCommandType() {
		return "Annotation";
	}

	private String getSelectionString() {
		String selectionString = "Other";
		switch (mId) {
		case AddAnnotationDialog.BACKTRACKING:
			selectionString = "Backtracking";
			break;

		case AddAnnotationDialog.WRITING_NEW_CODE:
			selectionString = "WritingNewCode";
			break;

		case AddAnnotationDialog.CANCEL:
			selectionString = "Cancel";
		}
		return selectionString;
	}

	public String getName() {
		return "Annotation (" + getSelectionString() + "): " + mComment;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategory() {
		return EventRecorder.AnnotationCategory;
	}

	public String getCategoryID() {
		return EventRecorder.AnnotationCategoryID;
	}

	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

}
