package edu.cmu.scs.fluorite.recorders;

import org.eclipse.jface.text.DocumentEvent;

import edu.cmu.scs.fluorite.model.EventRecorder;

public interface IDocumentRecorderInterceptor {
	
	void documentChanged(DocumentEvent event, EventRecorder recorder);
	
}
