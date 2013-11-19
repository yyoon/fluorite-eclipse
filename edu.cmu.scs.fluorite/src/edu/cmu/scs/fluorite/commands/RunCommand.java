package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class RunCommand extends AbstractCommand {

	public RunCommand() {
	}
	
	public RunCommand(boolean debug, boolean terminate, String projectName) {
		mDebug = debug;
		mTerminate = terminate;
		mProjectName = projectName;
	}

	private boolean mDebug;
	private boolean mTerminate;
	private String mProjectName;

	public boolean execute(IEditorPart target) {
		return false;
	}

	public void dump() {
	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", mDebug ? "Debug" : "Run");
		attrMap.put("kind", mTerminate ? "Terminate" : "Create");
		attrMap.put("projectName", mProjectName == null ? "(Unknown)"
				: mProjectName);
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		String value = null;
		
		if ((attr = commandElement.getAttributeNode("type")) != null) {
			mDebug = attr.getValue().equals("Debug");
		}
		else {
			mDebug = false;
		}
		
		if ((attr = commandElement.getAttributeNode("kind")) != null) {
			mTerminate = attr.getValue().equals("Terminate");
		}
		else {
			mTerminate = false;
		}
		
		if ((attr = commandElement.getAttributeNode("projectName")) != null) {
			value = attr.getValue();
			mProjectName = value.equals("(Unknown)") ? null : value;
		}
		else {
			mProjectName = null;
		}
	}

	public String getCommandType() {
		return "RunCommand";
	}

	public String getName() {
		return (mTerminate ? "Terminate" : "Create") + " "
				+ (mDebug ? "Debug" : "Run") + " Application";
	}

	public String getDescription() {
		return "Run: " + mProjectName + (mDebug ? " (Debug)" : "");
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
