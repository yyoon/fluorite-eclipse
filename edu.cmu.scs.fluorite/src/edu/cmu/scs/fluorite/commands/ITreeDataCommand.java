package edu.cmu.scs.fluorite.commands;

import java.util.Map;

public interface ITreeDataCommand extends ICommand {

	Object getRootElement();
	Object[] getChildren(Object parentElement);
	
	String getTagName(Object element);
	Map<String, String> getAttrMap(Object element);
	
}
