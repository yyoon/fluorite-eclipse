package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;


import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.dialogs.AddAnnotationDialog;
import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;



public class AnnotateCommand extends AbstractCommand {
	
	public AnnotateCommand(int id, String comment) {
		mId = id;
		mComment = comment;
	}
	
	private int mId;
	private String mComment;

	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub
		
	}

	public void persist(Document doc, Element commandElement) {
		Map<String, String> attrMap = new HashMap<String, String>();
		String selectionString = getSelectionString();
		attrMap.put("selection", selectionString);
		
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("comment", mComment);
		
		Utilities.persistCommand(doc, commandElement, "Annotation", attrMap, dataMap, this);
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

	public AbstractCommand createFrom(Element commandElement) {
		// TODO Auto-generated method stub
		return null;
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
