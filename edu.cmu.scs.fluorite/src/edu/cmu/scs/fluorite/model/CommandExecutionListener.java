package edu.cmu.scs.fluorite.model;

import edu.cmu.scs.fluorite.commands.ICommand;

public interface CommandExecutionListener {

	void commandExecuted(ICommand command);
	
}
