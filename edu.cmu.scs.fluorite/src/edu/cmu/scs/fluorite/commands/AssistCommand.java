package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class AssistCommand extends AbstractCommand {

	public enum AssistType {
		CONTENT_ASSIST, QUICK_ASSIST,
	}

	public enum StartEndType {
		START, END,
	}

	private AssistType mAssistType;
	private StartEndType mStartEndType;
	private boolean mAutoActivated;
	private String mContext;

	public AssistCommand(AssistType assistType, StartEndType startEndType,
			boolean autoActivated, String context) {
		mAssistType = assistType;
		mStartEndType = startEndType;
		mAutoActivated = (mStartEndType == StartEndType.END) ? false
				: autoActivated;
		mContext = context;
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
		attrMap.put("assist_type", mAssistType.toString());
		attrMap.put("start_end", mStartEndType.toString());
		attrMap.put("auto_activated", Boolean.toString(mAutoActivated));
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (mContext != null) {
			dataMap.put("context", mContext);
		}
		return dataMap;
	}

	public String getCommandType() {
		return "AssistCommand";
	}

	public ICommand createFrom(Element commandElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return mAssistType.toString() + " " + mStartEndType.toString()
				+ ", AutoActivated: " + Boolean.toString(mAutoActivated)
				+ ", Context: " + mContext;
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

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

}
