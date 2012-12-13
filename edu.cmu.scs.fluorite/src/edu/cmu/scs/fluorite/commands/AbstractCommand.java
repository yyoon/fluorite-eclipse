package edu.cmu.scs.fluorite.commands;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.plugin.Activator;
import edu.cmu.scs.fluorite.preferences.Initializer;
import edu.cmu.scs.fluorite.util.Utilities;

public abstract class AbstractCommand implements
		edu.cmu.scs.fluorite.commands.ICommand {

	private static boolean incrementCommandID = true;
	private static int currentCommandID = -1;

	private static Set<ICommandIndexListener> commandIndexListeners = new HashSet<ICommandIndexListener>();

	public static void addCommandIndexListener(ICommandIndexListener listener) {
		if (!commandIndexListeners.contains(listener)) {
			commandIndexListeners.add(listener);
		}
	}

	public static void removeCommandIndexListener(ICommandIndexListener listener) {
		if (commandIndexListeners.contains(listener)) {
			commandIndexListeners.remove(listener);
		}
	}

	public static int getCurrentCommandID() {
		return currentCommandID;
	}
	
	public static void setIncrementCommandID(boolean state) {
		incrementCommandID = state;
	}
	
	public static boolean getIncrementCommandID() {
		return incrementCommandID;
	}

	public AbstractCommand() {
		if (getIncrementCommandID()) {
			mRepeatCount = 1;
			mCommandIndex = ++currentCommandID;
	
			fireCommandIndexChanged();
		}

		try {
			if (Activator.getDefault().getPreferenceStore()
					.getBoolean(Initializer.Pref_LogTopBottomLines)) {
				mTopBottomLinesRecorded = true;
	
				IEditorPart editor = Utilities.getActiveEditor();
				StyledText styledText = Utilities.getStyledText(editor);
	
				int clientAreaHeight = styledText.getClientArea().height;
				mTopLineNumber = styledText.getLineIndex(0) + 1;
				mBottomLineNumber = styledText.getLineIndex(clientAreaHeight) + 1;
			} else {
				mTopBottomLinesRecorded = false;
			}
		}
		catch (NullPointerException e) {
			// Maybe this is created in a JUnit test.
			// Don't bother to add these things.
		}
	}

	private static void fireCommandIndexChanged() {
		for (ICommandIndexListener listener : commandIndexListeners) {
			listener.commandIndexIncreased(currentCommandID);
		}
	}

	private long mTimestamp;
	private long mTimestamp2;

	private int mRepeatCount;
	private int mCommandIndex;

	// Top Bottom Lines
	private boolean mTopBottomLinesRecorded;
	private int mTopLineNumber;
	private int mBottomLineNumber;

	public String persist() {
		return Utilities.persistCommand(getAttributesMap(), getDataMap(), this);
	}

	public void persist(Document doc, Element commandElement) {
		Utilities.persistCommand(doc, commandElement, getCommandType(),
				getAttributesMap(), getDataMap(), this);
	}

	@Override
	public void createFrom(Element commandElement) {
		if (commandElement == null) {
			throw new IllegalArgumentException();
		}
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("__id")) != null) {
			mCommandIndex = Integer.parseInt(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("timestamp")) != null) {
			mTimestamp = Long.parseLong(attr.getValue());
		}
		
		if ((attr = commandElement.getAttributeNode("repeat")) != null) {
			mRepeatCount = Integer.parseInt(attr.getValue());
		}
		else {
			mRepeatCount = 1;
		}
		
		if ((attr = commandElement.getAttributeNode("timestamp2")) != null) {
			mTimestamp2 = Long.parseLong(attr.getValue());
		}
		
		mTopBottomLinesRecorded = false;
		if ((attr = commandElement.getAttributeNode("topLine")) != null) {
			mTopLineNumber = Integer.parseInt(attr.getValue());
			mTopBottomLinesRecorded = true;
		}
		
		if ((attr = commandElement.getAttributeNode("bottomLine")) != null) {
			mBottomLineNumber = Integer.parseInt(attr.getValue());
			mTopBottomLinesRecorded = true;
		}
	}

	public void setTimestamp(long timestamp) {
		mTimestamp = timestamp;
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp2(long timestamp) {
		mTimestamp2 = timestamp;
	}

	public long getTimestamp2() {
		return mTimestamp2;
	}

	public void increaseRepeatCount() {
		++mRepeatCount;
	}

	public int getRepeatCount() {
		return mRepeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		mRepeatCount = repeatCount;
	}

	public int getCommandIndex() {
		return mCommandIndex;
	}

	public boolean areTopBottomLinesRecorded() {
		return mTopBottomLinesRecorded;
	}

	public int getTopLineNumber() {
		return mTopLineNumber;
	}

	public int getBottomLineNumber() {
		return mBottomLineNumber;
	}

	public boolean combineWith(ICommand anotherCommand) {
		IPreferenceStore prefStore = edu.cmu.scs.fluorite.plugin.Activator
				.getDefault().getPreferenceStore();

		// preference option check.
		if (!prefStore.getBoolean(Initializer.Pref_CombineCommands)) {
			return false;
		}

		// Time threshold check.
		if (anotherCommand.getTimestamp() - getTimestamp2() > prefStore
				.getInt(Initializer.Pref_CombineTimeThreshold)) {
			return false;
		}

		if (combine(anotherCommand)) {
			setTimestamp2(anotherCommand.getTimestamp());
			increaseRepeatCount();
			return true;
		}

		return false;
	}

	public abstract boolean combine(ICommand anotherCommand);

	public String getCommandTag() {
		String commandTag = EventRecorder.XML_Command_Tag;
		String categoryID = getCategoryID();
		if (categoryID == null) {
			// do nothing
		} else if (categoryID.equals(EventRecorder.DocumentChangeCategoryID)) {
			commandTag = EventRecorder.XML_DocumentChange_Tag;
		} else if (categoryID.equals(EventRecorder.AnnotationCategoryID)) {
			commandTag = EventRecorder.XML_Annotation_Tag;
		}

		return commandTag;
	}
	
	public static void resetCommandID() {
		currentCommandID = -1;
		fireCommandIndexChanged();
	}
}
