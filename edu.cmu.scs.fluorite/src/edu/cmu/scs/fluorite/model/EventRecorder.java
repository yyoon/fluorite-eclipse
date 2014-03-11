package edu.cmu.scs.fluorite.model;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.actions.FindAction;
import edu.cmu.scs.fluorite.commands.BaseDocumentChangeEvent;
import edu.cmu.scs.fluorite.commands.FileOpenCommand;
import edu.cmu.scs.fluorite.commands.FindCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.commands.MoveCaretCommand;
import edu.cmu.scs.fluorite.commands.SelectTextCommand;
import edu.cmu.scs.fluorite.preferences.Initializer;
import edu.cmu.scs.fluorite.recorders.CompletionRecorder;
import edu.cmu.scs.fluorite.recorders.DocumentRecorder;
import edu.cmu.scs.fluorite.recorders.EclipseCommandRecorder;
import edu.cmu.scs.fluorite.recorders.JUnitRecorder;
import edu.cmu.scs.fluorite.recorders.PartRecorder;
import edu.cmu.scs.fluorite.recorders.RunRecorder;
import edu.cmu.scs.fluorite.recorders.StyledTextEventRecorder;
import edu.cmu.scs.fluorite.util.EventLoggerConsole;
import edu.cmu.scs.fluorite.util.Utilities;

public class EventRecorder {

	public static final String MacroCommandCategory = "EventLogger utility command";
	public static final String MacroCommandCategoryID = "eventlogger.category.utility.command";
	public static final String UserMacroCategoryID = "eventlogger.category.usermacros";
	public static final String UserMacroCategoryName = "User defined editor macros";
	public static final String AnnotationCategory = "Annotation";
	public static final String AnnotationCategoryID = "eventlogger.category.annotation";
	public static final String DocumentChangeCategory = "Every document changes";
	public static final String DocumentChangeCategoryID = "eventlogger.category.documentChange";

	public static final String XML_Macro_Tag = "Events";
	public static final String XML_ID_Tag = "__id";
	public static final String XML_Description_Tag = "description";
	public static final String XML_Command_Tag = "Command";
	public static final String XML_DocumentChange_Tag = "DocumentChange";
	public static final String XML_Annotation_Tag = "Annotation";
	public static final String XML_CommandType_ATTR = "_type";
	public static final String PREF_USER_MACRO_DEFINITIONS = "Preference_UserMacroDefinitions";

	private IEditorPart mEditor;
	private LinkedList<ICommand> mCommands;
	private LinkedList<ICommand> mNormalCommands;
	private LinkedList<ICommand> mDocumentChangeCommands;
	private boolean mCurrentlyExecutingCommand;
	private boolean mRecordCommands;
	private IAction mSavedFindAction;

	private int mLastCaretOffset;
	private int mLastSelectionStart;
	private int mLastSelectionEnd;

	private long mStartTimestamp;

	private boolean mStarted;
	private boolean mAssistSession;

	private boolean mCombineCommands;
	private boolean mNormalCommandCombinable;
	private boolean mDocChangeCombinable;
	private int mCombineTimeThreshold;
	
	private BaseDocumentChangeEvent mLastFiredDocumentChange;
	
	private Timer mTimer;
	private TimerTask mNormalTimerTask;
	private TimerTask mDocChangeTimerTask;
	
	private ListenerList mDocumentChangeListeners;
	private ListenerList mCommandExecutionListeners;

	private List<Runnable> mScheduledTasks;
	
	private FileSnapshotManager mFileSnapshotManager;

	private static EventRecorder instance = null;

	private static final Logger LOGGER = Logger.getLogger(EventRecorder.class
			.getName());

	public static EventRecorder getInstance() {
		if (instance == null) {
			instance = new EventRecorder();
		}

		return instance;
	}

	private EventRecorder() {
		mEditor = null;

		mStarted = false;
		mAssistSession = false;
		
		mDocumentChangeListeners = new ListenerList();
		mCommandExecutionListeners = new ListenerList();
		
		mTimer = new Timer();
		
		mScheduledTasks = new ArrayList<Runnable>();
		
		mFileSnapshotManager = new FileSnapshotManager();
	}

	public void setCurrentlyExecutingCommand(boolean executingCommand) {
		mCurrentlyExecutingCommand = executingCommand;
	}

	public boolean isCurrentlyExecutingCommand() {
		return mCurrentlyExecutingCommand;
	}

	public void setIncrementalFindForward(boolean incrementalFindForward) {
		mIncrementalFindForward = incrementalFindForward;
	}

