package edu.cmu.scs.fluorite.commands;

import java.util.Map;

import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class CopyCommand extends AbstractCommand {
	
	public CopyCommand() {
	}

	public boolean execute(IEditorPart target) {
		return false;
	}

	public void dump() {
	}

	public Map<String, String> getAttributesMap() {
		return null;
	}

	public Map<String, String> getDataMap() {
		return null;
	}

	public String getCommandType() {
		return "CopyCommand";
	}

	public String getName() {
		return "Copy";
	}

	public String getDescription() {
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
		return false;
	}

}
