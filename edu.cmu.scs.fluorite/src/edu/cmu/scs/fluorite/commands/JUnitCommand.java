package edu.cmu.scs.fluorite.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jdt.junit.model.ITestSuiteElement;
import org.eclipse.ui.IEditorPart;

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getAttributesMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
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
			TEST_SESSION,
			TEST_SUITE,
			TEST_CASE,
		}
		
		public TestData(ITestElement testElement) {
			if (testElement instanceof ITestRunSession) {
				ITestRunSession testRunSession = (ITestRunSession) testElement;
				mType = ElementType.TEST_SESSION;
				mName = testRunSession.getTestRunName();
			}
			else if (testElement instanceof ITestSuiteElement) {
				ITestSuiteElement testSuiteElement = (ITestSuiteElement) testElement;
				mType = ElementType.TEST_SUITE;
				mName = testSuiteElement.getSuiteTypeName();
			} else if (testElement instanceof ITestCaseElement) {
				ITestCaseElement testCaseElement = (ITestCaseElement) testElement;
				mType = ElementType.TEST_CASE;
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
			return Collections.unmodifiableList(mChildren);
		}
	}

	@Override
	public Object getRootElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTagName(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getAttrMap(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

}
