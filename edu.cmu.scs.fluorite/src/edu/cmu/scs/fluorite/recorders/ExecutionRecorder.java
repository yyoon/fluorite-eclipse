package edu.cmu.scs.fluorite.recorders;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import edu.cmu.scs.fluorite.commands.CopyCommand;
import edu.cmu.scs.fluorite.commands.CutCommand;
import edu.cmu.scs.fluorite.commands.EclipseCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.commands.PasteCommand;
import edu.cmu.scs.fluorite.commands.RedoCommand;
import edu.cmu.scs.fluorite.commands.UndoCommand;
import edu.cmu.scs.fluorite.util.EventLoggerConsole;
import edu.cmu.scs.fluorite.util.Utilities;

public class ExecutionRecorder extends BaseRecorder implements
		IExecutionListener {

	private static ExecutionRecorder instance = null;

	public static ExecutionRecorder getInstance() {
		if (instance == null) {
			instance = new ExecutionRecorder();
		}

		return instance;
	}

	private ExecutionRecorder() {
		super();

		// TODO: get from extension point?
		mNonRecordableCommandIds = new HashSet<String>();
		mNonRecordableCommandIds
				.add(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE);
		mNonRecordableCommandIds.add("eventlogger.actions.viewLastLog");
		mNonRecordableCommandIds.add("eventlogger.actions.annotateBackward");
		mNonRecordableCommandIds.add("eventlogger.actions.annotateForward");
	}

	@Override
	public void addListeners(IEditorPart editor) {
		ICommandService cs = (ICommandService) PlatformUI.getWorkbench()
				.getAdapter(ICommandService.class);
		if (cs != null) {
			cs.addExecutionListener(this);
		}
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		try {
			ICommandService cs = (ICommandService) PlatformUI.getWorkbench()
					.getAdapter(ICommandService.class);
			if (cs != null) {
				cs.removeExecutionListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Listener[] mPreexecuteListeners = null;
	private Set<String> mNonRecordableCommandIds;

	public void notHandled(String commandId, NotHandledException exception) {
		getRecorder().setCurrentlyExecutingCommand(false);
		getRecorder().endIncrementalFindMode();
		System.out.println("not handled: " + commandId);
	}

	public void postExecuteFailure(String commandId,
			ExecutionException exception) {
		getRecorder().setCurrentlyExecutingCommand(false);
		getRecorder().endIncrementalFindMode();
		System.out.println("command failed: " + commandId);

	}

	public void postExecuteSuccess(String commandId, Object returnValue) {
		getRecorder().setCurrentlyExecutingCommand(false);
		getRecorder().endIncrementalFindMode();
		if (mNonRecordableCommandIds.contains(commandId)) {
			// we *always" see the record command first, so don't log it
			if (!commandId.equals("eventlogger.actions.recordMacro")
					&& !commandId
							.equals(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE)) {
				EventLoggerConsole.getConsole().writeln(
						"Not recording command (it's in the exclude list): "
								+ commandId, EventLoggerConsole.Type_Standard);
			}
			return;
		}

		if (commandId.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL)
				|| commandId
						.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL_REVERSE)) {
			if (commandId
					.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL))
				getRecorder().setIncrementalFindForward(true);
			else
				getRecorder().setIncrementalFindForward(false);
			Listener[] currentListeners = Utilities.getStyledText(
					Utilities.getActiveEditor()).getListeners(SWT.MouseUp);
			getRecorder().setIncrementalListener(null);
			for (Listener listener : currentListeners) {
				boolean inCurrentList = false;
				for (Listener listenerBeforeFind : mPreexecuteListeners) {
					if (listenerBeforeFind == listener)
						inCurrentList = true;
					break;
				}
				if (!inCurrentList) {
					getRecorder().setIncrementalListener(listener);
				}
			}
		}

		getRecorder().updateIncrementalFindMode();
		if (!getRecorder().isIncrementalFindMode()) {
			// System.out.println("Command executed: " + commandId);

			getRecorder().recordCommand(
					createCommandByEclipseCommandId(commandId));
		}
	}

	public void preExecute(String commandId, ExecutionEvent event) {
		getRecorder().setCurrentlyExecutingCommand(true);
		if (commandId.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL)
				|| commandId
						.equals(ITextEditorActionDefinitionIds.FIND_INCREMENTAL_REVERSE)) {
			getRecorder().setIncrementalFindMode(true);
			StyledText widget = Utilities.getStyledText(Utilities
					.getActiveEditor());
			if (widget != null) {
				mPreexecuteListeners = widget.getListeners(SWT.MouseUp);
			}
		}
		// System.out.println("preexecute: " + commandId);
	}

	private ICommand createCommandByEclipseCommandId(String commandId) {
		if (commandId.equals("org.eclipse.ui.edit.undo")) {
			return new UndoCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.redo")) {
			return new RedoCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.copy")) {
			return new CopyCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.cut")) {
			return new CutCommand();
		} else if (commandId.equals("org.eclipse.ui.edit.paste")) {
			return new PasteCommand();
		} else {
			return new EclipseCommand(commandId);
		}
	}
}
