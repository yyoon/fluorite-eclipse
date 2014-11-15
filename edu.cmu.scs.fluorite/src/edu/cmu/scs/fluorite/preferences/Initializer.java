package edu.cmu.scs.fluorite.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.cmu.scs.fluorite.plugin.Activator;

public class Initializer extends AbstractPreferenceInitializer {

	public static final String Pref_EnableEventLogger = "EventLogger_EnableEventLogger";
	public static final String Pref_ShowConsole = "EventLogger_ShowConsole";
	public static final String Pref_WriteToConsole = "EventLogger_WriteToConsole";

	public static final String Pref_CombineCommands = "EventLogger_CombineCommands";
	public static final String Pref_CombineTimeThreshold = "EventLogger_CombineTimeThreshold";

	public static final String Pref_LogInsertedText = "EventLogger_LogInsertedText";
	public static final String Pref_LogDeletedText = "EventLogger_LogDeletedText";

	public static final String Pref_LogTopBottomLines = "EventLogger_LogTopBottomLines";
	public static final String Pref_LogMouseWheel = "EventLogger_LogMouseWheel";

	public static final String Pref_FindForward = "EventLogger_FindForward";
	public static final String Pref_FindCaseSensitive = "EventLogger_FindCaseSensitive";
	public static final String Pref_FindWrapSearch = "EventLogger_FindWrapSearch";
	public static final String Pref_FindWholeWord = "EventLogger_FindWholeWord";
	public static final String Pref_FindRegExp = "EventLogger_FindRegExp";
	
	public static final String Pref_LogSeparateLines = "EventLogger_LogSeparateLines";

	public Initializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(Pref_EnableEventLogger, true);
		store.setDefault(Pref_ShowConsole, false);
		store.setDefault(Pref_WriteToConsole, false);

		store.setDefault(Pref_CombineCommands, true);
		store.setDefault(Pref_CombineTimeThreshold, 2000);

		store.setDefault(Pref_LogInsertedText, true);
		store.setDefault(Pref_LogDeletedText, true);

		store.setDefault(Pref_LogTopBottomLines, false);
		store.setDefault(Pref_LogMouseWheel, false);

		store.setDefault(Pref_FindForward, true);
		store.setDefault(Pref_FindCaseSensitive, false);
		store.setDefault(Pref_FindWrapSearch, false);
		store.setDefault(Pref_FindWholeWord, false);
		store.setDefault(Pref_FindRegExp, false);
		
		store.setDefault(Pref_LogSeparateLines, false);
	}

}
