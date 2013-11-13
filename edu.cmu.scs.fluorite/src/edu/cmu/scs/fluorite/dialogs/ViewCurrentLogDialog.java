package edu.cmu.scs.fluorite.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.model.Events;

public class ViewCurrentLogDialog extends Dialog {
	private Events mEvents;

	public ViewCurrentLogDialog(Shell shell, Events events) {
		super(shell);
		mEvents = events;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("View Current Log");
		Composite comp = new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		StyledText exportText = new StyledText(comp, SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 600;
		gd.widthHint = 800;
		exportText.setLayoutData(gd);

		String eventsXML = EventRecorder.persistMacro(mEvents);
		exportText.setText(eventsXML);

		return comp;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}
}
