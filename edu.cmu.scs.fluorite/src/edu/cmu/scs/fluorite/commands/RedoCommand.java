package edu.cmu.scs.fluorite.commands;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;

public class RedoCommand extends AbstractCommand {
	public static final String XML_Redo_Type="RedoCommand";

	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dump() {
		// TODO Auto-generated method stub

	}

	public void persist(Document doc, Element commandElement) {
		Utilities.persistCommand(doc, commandElement, XML_Redo_Type, null, null, this);
	}

	public ICommand createFrom(Element commandElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return "Redo";
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
		if (!(anotherCommand instanceof RedoCommand)) {
			return false;
		}
		
		return true;
	}

}
