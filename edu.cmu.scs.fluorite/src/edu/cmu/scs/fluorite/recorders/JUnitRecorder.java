package edu.cmu.scs.fluorite.recorders;

import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestRunSession;

import edu.cmu.scs.fluorite.commands.AnnotateCommand;
import edu.cmu.scs.fluorite.dialogs.AddAnnotationDialog;
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
		getRecorder().recordCommand(
				new AnnotateCommand(AddAnnotationDialog.OTHER,
						"sessionStarted"));
	}

	@Override
	public void sessionFinished(ITestRunSession session) {
		getRecorder().recordCommand(
				new AnnotateCommand(
						AddAnnotationDialog.OTHER,
						"sessionFinished: " + 
						(session.getTestResult(true) == ITestElement.Result.OK
						? "Succeeded"
						: "Failed")));
	}

}
