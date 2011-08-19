package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;


import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;



public class RunCommand extends AbstractCommand {
	
	private boolean mDebug;
	private boolean mTerminate;
	private String mProjectName;
	
	public RunCommand(boolean debug, boolean terminate, String projectName) {
		mDebug = debug;
		mTerminate = terminate;
		mProjectName = projectName;
	}

	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public void persist(Document doc, Element commandElement) {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", mDebug ? "Debug" : "Run");
		attrMap.put("kind", mTerminate ? "Terminate" : "Create");
		attrMap.put("projectName", mProjectName == null ? "(Unknown)" : mProjectName);
		
		Utilities.persistCommand(doc, commandElement, "RunCommand", attrMap, null, this);
	}

	public AbstractCommand createFrom(Element commandElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return (mTerminate ? "Terminate" : "Create") + " " + (mDebug ? "Debug" : "Run") + " Application";
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
		// TODO Auto-generated method stub
		return false;
	}

}
