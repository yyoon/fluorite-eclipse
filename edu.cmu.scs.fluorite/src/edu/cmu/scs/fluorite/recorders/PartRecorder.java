package edu.cmu.scs.fluorite.recorders;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import edu.cmu.scs.fluorite.commands.FileOpenCommand;
import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;

public class PartRecorder extends BaseRecorder implements IPartListener2 {

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

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		
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

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		
		if (part instanceof IEditorPart) {
			getRecorder().removeListeners();
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
	}

}
