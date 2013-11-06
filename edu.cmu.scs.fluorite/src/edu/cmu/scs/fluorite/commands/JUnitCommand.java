package edu.cmu.scs.fluorite.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jdt.junit.model.ITestSuiteElement;
import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class JUnitCommand extends AbstractCommand implements ITreeDataCommand {
	
	public JUnitCommand(String projectName, double elapsedTimeInSeconds, ITestElement rootElement) {
		mProjectName = projectName;
		mElapsedTimeInSeconds = elapsedTimeInSeconds;
		mRootData = new TestData(rootElement);
	}
	
	private String mProjectName;
	private double mElapsedTimeInSeconds;
	private TestData mRootData;

	@Override
	public boolean execute(IEditorPart target) {
		return false;
	}

	@Override
	public void dump() {
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("projectName", getProjectName());
		attrMap.put("elapsedTimeInSeconds", Double.toString(getElapsedTimeInSeconds()));
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		return null;
	}

	@Override
	public String getCommandType() {
		return "JUnitCommand";
	}

	@Override
	public String getName() {
		return "JUnit Command";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getCategory() {
		return EventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		return EventRecorder.MacroCommandCategoryID;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		return false;
	}
	
	public String getProjectName() {
		return mProjectName;
	}
	
	public double getElapsedTimeInSeconds() {
		return mElapsedTimeInSeconds;
	}
	
	public TestData getRootData() {
		return mRootData;
	}
	
	public static class TestData {
		
		public enum ElementType {
			TestSession,
			TestSuite,
			TestCase,
		}
		
		public TestData(ITestElement testElement) {
			if (testElement instanceof ITestRunSession) {
				ITestRunSession testRunSession = (ITestRunSession) testElement;
				mType = ElementType.TestSession;
				mName = testRunSession.getTestRunName();
			}
			else if (testElement instanceof ITestSuiteElement) {
				ITestSuiteElement testSuiteElement = (ITestSuiteElement) testElement;
				mType = ElementType.TestSuite;
				mName = testSuiteElement.getSuiteTypeName();
			} else if (testElement instanceof ITestCaseElement) {
				ITestCaseElement testCaseElement = (ITestCaseElement) testElement;
				mType = ElementType.TestCase;
				mName = testCaseElement.getTestMethodName();
			}
			
			mSucceeded = testElement.getTestResult(true) == ITestElement.Result.OK;
			
			if (testElement instanceof ITestElementContainer) {
				mChildren = new ArrayList<TestData>();
				
				ITestElementContainer testElementContainer = (ITestElementContainer) testElement;
				for (ITestElement childElement : testElementContainer.getChildren()) {
					mChildren.add(new TestData(childElement));
				}
			}
		}
		
		private ElementType mType;
		private String mName;
		private boolean mSucceeded;
		private List<TestData> mChildren;
		
		public ElementType getType() {
			return mType;
		}
		
		public String getName() {
			return mName;
		}
		
		public boolean getSucceeded() {
			return mSucceeded;
		}
		
		public List<TestData> getChildren() {
			if (mChildren != null) {
				return Collections.unmodifiableList(mChildren);
			} else {
				return null;
			}
		}
	}

	@Override
	public Object getRootElement() {
		return getRootData();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TestData) {
			List<TestData> children = ((TestData) parentElement).getChildren();
			if (children != null) {
				return children.toArray();
			} else {
				return null;
			}
		}
		
		return null;
	}

	@Override
	public String getTagName(Object element) {
		if (element instanceof TestData) {
			return ((TestData) element).getType().toString();
		}
		
		return null;
	}

	@Override
	public Map<String, String> getAttrMap(Object element) {
		if (element instanceof TestData) {
			TestData testData = (TestData) element;
			
			Map<String, String> attrMap = new HashMap<String, String>();
			attrMap.put("name", testData.getName());
			attrMap.put("succeeded", Boolean.toString(testData.getSucceeded()));
			return attrMap;
		}
		
		return null;
	}

}
