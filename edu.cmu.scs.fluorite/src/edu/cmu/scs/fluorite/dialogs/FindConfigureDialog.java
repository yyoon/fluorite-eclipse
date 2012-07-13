package edu.cmu.scs.fluorite.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension6;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.cmu.scs.fluorite.commands.FindCommand;
import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.plugin.Activator;
import edu.cmu.scs.fluorite.preferences.Initializer;

public class FindConfigureDialog extends Dialog {
	private Text mSearchText;
	private Text mReplaceText;
	private Button mCaseSensitive;
	private Button mForward;
	private Button mBackward;
	private Button mRegExpMode;
	private Button mMatchWord;
	private Button mWrap;
	private Button mIncremental;
	private Button mAll;
	private Button mSelectedLines;

	private Button mReplaceAndFindButton;
	private Button mReplaceButton;

	private String mInitialSearchString;

	private boolean mCreatedDialogArea;

	private IFindReplaceTargetExtension mFindReplaceTargetExt;
	private IFindReplaceTargetExtension3 mFindReplaceTargetExt3;
	private ITextViewer mViewer;
	private Point mInitialSelection;
	private Point mLastSelection;

	private static final int FIND = 0;
	private static final int REPLACE_AND_FIND = 1;
	private static final int REPLACE = 2;
	private static final int REPLACE_ALL = 3;
	private static final int CLOSE = 4;

	private static FindConfigureDialog _instance = null;

	public static FindConfigureDialog getInstance() {
		return _instance;
	}

