package edu.cmu.scs.fluorite.recorders;

import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestRunSession;

import edu.cmu.scs.fluorite.commands.JUnitCommand;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class JUnitRecorder extends TestRunListener {

	private static JUnitRecorder instance = null;

	public static JUnitRecorder getInstance() {
		if (instance == null) {
			instance = new JUnitRecorder();
		}

		return instance;
	}

	private EventRecorder mRecorder;

	protected EventRecorder getRecorder() {
		return mRecorder;
	}

	private JUnitRecorder() {
		mRecorder = EventRecorder.getInstance();
	}

	@Override
	public void sessionStarted(ITestRunSession session) {
		// Don't do anything here.
	}

	@Override
	public void sessionFinished(ITestRunSession session) {
		if (session == null) {
			throw new IllegalArgumentException();
		}
		
		// Create a JUnitCommand instance.
		JUnitCommand junitCommand = new JUnitCommand(
				session.getLaunchedProject().getProject().getName(),	// The name of the Launched project
				session.getElapsedTimeInSeconds(),
				session);												// The session itself as the root test element.
		
		getRecorder().recordCommand(junitCommand);
	}

}
