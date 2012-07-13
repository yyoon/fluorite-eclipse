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

public class AddAnnotationDialog extends Dialog {

	// log version > 0.2.0
	public static final int CANCEL = 0;
	public static final int OTHER = 1;
	public static final int BACKTRACKING = 2;
	public static final int WRITING_NEW_CODE = 3;
	public static final int TUNING_PARAMETERS = 4;
	public static final int LEARNING_API = 5;
	public static final int TRYING_OUT_UI_DESIGN = 6;
	public static final int CORRECTING_LOGIC = 7;
	public static final int TRYING_OUT_DIFFERENT_ALGORITHMS = 8;
	public static final int DEBUGGING = 9;

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
		createButton(parent, TUNING_PARAMETERS, "Tuning parameters", false);
		createButton(parent, LEARNING_API, "Learning an API", false);
		createButton(parent, TRYING_OUT_UI_DESIGN, "Trying another UI design",
				false);
		createButton(parent, CORRECTING_LOGIC, "Correcting logic", false);
		createButton(parent, TRYING_OUT_DIFFERENT_ALGORITHMS,
				"Trying another algorithm", false);
		createButton(parent, DEBUGGING, "Debugging", false);
		createButton(parent, OTHER, "Other", true);
		createButton(parent, CANCEL, "Cancel", false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Add annotation");

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label label = new Label(comp, SWT.NONE);
		label.setText("Please describe the intention of your recent backtracking:");

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

	public String getComment() {
		return this.comment;
	}

}
