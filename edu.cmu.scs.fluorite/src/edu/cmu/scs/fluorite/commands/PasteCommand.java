package edu.cmu.scs.fluorite.commands;

import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class PasteCommand extends AbstractCommand {
	
	public PasteCommand() {
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

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
	}

	public String getCommandType() {
		return "PasteCommand";
	}

	public String getName() {
		return "Paste";
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
