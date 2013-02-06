package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class MouseWheelCommand extends AbstractCommand {
	
	public MouseWheelCommand() {
	}

	public MouseWheelCommand(int wheelValue) {
		mWheelValue = wheelValue;
	}

	private int mWheelValue;

	@Override
	public boolean execute(IEditorPart target) {
		return false;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("wheelValue", Integer.toString(mWheelValue));
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("wheelValue")) != null) {
			mWheelValue = Integer.parseInt(attr.getValue());
		}
	}

	@Override
	public String getCommandType() {
		return "MouseWheelCommand";
	}

	@Override
	public String getName() {
		return "Mouse Wheel (wheel value: " + mWheelValue + ")";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		return EventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		return EventRecorder.MacroCommandCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		if (!(anotherCommand instanceof MouseWheelCommand)) {
			return false;
		}

		MouseWheelCommand nextCommand = (MouseWheelCommand) anotherCommand;

		if (nextCommand.mWheelValue < 0 && mWheelValue < 0
				|| nextCommand.mWheelValue > 0 && mWheelValue > 0) {
			mWheelValue += nextCommand.mWheelValue;
			return true;
		}

		return false;
	}

}