	public boolean isIncrementalFindForward() {
		return mIncrementalFindForward;
	}

	public void setIncrementalFindMode(boolean incrementalFindMode) {
		mIncrementalFindMode = incrementalFindMode;
	}

	public boolean isIncrementalFindMode() {
		return mIncrementalFindMode;
	}

	public void setIncrementalListener(Listener incrementalListener) {
		mIncrementalListener = incrementalListener;
	}

	public int getLastCaretOffset() {
		return mLastCaretOffset;
	}

	public int getLastSelectionStart() {
		return mLastSelectionStart;
	}

	public int getLastSelectionEnd() {
		return mLastSelectionEnd;
	}

	public void setAssistSession(boolean assistSession) {
		mAssistSession = assistSession;
	}

	public boolean isAssistSession() {
		return mAssistSession;
	}

	public void addDocumentChangeListener(DocumentChangeListener docChangeListener) {
		mDocumentChangeListeners.add(docChangeListener);
	}
	
	public void removeDocumentChangeListener(DocumentChangeListener docChangeListener) {
		mDocumentChangeListeners.remove(docChangeListener);
	}
	
	public void addCommandExecutionListener(CommandExecutionListener cmdExecListener) {
		mCommandExecutionListeners.add(cmdExecListener);
	}
	
	public void removeCommandExecutionListener(CommandExecutionListener cmdExecListener) {
		mCommandExecutionListeners.remove(cmdExecListener);
	}
	
	public void setCombineCommands(boolean enabled) {
		mCombineCommands = enabled;
	}
	
	public boolean getCombineCommands() {
		return mCombineCommands;
	}
	
	public void setCombineTimeThreshold(int newThreshold) {
		mCombineTimeThreshold = newThreshold;
	}
	
	public int getCombineTimeThreshold() {
		return mCombineTimeThreshold;
	}
	
	private Timer getTimer() {
		return mTimer;
	}
	
	public FileSnapshotManager getFileSnapshotManager() {
		return mFileSnapshotManager;
	}
	
	public void fireActiveFileChangedEvent(FileOpenCommand foc) {
		for (Object listenerObj : mDocumentChangeListeners.getListeners()) {
			((DocumentChangeListener)listenerObj).activeFileChanged(foc);
		}
	}
	
	public void fireDocumentChangedEvent(BaseDocumentChangeEvent docChange) {
		for (Object listenerObj : mDocumentChangeListeners.getListeners()) {
			((DocumentChangeListener)listenerObj).documentChanged(docChange);
		}
	}
	
	public void fireCommandExecutedEvent(ICommand command) {
		for (Object listenerObj : mCommandExecutionListeners.getListeners()) {
			((CommandExecutionListener)listenerObj).commandExecuted(command);
		}
	}
	
	public void fireLastDocumentChangeFinalizedEvent() {
		if (mDocumentChangeCommands != null && mDocumentChangeCommands.size() > 0) {
			fireDocumentChangeFinalizedEvent((BaseDocumentChangeEvent) mDocumentChangeCommands.get(mDocumentChangeCommands.size() - 1));
		}
	}
	
	public synchronized void fireDocumentChangeFinalizedEvent(BaseDocumentChangeEvent docChange) {
		if (docChange instanceof FileOpenCommand) { return; }
		
		if (docChange == mLastFiredDocumentChange) { return; }

		for (Object listenerObj : mDocumentChangeListeners.getListeners()) {
			((DocumentChangeListener)listenerObj).documentChangeFinalized(docChange);
		}
		
		mLastFiredDocumentChange = docChange;
		mDocChangeCombinable = false;
	}
	
	public void fireDocumentChangeUpdatedEvent(BaseDocumentChangeEvent docChange) {
		for (Object listenerObj : mDocumentChangeListeners.getListeners()) {
			((DocumentChangeListener)listenerObj).documentChangeUpdated(docChange);
		}
	}
	
	public void fireDocumentChangeAmendedEvent(BaseDocumentChangeEvent oldDocChange, BaseDocumentChangeEvent newDocChange) {
		for (Object listenerObj : mDocumentChangeListeners.getListeners()) {
			((DocumentChangeListener)listenerObj).documentChangeAmended(oldDocChange, newDocChange);
		}
	}

	public void addListeners() {
		addListeners(Utilities.getActiveEditor());
	}

