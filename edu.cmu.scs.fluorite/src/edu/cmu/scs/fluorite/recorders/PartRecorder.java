package edu.cmu.scs.fluorite.recorders;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import edu.cmu.scs.fluorite.commands.FileOpenCommand;
import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;

public class PartRecorder extends BaseRecorder implements IPartListener {

	private static PartRecorder instance = null;

	public static PartRecorder getInstance() {
		if (instance == null) {
			instance = new PartRecorder();
		}

		return instance;
	}

	private PartRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		// Do nothing.
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		// Do nothing.
	}

	public void partActivated(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (getRecorder().getEditor() == part) {
				return;
			}

			if (getRecorder().getEditor() != null) {
				String filePath = Utilities.getFilePathFromEditor(getRecorder()
						.getEditor());
				IDocument currentDoc = Utilities.getDocument(getRecorder()
						.getEditor());

				if (filePath != null && currentDoc != null) {
					EventRecorder.getInstance().getFileSnapshotManager()
							.updateSnapshot(filePath, currentDoc.get());
				}

				getRecorder().removeListeners();
			}

			IEditorPart editor = (IEditorPart) part;
			getRecorder().addListeners(editor);
			
			FileOpenCommand newFoc = new FileOpenCommand(editor);
			getRecorder().recordCommand(newFoc);
			getRecorder().fireActiveFileChangedEvent(newFoc);
			
			
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {
	}

	public void partClosed(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			getRecorder().removeListeners();
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
		// if (part instanceof IEditorPart) {
		// removeListeners();
		// }
	}

	public void partOpened(IWorkbenchPart part) {
	}

}