	public FindConfigureDialog(Shell shell, String initialSearchString,
			ITextViewer viewer) {
		super(shell);
		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE
				| SWT.RESIZE);
		mInitialSearchString = initialSearchString;
		mCreatedDialogArea = false;
		mViewer = viewer;
		mFindReplaceTargetExt = (IFindReplaceTargetExtension) viewer
				.getFindReplaceTarget();
		mFindReplaceTargetExt3 = (IFindReplaceTargetExtension3) viewer
				.getFindReplaceTarget();
		mInitialSelection = viewer.getSelectedRange();
		mLastSelection = new Point(0, 0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Find/Replace");
		Composite comp = new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite stringComp = new Composite(comp, SWT.NONE);
		stringComp.setLayout(new GridLayout(2, false));
		stringComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(stringComp, SWT.NONE);
		label.setText("&Find:");

		mSearchText = new Text(stringComp, SWT.BORDER);
		mSearchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		label = new Label(stringComp, SWT.NONE);
		label.setText("R&eplace with:");

		mReplaceText = new Text(stringComp, SWT.BORDER);
		mReplaceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		stringComp.pack();

		Composite dirScopeComp = new Composite(comp, SWT.NONE);
		dirScopeComp.setLayout(new GridLayout(2, true));
		dirScopeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group dirGroup = new Group(dirScopeComp, SWT.NONE);
		dirGroup.setText("Direction");
		dirGroup.setLayout(new GridLayout());
		dirGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		mForward = new Button(dirGroup, SWT.RADIO);
		mForward.setText("F&orward");
		mBackward = new Button(dirGroup, SWT.RADIO);
		mBackward.setText("&Backward");

		Group scopeGroup = new Group(dirScopeComp, SWT.NONE);
		scopeGroup.setText("Scope");
		scopeGroup.setLayout(new GridLayout());
		scopeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		mAll = new Button(scopeGroup, SWT.RADIO);
		mAll.setText("A&ll");
		mAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mFindReplaceTargetExt.setScope(null);
				mViewer.setSelectedRange(mInitialSelection.x,
						mInitialSelection.y);
			}

		});
		mSelectedLines = new Button(scopeGroup, SWT.RADIO);
		mSelectedLines.setText("Selec&ted lines");
		mSelectedLines.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Point lineInfo = mFindReplaceTargetExt.getLineSelection();
				mFindReplaceTargetExt.setScope(new Region(lineInfo.x,
						lineInfo.y));
				mViewer.setSelectedRange(lineInfo.x, 0);
			}

		});

		Group optionsGroup = new Group(dirScopeComp, SWT.NONE);
		optionsGroup.setText("Options");
		optionsGroup.setLayout(new GridLayout(2, false));
		optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				2, 1));

		mCaseSensitive = new Button(optionsGroup, SWT.CHECK);
		mCaseSensitive.setText("&Case sensitive");

		mWrap = new Button(optionsGroup, SWT.CHECK);
		mWrap.setText("Wra&p search");
		mWrap.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		mMatchWord = new Button(optionsGroup, SWT.CHECK);
		mMatchWord.setText("&Whole word");

		mIncremental = new Button(optionsGroup, SWT.CHECK);
		mIncremental.setText("&Incremental");
		mIncremental.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		mRegExpMode = new Button(optionsGroup, SWT.CHECK);
		mRegExpMode.setText("Regular e&xpressions");
		mRegExpMode.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				2, 1));

		dirScopeComp.pack();

		Composite buttonsComp = new Composite(comp, SWT.NONE);
		GridLayout buttonsLayout = new GridLayout();
		buttonsComp.setLayout(buttonsLayout);

		createButton(buttonsComp, FIND, "Fi&nd", true);
		mReplaceAndFindButton = createButton(buttonsComp, REPLACE_AND_FIND,
				"Replace/Fin&d", false);

		mReplaceButton = createButton(buttonsComp, REPLACE, "&Replace", false);
		createButton(buttonsComp, REPLACE_ALL, "Replace &All", false);

		setReplaceButtonsEnabled(false);

		buttonsComp.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true,
				true));
		buttonsLayout.numColumns = 2;

		// populate fields
		if (mInitialSearchString != null) {
			mSearchText.setText(mInitialSearchString);
		}

		IPreferenceStore prefStore = Activator.getDefault()
				.getPreferenceStore();

		if (prefStore.getBoolean(Initializer.Pref_FindForward)) {
			mForward.setSelection(true);
		} else {
			mBackward.setSelection(true);
		}

		mCaseSensitive.setSelection(prefStore
				.getBoolean(Initializer.Pref_FindCaseSensitive));
		mWrap.setSelection(prefStore
				.getBoolean(Initializer.Pref_FindWrapSearch));
		mMatchWord.setSelection(prefStore
				.getBoolean(Initializer.Pref_FindWholeWord));

		// TODO: implement incremental find later.
		mIncremental.setEnabled(false);

		mRegExpMode.setSelection(prefStore
				.getBoolean(Initializer.Pref_FindRegExp));

		mSearchText.selectAll();

		mFindReplaceTargetExt.beginSession();

		// set the scope
		Point selectedRange = mViewer.getSelectedRange();
		try {
			if (mViewer.getDocument().getLineOfOffset(selectedRange.x) != mViewer
					.getDocument().getLineOfOffset(
							selectedRange.x + selectedRange.y)) {
				Point lineInfo = mFindReplaceTargetExt.getLineSelection();
				mFindReplaceTargetExt.setScope(new Region(lineInfo.x,
						lineInfo.y));
				mViewer.setSelectedRange(lineInfo.x, 0);

				mSelectedLines.setSelection(true);
			} else {
				mAll.setSelection(true);
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// when the search string is changed, the replace buttons should be
		// disabled.
		mSearchText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				setReplaceButtonsEnabled(false);

				super.keyPressed(e);
			}

		});

		mCreatedDialogArea = true;

		_instance = this;

		return comp;
	}

	private void setReplaceButtonsEnabled(boolean enabled) {
		mReplaceAndFindButton.setEnabled(enabled);
		mReplaceButton.setEnabled(enabled);
	}

	@Override
	protected void buttonPressed(int buttonId) {

		switch (buttonId) {
		case FIND:
			find();
			break;

		case REPLACE_AND_FIND:
			replaceAndFind();
			break;

		case REPLACE:
			replace();
			break;

		case REPLACE_ALL:
			replaceAll();
			break;

		case CLOSE:
			cancelPressed();
			break;
		}
	}

	private void replaceAll() {
		int offset = getOffset();
		int count = 0;

		IUndoManager undoManager = null;
		if (mViewer instanceof ITextViewerExtension6) {
			undoManager = ((ITextViewerExtension6) mViewer).getUndoManager();
		}

		if (undoManager != null) {
			undoManager.beginCompoundChange();
		}

		while (performFind(offset) != -1) {
			performReplace();
			offset = mLastSelection.x + mReplaceText.getText().length();

			++count;
		}

		if (undoManager != null) {
			undoManager.endCompoundChange();
		}

		MessageBox msgBox = new MessageBox(getShell(), SWT.OK);
		msgBox.setText("Replace All");
		msgBox.setMessage("" + count + " matches replaced");
		msgBox.open();

		FindCommand command = new FindCommand();
		command.setSelection("Replace All");
		fillCommandParameters(command);
		EventRecorder.getInstance().recordCommand(command);
	}

	private void replaceAndFind() {
		performReplace();
		int offset = getOffset();
		performFind(offset);

		FindCommand command = new FindCommand();
		command.setSelection("Replace/Find");
		command.setOffset(offset);
		fillCommandParameters(command);
		EventRecorder.getInstance().recordCommand(command);
	}

	private void replace() {
		performReplace();

		FindCommand command = new FindCommand();
		command.setSelection("Replace");
		fillCommandParameters(command);
		EventRecorder.getInstance().recordCommand(command);
	}

	private void performReplace() {
		mFindReplaceTargetExt3.replaceSelection(mReplaceText.getText(),
				mRegExpMode.getSelection());
		setReplaceButtonsEnabled(false);
	}

	private void find() {
		int offset = getOffset();
		performFind(offset);

		FindCommand command = new FindCommand();
		command.setSelection("Find");
		command.setOffset(offset);
		fillCommandParameters(command);
		EventRecorder.getInstance().recordCommand(command);
	}

	private int performFind(int offset) {
		int ret = mFindReplaceTargetExt3.findAndSelect(offset,
				mSearchText.getText(), mForward.getSelection(),
				mCaseSensitive.getSelection(), mMatchWord.getSelection(),
				mRegExpMode.getSelection());
		if (ret == -1 && mWrap.getSelection()) {
			ret = mFindReplaceTargetExt3.findAndSelect(0,
					mSearchText.getText(), mForward.getSelection(),
					mCaseSensitive.getSelection(), mMatchWord.getSelection(),
					mRegExpMode.getSelection());
		}

		if (ret != -1) {
			mLastSelection.x = ret;
			mLastSelection.y = mSearchText.getText().length();

			setReplaceButtonsEnabled(true);
		} else {
			Display.getCurrent().beep();
		}

		return ret;
	}

	private void fillCommandParameters(FindCommand command) {
		command.setSearchString(mSearchText.getText());
		command.setReplaceString(mReplaceText.getText());
		command.setSearchForward(mForward.getSelection());
		command.setScopeIsSelection(mSelectedLines.getSelection());
		command.setCaseSensitive(mCaseSensitive.getSelection());
		command.setWrapSearch(mWrap.getSelection());
		command.setMatchWholeWord(mMatchWord.getSelection());
		command.setRegExpMode(mRegExpMode.getSelection());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if (!mCreatedDialogArea) {
			return;
		}

		createButton(parent, CLOSE, "Close", false);
	}

	private int getOffset() {
		return mViewer.getSelectedRange().x + mViewer.getSelectedRange().y;
	}

	@Override
	public boolean close() {
		mFindReplaceTargetExt.endSession();
		_instance = null;

		IPreferenceStore prefStore = Activator.getDefault()
				.getPreferenceStore();
		if (prefStore != null) {
			prefStore.setValue(Initializer.Pref_FindForward,
					mForward.getSelection());
			prefStore.setValue(Initializer.Pref_FindCaseSensitive,
					mCaseSensitive.getSelection());
			prefStore.setValue(Initializer.Pref_FindWrapSearch,
					mWrap.getSelection());
			prefStore.setValue(Initializer.Pref_FindWholeWord,
					mMatchWord.getSelection());
			prefStore.setValue(Initializer.Pref_FindRegExp,
					mRegExpMode.getSelection());
		}

		return super.close();
	}

}