	public void addListeners(IEditorPart editor) {
		mEditor = editor;
		final StyledText styledText = Utilities.getStyledText(mEditor);
		final ISourceViewer viewer = Utilities.getSourceViewer(mEditor);

		if (styledText == null || viewer == null)
			return;

		StyledTextEventRecorder.getInstance().addListeners(editor);

		DocumentRecorder.getInstance().addListeners(editor);

		EclipseCommandRecorder.getInstance().addListeners(editor);

		CompletionRecorder.getInstance().addListeners(editor);

		registerFindAction();
		
		final ITextViewerExtension5 ext5 = Utilities.getTextViewerExtension5(editor);

		styledText.getDisplay().asyncExec(new Runnable() {
			public void run() {
				mLastCaretOffset = styledText.getCaretOffset();

				mLastSelectionStart = styledText.getSelection().x;
				mLastSelectionEnd = styledText.getSelection().y;
				if (mLastSelectionStart != mLastSelectionEnd) {
					int docStart = ext5.widgetOffset2ModelOffset(mLastSelectionStart);
					int docEnd = ext5.widgetOffset2ModelOffset(mLastSelectionEnd);
					int docOffset = ext5.widgetOffset2ModelOffset(mLastCaretOffset);
					recordCommand(new SelectTextCommand(
							mLastSelectionStart, mLastSelectionEnd, mLastCaretOffset,
							docStart, docEnd, docOffset));
				} else {
					recordCommand(new MoveCaretCommand(mLastCaretOffset, viewer
							.getSelectedRange().x));
				}
			}
		});
	}

	public void removeListeners() {
		if (mEditor == null) {
			return;
		}

		try {
			StyledTextEventRecorder.getInstance().removeListeners(mEditor);

			DocumentRecorder.getInstance().removeListeners(mEditor);

			EclipseCommandRecorder.getInstance().removeListeners(mEditor);

			CompletionRecorder.getInstance().removeListeners(mEditor);

			unregisterFindAction();
		} catch (Exception e) {
			// catch all exceptions since we don't want anything bad that
			// happens to prevent other cleanup
			e.printStackTrace();
		}

		mEditor = null;
	}

