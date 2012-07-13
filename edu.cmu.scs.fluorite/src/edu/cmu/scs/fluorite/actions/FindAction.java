package edu.cmu.scs.fluorite.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import edu.cmu.scs.fluorite.commands.FindCommand;

public class FindAction extends Action {
	public FindAction() {
	}

	@Override
	public void run() {
		FindCommand fc = new FindCommand();
		fc.configureNew(Display.getDefault().getActiveShell());
	}

}
