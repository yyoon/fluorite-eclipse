package edu.cmu.scs.fluorite.commands;

public interface ITypeOverridable {
	
	/**
	 * Gets the Type value to be used when showing the event in the timeline.
	 * @return type value for displaying the event in the timeline.
	 */
	String getTypeForDisplay();

}
