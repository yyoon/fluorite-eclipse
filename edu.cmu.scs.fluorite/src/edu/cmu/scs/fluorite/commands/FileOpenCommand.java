package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.model.FileSnapshotManager;
import edu.cmu.scs.fluorite.util.Utilities;

public class FileOpenCommand extends BaseDocumentChangeEvent {
	
	public FileOpenCommand() {
	}

	public FileOpenCommand(IEditorPart editor) {
		initialize(editor);
	}

	private void initialize(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			try {
				IFileEditorInput fileInput = (IFileEditorInput) input;
				IFile file = fileInput.getFile();
				IProject project = file.getProject();
				mProjectName = project.getName();
				mFilePath = fileInput.getFile().getLocation().toOSString();

				IDocument doc = Utilities.getDocument(editor);
				if (doc != null) {
					String content = doc.get();
					calcNumericalValues(content);
	
					// Snapshot
					FileSnapshotManager snapshotManager = EventRecorder.getInstance().getFileSnapshotManager();
					if (!snapshotManager.isSame(mFilePath, content)) {
						mPrevSnapshot = snapshotManager.getContent(mFilePath);
						mSnapshot = content;
						snapshotManager.updateSnapshot(mFilePath, content);
					} else {
						mSnapshot = null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String mFilePath;
	private String mProjectName;
	private String mSnapshot;
	private String mPrevSnapshot;

	public boolean execute(IEditorPart target) {
		// Not supported yet

		// IWorkbenchWindow window =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// if (window == null) { return false; }
		//
		// IWorkbenchPage page = window.getActivePage();
		// IEditorReference[] editorReferences = page.getEditorReferences();
		return false;
	}

	public void dump() {
	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("projectName", mProjectName == null ? "null" : mProjectName);

		Map<String, Integer> numericalValues = getNumericalValues();
		if (numericalValues != null) {
			for (Map.Entry<String, Integer> pair : numericalValues.entrySet()) {
				attrMap.put(pair.getKey(), Integer.toString(pair.getValue()));
			}
		}

		return attrMap;
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("filePath", mFilePath == null ? "null" : mFilePath);
		if (mSnapshot != null) {
			dataMap.put("snapshot", mSnapshot);
		}

		return dataMap;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		String value = null;
		NodeList nodeList = null;
		
		if ((attr = commandElement.getAttributeNode("projectName")) != null) {
			value = attr.getValue();
			mProjectName = value.equals("null") ? null : value;
		}
		else {
			mProjectName = null;
		}
		
		if ((nodeList = commandElement.getElementsByTagName("filePath")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			value = textNode.getTextContent();
			mFilePath = value.equals("null") ? null : value;
		}
		else {
			mFilePath = null;
		}
		
		if ((nodeList = commandElement.getElementsByTagName("snapshot")).getLength() > 0) {
			Node textNode = nodeList.item(0);
			value = textNode.getTextContent();
			mSnapshot = checkTextValidity(value, getNumericalValues().get("docLength"));
		}
		else {
			mSnapshot = null;
		}
	}

	public String getCommandType() {
		return "FileOpenCommand";
	}

	public String getName() {
		return "File Open: \"" + mFilePath + "\"";
	}

	public String getDescription() {
		return null;
	}

	public String getCategory() {
		return EventRecorder.MacroCommandCategory;
	}

	public String getCategoryID() {
		return EventRecorder.MacroCommandCategoryID;
	}
	
	public String getSnapshot() {
		return mSnapshot;
	}
	
	public String getPrevSnapshot() {
		return mPrevSnapshot;
	}
	
	public String getFilePath() {
		return mFilePath;
	}
	
	public String getProjectName() {
		return mProjectName;
	}

	public boolean combine(ICommand anotherCommand) {
		return false;
	}

	@Override
	public void applyToDocument(IDocument doc) {
		if (getSnapshot() != null) {
			doc.set(getSnapshot());
		}
	}

	@Override
	public String applyToString(String original) {
		if (getSnapshot() != null) {
			return getSnapshot();
		} else {
			return original;
		}
	}

	@Override
	public double getY1() {
		return 0;
	}

	@Override
	public double getY2() {
		return 100;
	}
}
