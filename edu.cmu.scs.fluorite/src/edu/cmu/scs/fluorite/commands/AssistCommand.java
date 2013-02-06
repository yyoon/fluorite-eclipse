package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	
	public AssistCommand() {
	}

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

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		String value = null;
		NodeList nodeList = null;
		
		if ((attr = commandElement.getAttributeNode("assist_type")) != null) {
			try {
				mAssistType = AssistType.valueOf(attr.getValue());
			} catch (Exception e) {
				mAssistType = AssistType.CONTENT_ASSIST;
			}
		}
		else {
			mAssistType = AssistType.CONTENT_ASSIST;
		}
		
		if ((attr = commandElement.getAttributeNode("start_end")) != null) {
			try {
				mStartEndType = StartEndType.valueOf(attr.getValue());
			} catch (Exception e) {
				mStartEndType = StartEndType.START;
			}
		}
		else {
			mStartEndType = StartEndType.START;
		}
		
		if ((attr = commandElement.getAttributeNode("auto_activated")) != null) {
			mAutoActivated = Boolean.parseBoolean(attr.getValue());
		}
		else {
			mAutoActivated = false;
		}
		
		if ((nodeList = commandElement.getElementsByTagName("context")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			value = textNode.getTextContent();
			mContext = value.equals("null") ? null : value;
		}
		else {
			mContext = null;
		}
	}

	public String getCommandType() {
		return "AssistCommand";
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
