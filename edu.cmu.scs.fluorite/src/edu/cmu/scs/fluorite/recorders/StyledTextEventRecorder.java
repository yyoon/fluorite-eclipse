package edu.cmu.scs.fluorite.recorders;

import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

import edu.cmu.scs.fluorite.commands.AbstractCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.commands.MouseWheelCommand;
import edu.cmu.scs.fluorite.commands.MoveCaretCommand;
import edu.cmu.scs.fluorite.commands.SelectTextCommand;
import edu.cmu.scs.fluorite.plugin.Activator;
import edu.cmu.scs.fluorite.preferences.Initializer;
import edu.cmu.scs.fluorite.util.Utilities;

public class StyledTextEventRecorder extends BaseRecorder implements Listener {

	private static StyledTextEventRecorder instance;

	public static StyledTextEventRecorder getInstance() {
		if (instance == null) {
			instance = new StyledTextEventRecorder();
		}

		return instance;
	}

	private StyledTextEventRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		final StyledText styledText = Utilities.getStyledText(editor);
		if (styledText == null)
			return;

		styledText.getDisplay().asyncExec(new Runnable() {
			public void run() {
				StyledTextEventRecorder styledTextEventRecorder = getInstance();
				styledText.addListener(SWT.KeyDown, styledTextEventRecorder);
				styledText.addListener(SWT.KeyUp, styledTextEventRecorder);
				styledText.addListener(SWT.MouseDown, styledTextEventRecorder);
				styledText.addListener(SWT.MouseUp, styledTextEventRecorder);
				styledText.addListener(SWT.MouseVerticalWheel,
						styledTextEventRecorder);
			}
		});
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		try {
			StyledText styledText = Utilities.getStyledText(editor);

			if (styledText != null) {
				styledText.removeListener(SWT.KeyDown, this);
				styledText.removeListener(SWT.KeyUp, this);
				styledText.removeListener(SWT.MouseDown, this);
				styledText.removeListener(SWT.MouseUp, this);
				styledText.removeListener(SWT.MouseVerticalWheel, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isBound(Event event) {
		IBindingService bindingService = (IBindingService) PlatformUI
				.getWorkbench().getAdapter(IBindingService.class);
		KeyStroke k = KeyStroke.getInstance(event.stateMask, event.keyCode);
		if (k == null)
			return false;
		Binding b = bindingService.getPerfectMatch(KeySequence.getInstance(k));
		return (b != null);
	}

	public void handleEvent(Event event) {
		getRecorder().updateIncrementalFindMode();
		switch (event.type) {
		case SWT.KeyDown: {
			if (isBound(event))
				return;

			if (getRecorder().isAssistSession()) {
				return;
			}

			ICommand command = Utilities.getCommandForKeyEvent(event);

			if (!getRecorder().isIncrementalFindMode()) {
				if (command != null) {
					getRecorder().recordCommand(command);
				}
			}
			break;
		}

		// case SWT.MouseDown:
		case SWT.MouseUp: {
			IEditorPart editor = Utilities.getActiveEditor();
			StyledText styledText = Utilities.getStyledText(editor);
			ISourceViewer viewer = Utilities.getSourceViewer(editor);
			if (styledText == null || viewer == null)
				break;

			if ((styledText.getSelection().x != styledText.getSelection().y)
					&& (styledText.getSelection().x != getRecorder()
							.getLastSelectionStart() || styledText
							.getSelection().y != getRecorder()
							.getLastSelectionEnd())) {
				AbstractCommand command = new SelectTextCommand(
						styledText.getSelection().x,
						styledText.getSelection().y,
						styledText.getCaretOffset());
				getRecorder().recordCommand(command);
			} else if (getRecorder().getLastCaretOffset() != styledText
					.getCaretOffset()) {
				AbstractCommand command = new MoveCaretCommand(
						styledText.getCaretOffset(),
						viewer.getSelectedRange().x);
				getRecorder().recordCommand(command);
			}

			break;
		}

		case SWT.MouseVerticalWheel: {
			if (Activator.getDefault().getPreferenceStore()
					.getBoolean(Initializer.Pref_LogMouseWheel) == false) {
				break;
			}

			getRecorder().recordCommand(new MouseWheelCommand(event.count));
		}
		}
	}
}
