package edu.cmu.scs.fluorite.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
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
import edu.cmu.scs.fluorite.commands.FindCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.commands.MoveCaretCommand;
import edu.cmu.scs.fluorite.commands.SelectTextCommand;
import edu.cmu.scs.fluorite.recorders.CompletionRecorder;
import edu.cmu.scs.fluorite.recorders.DebugEventSetRecorder;
import edu.cmu.scs.fluorite.recorders.DocumentRecorder;
import edu.cmu.scs.fluorite.recorders.ExecutionRecorder;
import edu.cmu.scs.fluorite.recorders.PartRecorder;
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
	private List<ICommand> mCommands;
	private List<ICommand> mNormalCommands;
	private List<ICommand> mDocumentChangeCommands;
	private boolean mCurrentlyExecutingCommand;
	private boolean mRecordCommands;
	private IAction mSavedFindAction;

	private int mLastCaretOffset;
	private int mLastSelectionStart;
	private int mLastSelectionEnd;

	private long mStartTimestamp;
	private int mRecordCount;

	private boolean mStarted;
	private boolean mAssistSession;

	private static EventRecorder instance = null;

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

		ExecutionRecorder.getInstance().addListeners(editor);

		CompletionRecorder.getInstance().addListeners(editor);

		registerFindAction();

		styledText.getDisplay().asyncExec(new Runnable() {
			public void run() {
				mLastCaretOffset = styledText.getCaretOffset();

				mLastSelectionStart = styledText.getSelection().x;
				mLastSelectionEnd = styledText.getSelection().y;
				if (mLastSelectionStart != mLastSelectionEnd) {
					recordCommand(new SelectTextCommand(mLastSelectionStart,
							mLastSelectionEnd, mLastCaretOffset));
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

			ExecutionRecorder.getInstance().removeListeners(mEditor);

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

	public void start() {
		EventLoggerConsole.getConsole().writeln("***Started macro recording",
				EventLoggerConsole.Type_RecordingCommand);
		mCommands = new ArrayList<ICommand>();
		mNormalCommands = new ArrayList<ICommand>();
		mDocumentChangeCommands = new ArrayList<ICommand>();
		mCurrentlyExecutingCommand = false;
		mRecordCommands = true;
		mStartTimestamp = Calendar.getInstance().getTime().getTime();
		mRecordCount = 0;

		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			IPartService service = window.getPartService();
			if (service != null) {
				service.addPartListener(PartRecorder.getInstance());

				if (service.getActivePart() instanceof IEditorPart) {
					PartRecorder.getInstance().partActivated(
							service.getActivePart());
				}
			}
		}

		DebugPlugin.getDefault().addDebugEventListener(
				DebugEventSetRecorder.getInstance());

		mStarted = true;
	}

	public void stop() {
		if (mStarted == false) {
			return;
		}

		updateIncrementalFindMode();

		List<ICommand> commands = getMacroCommands();
		if (commands.size() > 0) {
			saveAsFile(commands, false);

			File logLocation = null;
			try {
				logLocation = getLogLocation();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			File autosaveFile = new File(logLocation,
					EventRecorder.getUniqueMacroNameByTimestamp(
							getStartTimestamp(), true));
			if (autosaveFile.exists()) {
				autosaveFile.delete();
			}
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
					DebugEventSetRecorder.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Events getRecordedEventsSoFar() {
		return new Events(getMacroCommands(), "",
				Long.toString(getStartTimestamp()), "", getStartTimestamp());
	}

	private void saveAsFile(List<ICommand> commands, boolean autosave) {
		Writer writer = null;

		try {
			File logLocation = getLogLocation();

			File outputFile = new File(logLocation,
					EventRecorder.getUniqueMacroNameByTimestamp(
							getStartTimestamp(), autosave));
			
			String xmlContent = persistMacro(getRecordedEventsSoFar());

			// If the file already exists, append the elements to the end.
			if (outputFile.exists()) {
				writer = new OutputStreamWriter(new FileOutputStream(outputFile, true), "UTF-8");
			}
			// Else, create a new one with the top element declaration.
			else {
				writer = new OutputStreamWriter(new FileOutputStream(outputFile, false), "UTF-8");
			}

			writer.write(xmlContent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private File getLogLocation() throws Exception {
		File logLocation = edu.cmu.scs.fluorite.plugin.Activator.getDefault()
				.getStateLocation().append("Logs").toFile();
		if (!logLocation.exists()) {
			if (!logLocation.mkdirs()) {
				throw new Exception("Could not make log directory!");
			}
		}
		return logLocation;
	}

	private boolean mIncrementalFindMode = false;
	private boolean mIncrementalFindForward = true;
	private Listener mIncrementalListener = null;

	public List<ICommand> getMacroCommands() {
		return mCommands;
	}

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

		List<ICommand> commands = mNormalCommands;
		if (newCommand instanceof BaseDocumentChangeEvent) {
			commands = mDocumentChangeCommands;
		}

		boolean combined = false;
		if (commands.size() > 0) {
			ICommand lastCommand = commands.get(commands.size() - 1);

			// See if combining with previous command is possible .
			if (lastCommand != null) {
				combined = lastCommand.combineWith(newCommand);
			}
		}

		// If combining is failed, just add it.
		if (!combined) {
			commands.add(newCommand);
			mCommands.add(newCommand);
		}

		// Autosave the log for every 100 commands just in case Eclipse crashes
		++mRecordCount;
		if (mRecordCount % 100 == 0) {
			saveAsFile(mCommands, true);
		}

		StyledText styledText = Utilities.getStyledText(Utilities
				.getActiveEditor());
		if (styledText != null) {
			this.mLastCaretOffset = styledText.getCaretOffset();
			this.mLastSelectionStart = styledText.getSelection().x;
			this.mLastSelectionEnd = styledText.getSelection().y;
		}
	}

	public long getStartTimestamp() {
		return mStartTimestamp;
	}

	public static String getUniqueMacroNameByTimestamp(long timestamp,
			boolean autosave) {
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd-HH-mm-ss-SSS");
		return "Log" + format.format(new Date(timestamp))
				+ (autosave ? "-Autosave" : "") + ".xml";
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
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
