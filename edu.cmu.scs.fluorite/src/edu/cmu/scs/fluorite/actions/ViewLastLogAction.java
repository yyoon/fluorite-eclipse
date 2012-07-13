package edu.cmu.scs.fluorite.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import edu.cmu.scs.fluorite.dialogs.ViewCurrentLogDialog;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class ViewLastLogAction extends Action implements
		IWorkbenchWindowActionDelegate {

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		Shell shell = Display.getDefault().getActiveShell();
		ViewCurrentLogDialog dialog = new ViewCurrentLogDialog(shell,
				EventRecorder.getInstance().getRecordedEventsSoFar());
		dialog.open();
	}

}
