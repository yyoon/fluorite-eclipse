package edu.cmu.scs.fluorite.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import edu.cmu.scs.fluorite.commands.AnnotateCommand;
import edu.cmu.scs.fluorite.dialogs.AddAnnotationDialog;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class AnnotateCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AddAnnotationDialog dialog = new AddAnnotationDialog(Display
				.getDefault().getActiveShell());
		dialog.open();

		EventRecorder.getInstance()
				.recordCommand(
						new AnnotateCommand(dialog.getReturnCode(), dialog
								.getComment()));
		return null;
	}

}
