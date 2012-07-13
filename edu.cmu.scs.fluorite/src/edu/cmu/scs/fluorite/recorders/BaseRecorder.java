package edu.cmu.scs.fluorite.recorders;

import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.model.EventRecorder;

public abstract class BaseRecorder {

	protected BaseRecorder() {
		mRecorder = EventRecorder.getInstance();
	}

	public abstract void addListeners(IEditorPart editor);

	public abstract void removeListeners(IEditorPart editor);

	private EventRecorder mRecorder;

	protected EventRecorder getRecorder() {
		return mRecorder;
	}

}
