package edu.cmu.scs.fluorite.recorders;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.commands.RunCommand;

public class RunRecorder extends BaseRecorder implements IDebugEventSetListener {

	private static final String PROJECT_ATTR_KEY = "org.eclipse.jdt.launching.PROJECT_ATTR";

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
			handleDebugEvent(event);
		}
	}

	private void handleDebugEvent(DebugEvent event) {
		int kind = event.getKind();
		if (kind != DebugEvent.CREATE && kind != DebugEvent.TERMINATE) {
			return;
		}

		Object source = event.getSource();
		boolean terminate = kind == DebugEvent.TERMINATE;

		IProcess process = getProcess(source);
		boolean debug = source instanceof IDebugTarget;
		if (process == null) {
			return;
		}

		int exitValue = 0;
		if (terminate) {
			try {
				exitValue = process.getExitValue();
			} catch (DebugException e1) {
			}
		}

		ILaunchConfiguration config = process.getLaunch().getLaunchConfiguration();
		if (config == null) {
			return;
		}

		ILaunchConfigurationType configType = getConfigurationType(config);
		if (configType == null) {
			return;
		}

		// Filter out JUnit runs, because they are handled separately
		// by JUnitRecorder.
		if (configType.getIdentifier().contains("junit")) {
			return;
		}

		String projectName = "null";
		try {
			projectName = config.getAttribute(PROJECT_ATTR_KEY, "null");
		} catch (CoreException e) {
			e.printStackTrace();
		}

		getRecorder().recordCommand(new RunCommand(debug, terminate, projectName, exitValue));
	}

	private IProcess getProcess(Object source) {
		if (source instanceof IDebugTarget) {
			return ((IDebugTarget) source).getProcess();
		} else if (source instanceof IProcess) {
			return (IProcess) source;
		}
		return null;
	}

	private ILaunchConfigurationType getConfigurationType(ILaunchConfiguration config) {
		ILaunchConfigurationType configType = null;
		try {
			configType = config.getType();
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		return configType;
	}

}
