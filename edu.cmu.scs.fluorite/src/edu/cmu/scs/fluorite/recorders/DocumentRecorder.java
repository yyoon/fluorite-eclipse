package edu.cmu.scs.fluorite.recorders;

import org.eclipse.jface.preference.IPreferenceStore;
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
	
	private static class InterceptInfo {
		private IDocument doc;
		private long beforeStamp;
		private long afterStamp;
		private IDocumentRecorderInterceptor interceptor;
		
		private boolean beforeMatched;
		
		public InterceptInfo(IDocument doc, long beforeStamp, long afterStamp, IDocumentRecorderInterceptor interceptor) {
			this.doc = doc;
			this.beforeStamp = beforeStamp;
			this.afterStamp = afterStamp;
			this.interceptor = interceptor;
			
			this.beforeMatched = false;
		}
		
		public boolean matchesBefore(IDocument doc, long beforeStamp) {
			return this.doc == doc && this.beforeStamp == beforeStamp;
		}
		
		public void setBeforeMatched() {
			this.beforeMatched = true;
		}
		
		public boolean matchesAfter(IDocument doc, long afterStamp) {
			return this.beforeMatched == true && this.doc == doc && this.afterStamp == afterStamp;
		}
		
		public IDocumentRecorderInterceptor getListener() {
			return this.interceptor;
		}
	}
	
	private InterceptInfo interceptInfo;

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
	
	public void setIntercept(IDocument doc, long beforeStamp, long afterStamp, IDocumentRecorderInterceptor interceptor) {
		if (this.interceptInfo != null) {
			throw new IllegalStateException();
		}
		
		this.interceptInfo = new InterceptInfo(doc, beforeStamp, afterStamp, interceptor);
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		// Check if there is an interceptor set.
		if (this.interceptInfo != null) {
			if (this.interceptInfo.matchesBefore(event.getDocument(), event.getModificationStamp())) {
				this.interceptInfo.setBeforeMatched();
				return;
			} else {
				this.interceptInfo = null;
			}
		}
		
		// DeleteCommand or ReplaceCommand
		if (event.getLength() > 0) {
			IDocument doc = event.getDocument();

			try {
				int startLine = doc.getLineOfOffset(event.getOffset());
				int endLine = doc.getLineOfOffset(event.getOffset()
						+ event.getLength());

				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				String deletedText = null;
				boolean logDeleted = store.getBoolean(Initializer.Pref_LogDeletedText);
				if (logDeleted) {
					deletedText = doc.get(event.getOffset(), event.getLength());
				}

				String insertedText = null;
				boolean logInserted = store.getBoolean(Initializer.Pref_LogInsertedText);
				if (logInserted) {
					insertedText = event.getText();
				}

				ICommand command = null;
				if (event.getText().length() > 0) {
					if (logDeleted && deletedText != null && deletedText.length() > 0 &&
						logInserted && insertedText != null && insertedText.length() > 0) {
						// Find the common prefix.
						int idx = 0;
						while (idx < deletedText.length() && idx < insertedText.length()) {
							if (deletedText.charAt(idx) != insertedText.charAt(idx)) {
								break;
							}
							
							++idx;
						}
						
						// Strip the common prefix.
						deletedText = deletedText.substring(idx);
						insertedText = insertedText.substring(idx);
						
						if (deletedText.length() == 0) {
							splitAndAdd(insertedText, event.getOffset() + idx, doc);
						} else {
							if (insertedText.contains("\r") || insertedText.contains("\n")) {
								getRecorder().recordCommand(new Delete(event.getOffset() + idx, deletedText.length(),
										startLine, endLine, deletedText, doc));
								splitAndAdd(insertedText, event.getOffset() + idx, doc);
							} else {
								command = new Replace(event.getOffset() + idx, deletedText.length(),
										startLine, endLine, insertedText.length(),
										deletedText, insertedText, doc);
							}
						}
					} else {
						command = new Replace(event.getOffset(), event.getLength(),
								startLine, endLine, event.getText().length(),
								deletedText, insertedText, doc);
					}
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

		// Check if there is an interceptor set.
		if (this.interceptInfo != null) {
			if (this.interceptInfo.matchesAfter(event.getDocument(), event.getModificationStamp())) {
				this.interceptInfo.getListener().documentChanged(event, getRecorder());
				this.interceptInfo = null;
				return;
			} else {
				this.interceptInfo = null;
			}
		}
		
		// InsertCommand
		if (event.getText().length() > 0 && event.getLength() <= 0) {
			try {
				IDocument doc = event.getDocument();

				String text = null;
				if (Activator.getDefault().getPreferenceStore()
						.getBoolean(Initializer.Pref_LogInsertedText)) {
					text = event.getText();
					
					splitAndAdd(text, event.getOffset(), doc);
				} else {
					Insert command = new Insert(event.getOffset(), text, doc);
					getRecorder().recordCommand(command);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void splitAndAdd(String insertedText, int offset, IDocument doc) {
		// Nothing to log.
		if (insertedText == null || insertedText.length() == 0) {
			return;
		}
		
		// No new line characters. Just record it.
		if (!insertedText.contains("\r") && !insertedText.contains("\n")) {
			getRecorder().recordCommand(new Insert(offset, insertedText, doc));
			return;
		}
		
		int index = -1;
		
		// Determine whether this string starts with a new line character or not.
		if (insertedText.startsWith("\r") || insertedText.startsWith("\n")) {
			index = insertedText.startsWith("\r\n") ? 2 : 1;
			while (index < insertedText.length()) {
				char ch = insertedText.charAt(index);
				if (!Character.isWhitespace(ch) || ch == '\r' || ch == '\n') {
					break;
				}
				
				++index;
			}
		} else {
			// Find the newline character!
			int crIndex = insertedText.indexOf('\r');
			int lfIndex = insertedText.indexOf('\n');
			
			if (crIndex > -1) {
				index = crIndex;
			}
			
			if (lfIndex > -1 && (index == -1 || lfIndex < crIndex)) {
				index = lfIndex;
			}
		}
		
		getRecorder().recordCommand(new Insert(offset, insertedText.substring(0, index), doc));
		splitAndAdd(insertedText.substring(index), offset + index, doc);
	}
}
