package edu.cmu.scs.fluorite.model;

import edu.cmu.scs.fluorite.commands.FileOpenCommand;
import edu.cmu.scs.fluorite.commands.document.DocChange;

public interface DocumentChangeListener {
	
	/**
	 * Fired when a new file was opened and the corresponding FileOpenCommand was recorded. 
	 */
	void activeFileChanged(FileOpenCommand foc);
	
	/**
	 * Fired when a new documentChange event was recorded.
	 */
	void documentChanged(DocChange docChange);
	
	/**
	 * Fired when a new documentChange event was recorded and its values are fixed.
	 */
	void documentChangeFinalized(DocChange docChange);
	
	/**
	 * Fired when a documentChange event was updated (due to combining).
	 */
	void documentChangeUpdated(DocChange docChange);
	
	/**
	 * Fired when a document change event was replaced with another one.
	 */
	void documentChangeAmended(DocChange oldDocChange, DocChange newDocChange);

}
