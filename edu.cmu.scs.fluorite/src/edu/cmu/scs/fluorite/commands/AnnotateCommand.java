package edu.cmu.scs.fluorite.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class AnnotateCommand extends AbstractCommand implements ITimestampOverridable {

	// log version > 0.2.0
	public static final int CANCEL = Window.CANCEL;
	public static final int OTHER = Window.OK;
	public static final int BACKTRACKING = 2;
	public static final int WRITING_NEW_CODE = 3;
	public static final int TUNING_PARAMETERS = 4;
	public static final int LEARNING_API = 5;
	public static final int TRYING_OUT_UI_DESIGN = 6;
	public static final int CORRECTING_LOGIC = 7;
	public static final int TRYING_OUT_DIFFERENT_ALGORITHMS = 8;
	public static final int DEBUGGING = 9;
	public static final int TAG = 10;

	public static final String[] BUTTON_NAMES = {
		"Other",
		"Cancel",
		"Backtracking",
		"Writing new code",
		"Tuning parameters",
		"Learning API",
		"Trying out UI design",
		"Correcting Logic",
		"Trying out different algorithms",
		"Debugging",
		"Tag",
	};
	
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

	public String getComment() {
		return mComment;
	}

	public void setmComment(String mComment) {
		this.mComment = mComment;
	}

	public boolean execute(IEditorPart target) {
		return false;
	}

	public void dump() {
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
		super.createFrom(commandElement);
		
		Attr attr = null;
		String value = null;
		NodeList nodeList = null;
		
		if ((attr = commandElement.getAttributeNode("selection")) != null) {
			mId = Arrays.asList(BUTTON_NAMES).indexOf(attr.getValue());
		}
		else {
			mId = -1;
		}
		
		if ((nodeList = commandElement.getElementsByTagName("comment")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			value = textNode.getTextContent();
			mComment = value.equals("null") ? null : value;
		}
		else {
			mComment = null;
		}
	}

	public String getCommandType() {
		return "Annotation";
	}

	private String getSelectionString() {
		String selectionString = "Other";
		
		if (0 <= mId && mId <= BUTTON_NAMES.length) {
			selectionString = BUTTON_NAMES[mId];
		}
		
		return selectionString;
	}

	public String getName() {
		return "Annotation (" + getSelectionString() + "): " + mComment;
	}

	public String getDescription() {
		return getComment();
	}

	public String getCategory() {
		return EventRecorder.AnnotationCategory;
	}

	public String getCategoryID() {
		return EventRecorder.AnnotationCategoryID;
	}

	public boolean combine(ICommand anotherCommand) {
		return false;
	}

	@Override
	public long getTimestampForDisplay() {
		return getSessionId() + getTimestamp();
	}

}
