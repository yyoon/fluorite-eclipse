package edu.cmu.scs.fluorite.dialogs;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.plugin.Activator;
import edu.cmu.scs.fluorite.preferences.Initializer;

public class OptionsPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button mEnableEventLogger;
	private Button mShowConsole;
	private Button mWriteToConsole;

	private Button mCombineCommands;
	private Text mCombineThreshold;

	private Button mLogInsertedText;
	private Button mLogDeletedText;

	private Button mLogTopBottomLines;
	private Button mLogMouseWheel;
	
	private Button mLogSeparateLines;

	public OptionsPage() {
	}

	public OptionsPage(String title) {
		super(title);
	}

	public OptionsPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(getShell(), Activator.PLUGIN_ID + ".overallHelp");
		Composite comp = new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		mEnableEventLogger = new Button(comp, SWT.CHECK);
		mEnableEventLogger
				.setText("Enable EventLogger plugin (restart needed)");
		mEnableEventLogger.setSelection(store.getBoolean(Initializer.Pref_EnableEventLogger));

		mShowConsole = new Button(comp, SWT.CHECK);
		mShowConsole.setText("Show console");
		mShowConsole
				.setToolTipText("If set, make the console visible during command log.");
		mShowConsole.setSelection(store.getBoolean(Initializer.Pref_ShowConsole));

		mWriteToConsole = new Button(comp, SWT.CHECK);
		mWriteToConsole.setText("Write to console");
		mWriteToConsole
				.setToolTipText("If set, write normal execution trace statements to the console during command log.");
		mWriteToConsole.setSelection(store.getBoolean(Initializer.Pref_WriteToConsole));

		mCombineCommands = new Button(comp, SWT.CHECK);
		mCombineCommands.setText("Combine multiple commands of same type");
		mCombineCommands.setSelection(store.getBoolean(Initializer.Pref_CombineCommands));

		Composite compCombine = new Composite(comp, SWT.NONE);
		compCombine.setLayout(new GridLayout(2, false));
		compCombine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(compCombine, SWT.NONE);
		label.setText("Command combine time threshold (in milliseconds)");

		mCombineThreshold = new Text(compCombine, SWT.BORDER);
		mCombineThreshold.setText(Integer.toString(store.getInt(Initializer.Pref_CombineTimeThreshold)));

		mLogInsertedText = new Button(comp, SWT.CHECK);
		mLogInsertedText.setText("Log Inserted Text");
		mLogInsertedText.setSelection(store.getBoolean(Initializer.Pref_LogInsertedText));

		mLogDeletedText = new Button(comp, SWT.CHECK);
		mLogDeletedText.setText("Log Deleted Text");
		mLogDeletedText.setSelection(store.getBoolean(Initializer.Pref_LogDeletedText));

		mLogTopBottomLines = new Button(comp, SWT.CHECK);
		mLogTopBottomLines
				.setText("Log top / bottom line numbers shown on the screen");
		mLogTopBottomLines.setSelection(store.getBoolean(Initializer.Pref_LogTopBottomLines));

		mLogMouseWheel = new Button(comp, SWT.CHECK);
		mLogMouseWheel.setText("Log mouse wheels");
		mLogMouseWheel.setSelection(store.getBoolean(Initializer.Pref_LogMouseWheel));
		
		mLogSeparateLines = new Button(comp, SWT.CHECK);
		mLogSeparateLines.setText("Log separate lines when a large chunk of code is inserted");
		mLogSeparateLines.setSelection(store.getBoolean(Initializer.Pref_LogSeparateLines));

		return comp;
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

	@Override
	public boolean performOk() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setValue(Initializer.Pref_EnableEventLogger, mEnableEventLogger.getSelection());
		store.setValue(Initializer.Pref_ShowConsole, mShowConsole.getSelection());
		store.setValue(Initializer.Pref_WriteToConsole, mWriteToConsole.getSelection());

		store.setValue(Initializer.Pref_CombineCommands, mCombineCommands.getSelection());
		store.setValue(Initializer.Pref_CombineTimeThreshold,
				Integer.parseInt(mCombineThreshold.getText()));
		EventRecorder.getInstance().setCombineCommands(mCombineCommands.getSelection());
		EventRecorder.getInstance().setCombineTimeThreshold(
				Integer.parseInt(mCombineThreshold.getText()));

		store.setValue(Initializer.Pref_LogInsertedText, mLogInsertedText.getSelection());
		store.setValue(Initializer.Pref_LogDeletedText, mLogDeletedText.getSelection());

		store.setValue(Initializer.Pref_LogTopBottomLines, mLogTopBottomLines.getSelection());

		store.setValue(Initializer.Pref_LogMouseWheel, mLogMouseWheel.getSelection());
		
		store.setValue(Initializer.Pref_LogSeparateLines, mLogSeparateLines.getSelection());

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		mEnableEventLogger
				.setSelection(store.getDefaultBoolean(Initializer.Pref_EnableEventLogger));
		mShowConsole.setSelection(store.getDefaultBoolean(Initializer.Pref_ShowConsole));
		mWriteToConsole.setSelection(store.getDefaultBoolean(Initializer.Pref_WriteToConsole));

		mCombineCommands.setSelection(store.getDefaultBoolean(Initializer.Pref_CombineCommands));
		mCombineThreshold.setText(Integer.toString(store
				.getDefaultInt(Initializer.Pref_CombineTimeThreshold)));

		mLogInsertedText.setSelection(store.getDefaultBoolean(Initializer.Pref_LogInsertedText));
		mLogDeletedText.setSelection(store.getDefaultBoolean(Initializer.Pref_LogDeletedText));

		mLogTopBottomLines
				.setSelection(store.getDefaultBoolean(Initializer.Pref_LogTopBottomLines));
		mLogMouseWheel.setSelection(store.getDefaultBoolean(Initializer.Pref_LogMouseWheel));
		
		mLogSeparateLines.setSelection(store.getDefaultBoolean(Initializer.Pref_LogSeparateLines));

		super.performDefaults();
	}

}