	private void registerFindAction() {
		AbstractTextEditor ate = findTextEditor(getEditor());
		if (ate != null) {
			mSavedFindAction = ate.getAction(ITextEditorActionConstants.FIND);
			IAction findAction = new FindAction();
			findAction
					.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE);
			ate.setAction(ITextEditorActionConstants.FIND, findAction);
		}

	}

	private void unregisterFindAction() {
		AbstractTextEditor ate = findTextEditor(getEditor());
		if (ate != null) {
			ate.setAction(ITextEditorActionConstants.FIND, mSavedFindAction);
		}
	}

	public static AbstractTextEditor findTextEditor(IEditorPart editor) {
		if (editor instanceof AbstractTextEditor)
			return (AbstractTextEditor) editor;

		if (editor instanceof MultiPageEditorPart) {
			MultiPageEditorPart mpe = (MultiPageEditorPart) editor;
			IEditorPart[] parts = mpe.findEditors(editor.getEditorInput());
			for (IEditorPart editorPart : parts) {
				if (editorPart instanceof AbstractTextEditor) {
					return (AbstractTextEditor) editorPart;
				}
			}
		}

		return null;
	}

	public void scheduleTask(Runnable runnable) {
		if (mStarted) {
			runnable.run();
		}
		else {
			mScheduledTasks.add(runnable);
		}
	}

	public void start() {
		EventLoggerConsole.getConsole().writeln("***Started macro recording",
				EventLoggerConsole.Type_RecordingCommand);
		mCommands = new LinkedList<ICommand>();
		mNormalCommands = new LinkedList<ICommand>();
		mDocumentChangeCommands = new LinkedList<ICommand>();
		mCurrentlyExecutingCommand = false;
		mRecordCommands = true;
		mStartTimestamp = Calendar.getInstance().getTime().getTime();

		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			IPartService service = window.getPartService();
			if (service != null) {
				service.addPartListener(PartRecorder.getInstance());
				
				if (service.getActivePartReference() instanceof IEditorReference) {
					PartRecorder.getInstance().partActivated(service.getActivePartReference());
				}
			}
		}

		DebugPlugin.getDefault().addDebugEventListener(
				RunRecorder.getInstance());
		
		JUnitCore.addTestRunListener(JUnitRecorder.getInstance());

		initializeLogger();

		// Set the combine time threshold.
		IPreferenceStore prefStore = edu.cmu.scs.fluorite.plugin.Activator
				.getDefault().getPreferenceStore();
		
		setCombineCommands(prefStore.getBoolean(Initializer.Pref_CombineCommands));
		setCombineTimeThreshold(prefStore.getInt(Initializer.Pref_CombineTimeThreshold));

		mStarted = true;
		
		// Execute all the scheduled tasks.
		for (Runnable runnable : mScheduledTasks) {
			runnable.run();
		}
	}

	public void stop() {
		if (mStarted == false) {
			return;
		}

		updateIncrementalFindMode();

		// Flush the commands that are not yet logged into the file.
		for (ICommand command : mCommands) {
			LOGGER.log(Level.FINE, null, command);
		}

		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window != null) {
					IPartService partService = window.getPartService();
					if (partService != null) {
						partService.removePartListener(PartRecorder
								.getInstance());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			DebugPlugin.getDefault().removeDebugEventListener(
					RunRecorder.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// purge timer events.
		getTimer().cancel();
		getTimer().purge();
	}

	private void initializeLogger() {
		LOGGER.setLevel(Level.FINE);

		File outputFile = null;
		try {
			File logLocation = Utilities.getLogLocation();
			outputFile = new File(logLocation,
					Utilities.getUniqueLogNameByTimestamp(
							getStartTimestamp(), false));

			FileHandler handler = new FileHandler(outputFile.getPath());
			handler.setEncoding("UTF-8");
			handler.setFormatter(new FluoriteXMLFormatter(getStartTimestamp()));

			LOGGER.addHandler(handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Events getRecordedEventsSoFar() {
		return getRecordedEvents(mCommands);
	}

	public Events getRecordedEvents(List<ICommand> commands) {
		return new Events(commands, "", Long.toString(getStartTimestamp()), "",
				getStartTimestamp());
	}

	private boolean mIncrementalFindMode = false;
	private boolean mIncrementalFindForward = true;
	private Listener mIncrementalListener = null;

	public IEditorPart getEditor() {
		return mEditor;
	}

	public void updateIncrementalFindMode() {
		if (!mIncrementalFindMode)
			return;

		StyledText st = Utilities.getStyledText(Utilities.getActiveEditor());
		Listener[] currentListeners = st.getListeners(SWT.MouseUp);
		boolean stillInList = false;
		for (Listener listener : currentListeners) {
			if (listener == mIncrementalListener) {
				stillInList = true;
				break;
			}
		}

		if (!stillInList) {
			mIncrementalFindMode = false;

			// add find command representing whatever is currently selected
			String selectionText = st.getSelectionText();
			FindCommand findCommand = new FindCommand(selectionText);
			findCommand.setSearchForward(mIncrementalFindForward);
			recordCommand(findCommand);
			System.out.println("Incremental find string: " + selectionText);
		}
	}

	// @Override
	// public void modifyText(ExtendedModifyEvent event)
	// {
	// // if (!mCurrentlyExecutingCommand)
	// // {
	// // System.out.println(event);
	// // }
	// // //the text modify event is used to handle character insert/delete
	// events
	// // if (event.replacedText.length()>0)
	// // {
	// // mCommands.add(new StyledTextCommand(ST.DELETE_NEXT));
	// // }
	// //
	// // if ()
	// }

	public void endIncrementalFindMode() {

	}

	public void pauseRecording() {
		mRecordCommands = false;
	}

	public void resumeRecording() {
		mRecordCommands = true;
	}
	
	public void amendLastDocumentChange(BaseDocumentChangeEvent newDocChange, boolean usePreviousTimestamp) {
		BaseDocumentChangeEvent lastDocChange = (BaseDocumentChangeEvent) mDocumentChangeCommands.getLast();
		int index = mCommands.indexOf(lastDocChange);
		
		// Make sure that this document change is finalized!
		// If not, finalize it now!
		fireDocumentChangeFinalizedEvent(lastDocChange);

		// Preserve the command index.
		newDocChange.setCommandIndex(lastDocChange.getCommandIndex());
		
		// Timestamp
		long timestamp = Calendar.getInstance().getTime().getTime();
		timestamp -= mStartTimestamp;
		
		newDocChange.setTimestamp(usePreviousTimestamp ? lastDocChange.getTimestamp() : timestamp);
		newDocChange.setTimestamp2(timestamp);
		
		mDocumentChangeCommands.set(mDocumentChangeCommands.size() - 1, newDocChange);
		mCommands.set(index, newDocChange);
		
		// To prevent from firing finalized event more than once.
		mLastFiredDocumentChange = newDocChange;
		
		fireDocumentChangeAmendedEvent(lastDocChange, newDocChange);
	}
	
	public void recordCommand(ICommand newCommand) {
		if (!mRecordCommands) {
			return;
		}

		long timestamp = Calendar.getInstance().getTime().getTime();
		timestamp -= mStartTimestamp;
		EventLoggerConsole.getConsole().writeln(
				"*Command added to macro: " + newCommand.getName()
						+ "\ttimestamp: " + timestamp,
				EventLoggerConsole.Type_RecordingCommand);
		newCommand.setTimestamp(timestamp);
		newCommand.setTimestamp2(timestamp);

		final boolean isNewCmdDocChange = (newCommand instanceof BaseDocumentChangeEvent);
		final LinkedList<ICommand> commands = isNewCmdDocChange ? mDocumentChangeCommands : mNormalCommands;

		boolean combined = false;
		final ICommand lastCommand = commands.size() > 0 ? commands.get(commands.size() - 1) : null;
		final boolean isLastCmdDocChange = (lastCommand instanceof BaseDocumentChangeEvent);

		// See if combining with previous command is possible .
		if (lastCommand != null && isCombineEnabled(newCommand, lastCommand, isNewCmdDocChange)) {
			combined = lastCommand.combineWith(newCommand);
		}
		
		// If combined, fire the updated event.
		if (combined && isLastCmdDocChange) {
			fireDocumentChangeUpdatedEvent((BaseDocumentChangeEvent)lastCommand);
		}

		// If combining is failed, just add it.
		if (!combined) {
			commands.add(newCommand);
			mCommands.add(newCommand);
			
			if (newCommand instanceof BaseDocumentChangeEvent) {
				if (!(newCommand instanceof FileOpenCommand)) {
					fireDocumentChangedEvent((BaseDocumentChangeEvent)newCommand);
				}
				
				if (isLastCmdDocChange && lastCommand != mLastFiredDocumentChange) {
					fireDocumentChangeFinalizedEvent((BaseDocumentChangeEvent)lastCommand);
				}
			}
			else {
				fireCommandExecutedEvent(newCommand);
			}
		}

		// Log to the file.
		while (!mCommands.isEmpty()) {
			ICommand firstCmd = mCommands.getFirst();
			LinkedList<ICommand> typeList = firstCmd instanceof BaseDocumentChangeEvent
					? mDocumentChangeCommands
					: mNormalCommands;
			
			if (typeList.size() <= 1 || typeList.getFirst() != firstCmd) {
				break;
			}
			
			LOGGER.log(Level.FINE, null, firstCmd);
			
			typeList.removeFirst();
			mCommands.removeFirst();
		}

		StyledText styledText = Utilities.getStyledText(Utilities
				.getActiveEditor());
		if (styledText != null) {
			this.mLastCaretOffset = styledText.getCaretOffset();
			this.mLastSelectionStart = styledText.getSelection().x;
			this.mLastSelectionEnd = styledText.getSelection().y;
		}
		
		// Deal with timer.
		// TODO Refactor!! maybe use State pattern or something, using inner classes.
		if (isNewCmdDocChange) {
			if (mDocChangeTimerTask != null) { mDocChangeTimerTask.cancel(); }
			
			mDocChangeTimerTask = new TimerTask() {
				public void run() {
					mDocChangeCombinable = false;
					
					try {
					
						final ICommand lastCommand = (mDocumentChangeCommands.size() > 0) ? mDocumentChangeCommands.get(mDocumentChangeCommands.size() - 1) : null;
						if (lastCommand != null && lastCommand != mLastFiredDocumentChange) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									fireDocumentChangeFinalizedEvent((BaseDocumentChangeEvent)lastCommand);
								}
							});
						}
					
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			getTimer().schedule(mDocChangeTimerTask, (long)getCombineTimeThreshold());
			mDocChangeCombinable = true;
		}
		else {
			if (mNormalTimerTask != null) { mNormalTimerTask.cancel(); }
			
			mNormalTimerTask = new TimerTask() {
				public void run() {
					mNormalCommandCombinable = false;
				}
			};
			getTimer().schedule(mNormalTimerTask, (long)getCombineTimeThreshold());
			mNormalCommandCombinable = true;
		}
	}

	private boolean isCombineEnabled(ICommand newCommand, ICommand lastCommand, boolean isDocChange) {
		return getCombineCommands() && (isDocChange ? mDocChangeCombinable : mNormalCommandCombinable);
	}

	public long getStartTimestamp() {
		return mStartTimestamp;
	}

	public static Document createDocument(Events events) {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			// create the root element and add it to the document
			Element root = doc.createElement(XML_Macro_Tag);
			doc.appendChild(root);
			events.persist(doc, root);

			return doc;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	public static String outputXML(Document doc) {
		try {
			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();
			return xmlString;
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	public static String persistMacro(Events macro) {
		Document doc = createDocument(macro);
		return outputXML(doc);
	}
}
