package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class ShellBoundsCommand extends AbstractCommand {
	
	private Rectangle bounds;
	private Map<String, String> attrs;
	
	public ShellBoundsCommand(Rectangle r) {
		this.bounds = r;
		this.attrs = null;
		
		if (r != null) {
			this.attrs = new HashMap<String, String>();
			this.attrs.put("bounds", String.format("[%d, %d, %d, %d]", r.x, r.y, r.width, r.height));
		}
	}

	@Override
	public boolean execute(IEditorPart target) {
		return false;
	}

	@Override
	public void dump() {
	}

	@Override
	public Map<String, String> getAttributesMap() {
		return this.attrs;
	}

	@Override
	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public String getCommandType() {
		return "ShellBoundsCommand";
	}

	@Override
	public String getName() {
		return "Shell Bounds Command";
	}

	@Override
	public String getDescription() {
		return "Shell moved/resized to " + bounds;
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
		if (!(anotherCommand instanceof ShellBoundsCommand)) {
			return false;
		}
		
		ShellBoundsCommand rsc = (ShellBoundsCommand) anotherCommand;
		this.attrs = rsc.attrs;
		return true;
	}

}
