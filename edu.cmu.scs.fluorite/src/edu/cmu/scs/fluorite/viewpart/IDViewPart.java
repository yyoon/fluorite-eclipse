package edu.cmu.scs.fluorite.viewpart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import edu.cmu.scs.fluorite.commands.AbstractCommand;
import edu.cmu.scs.fluorite.commands.ICommandIndexListener;

public class IDViewPart extends ViewPart implements ICommandIndexListener {

	private Label label;

	@Override
	public void createPartControl(Composite parent) {
		this.label = new Label(parent, SWT.CENTER);
		this.label.setText(Integer.toString(AbstractCommand
				.getCurrentCommandID()));

		AbstractCommand.addCommandIndexListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();

		AbstractCommand.removeCommandIndexListener(this);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setID(int id) {
		this.label.setText(Integer.toString(id));
	}

	public void commandIndexIncreased(final int currentIndex) {
		// This should be always run in the UI thread.
		// If not, SWTException is thrown. (e.g., when the RunCommand is
		// executed)
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				setID(currentIndex);
			}
		});
	}

}
