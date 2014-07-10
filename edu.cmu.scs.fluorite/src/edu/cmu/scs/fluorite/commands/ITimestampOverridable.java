package edu.cmu.scs.fluorite.commands;

public interface ITimestampOverridable {
	
	/**
	 * Gets the Timestamp value to be used when showing the event in the timeline.
	 * The returned value should be absolute timestamp, not relative.
	 * @return absolute timestamp value for displaying the event in the timeline.
	 */
	long getTimestampForDisplay();
	
}
