package edu.cmu.scs.fluorite.recorders;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.commands.RunCommand;

public class RunRecorder extends BaseRecorder implements
		IDebugEventSetListener {

	private static RunRecorder instance = null;

	public static RunRecorder getInstance() {
		if (instance == null) {
			instance = new RunRecorder();
		}

		return instance;
	}

	private RunRecorder() {
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

	public void handleDebugEvents(DebugEvent[] debugEvents) {
		for (DebugEvent event : debugEvents) {
			if (event.getKind() == DebugEvent.CREATE
					|| event.getKind() == DebugEvent.TERMINATE) {
				Object source = event.getSource();
				boolean terminate = event.getKind() == DebugEvent.TERMINATE;

				if (source instanceof IProcess) {
					IProcess process = (IProcess) source;
					ILaunchConfiguration config = process.getLaunch()
							.getLaunchConfiguration();

					if (config == null) {
						return;
					}

					@SuppressWarnings("rawtypes")
					Map attributes = null;
					try {
						attributes = config.getAttributes();
					} catch (CoreException e) {
						e.printStackTrace();
					}

					// Retrieve the corresponding project name
					String projectName = (String) (attributes
							.get("org.eclipse.jdt.launching.PROJECT_ATTR"));

					getRecorder().recordCommand(
							new RunCommand(false, terminate, projectName));

				} else if (source instanceof IDebugTarget) {
					getRecorder().recordCommand(
							new RunCommand(true, terminate, null));
				}
			}
		}
	}

}
