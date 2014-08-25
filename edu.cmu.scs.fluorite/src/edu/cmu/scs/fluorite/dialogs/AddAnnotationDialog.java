package edu.cmu.scs.fluorite.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.cmu.scs.fluorite.commands.AnnotateCommand;

public class AddAnnotationDialog extends Dialog {
	
	private Text textComment;
	private String comment;

	public AddAnnotationDialog(Shell parentShell) {
		super(parentShell);

		comment = "";
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// createButton(parent, BACKTRACKING, "Backtracking", true);
		// createButton(parent, WRITING_NEW_CODE, "Writing new code", false);
		createButton(parent, AnnotateCommand.TUNING_PARAMETERS, "Tuning parameters", false);
		createButton(parent, AnnotateCommand.LEARNING_API, "Learning an API", false);
		createButton(parent, AnnotateCommand.TRYING_OUT_UI_DESIGN, "Trying another UI design",
				false);
		createButton(parent, AnnotateCommand.CORRECTING_LOGIC, "Correcting logic", false);
		createButton(parent, AnnotateCommand.TRYING_OUT_DIFFERENT_ALGORITHMS,
				"Trying another algorithm", false);
		createButton(parent, AnnotateCommand.DEBUGGING, "Debugging", false);
		createButton(parent, AnnotateCommand.OTHER, "Other", true);
		createButton(parent, AnnotateCommand.CANCEL, "Cancel", false);
		
		// Modify the parent's layout
		GridLayout gridLayout = new GridLayout(3, true);
		parent.setLayout(gridLayout);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Add Annotation to the Log File");

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label label = new Label(comp, SWT.NONE);
		StringBuffer msg = new StringBuffer();
		msg.append("Please describe the intention of your recent backtracking.");
		msg.append("\n\nIf one of the buttons below describes your situation, you can simply click the button.");
		msg.append("\nOtherwise, please write a brief description in the textbox and click \"Other\".");
		label.setText(msg.toString());

		this.textComment = new Text(comp, SWT.BORDER);
		this.textComment.setSize(200, -1);
		this.textComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));

		return comp;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		this.comment = this.textComment.getText();
		close();
	}

	@Override
	protected void cancelPressed() {
		setReturnCode(CANCEL);
		close();
	}

	public String getComment() {
		return this.comment;
	}

}
