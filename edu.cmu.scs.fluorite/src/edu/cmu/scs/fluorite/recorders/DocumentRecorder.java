package edu.cmu.scs.fluorite.recorders;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.commands.Delete;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.commands.Insert;
import edu.cmu.scs.fluorite.commands.Replace;
import edu.cmu.scs.fluorite.plugin.Activator;
import edu.cmu.scs.fluorite.preferences.Initializer;
import edu.cmu.scs.fluorite.util.Utilities;

public class DocumentRecorder extends BaseRecorder implements IDocumentListener {

	private static DocumentRecorder instance = null;

	public static DocumentRecorder getInstance() {
		if (instance == null) {
			instance = new DocumentRecorder();
		}

		return instance;
	}

	private DocumentRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		IDocument document = Utilities.getIDocumentForEditor(editor);
		if (document != null) {
			document.addDocumentListener(this);
		}
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		try {
			IDocument document = Utilities.getIDocumentForEditor(editor);
			if (document != null) {
				document.removeDocumentListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		// DeleteCommand or ReplaceCommand
		if (event.getLength() > 0) {
			IDocument doc = event.getDocument();

			try {
				int startLine = doc.getLineOfOffset(event.getOffset());
				int endLine = doc.getLineOfOffset(event.getOffset()
						+ event.getLength());

				String deletedText = null;
				if (Activator.getDefault().getPreferenceStore()
						.getBoolean(Initializer.Pref_LogDeletedText)) {
					deletedText = doc.get(event.getOffset(), event.getLength());
				}

				String insertedText = null;
				if (Activator.getDefault().getPreferenceStore()
						.getBoolean(Initializer.Pref_LogInsertedText)) {
					insertedText = event.getText();
				}

				ICommand command = null;
				if (event.getText().length() > 0) {
					command = new Replace(event.getOffset(), event.getLength(),
							startLine, endLine, event.getText().length(),
							deletedText, insertedText, doc);
				} else {
					command = new Delete(event.getOffset(), event.getLength(),
							startLine, endLine, deletedText, doc);

				}

				if (command != null) {
					getRecorder().recordCommand(command);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void documentChanged(DocumentEvent event) {
		if (!getRecorder().isCurrentlyExecutingCommand()) {
			getRecorder().endIncrementalFindMode();
		}

		// InsertCommand
		if (event.getText().length() > 0 && event.getLength() <= 0) {
			try {
				IDocument doc = event.getDocument();

				String text = null;
				if (Activator.getDefault().getPreferenceStore()
						.getBoolean(Initializer.Pref_LogInsertedText)) {
					text = event.getText();
				}

				Insert command = new Insert(event.getOffset(), text, doc);

				getRecorder().recordCommand(command);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
