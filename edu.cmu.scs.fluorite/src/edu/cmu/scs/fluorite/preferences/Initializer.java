package edu.cmu.scs.fluorite.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

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

	public Initializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_EnableEventLogger, true);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_ShowConsole, false);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_WriteToConsole, false);

		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_CombineCommands, true);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_CombineTimeThreshold, 2000);

		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_LogInsertedText, true);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_LogDeletedText, true);

		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_LogTopBottomLines, false);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_LogMouseWheel, false);

		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_FindForward, true);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_FindCaseSensitive, false);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_FindWrapSearch, false);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_FindWholeWord, false);
		Activator.getDefault().getPreferenceStore()
				.setDefault(Pref_FindRegExp, false);
	}

}
