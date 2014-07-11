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
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.model.EventRecorder;

public class JUnitCommand extends AbstractCommand implements ITreeDataCommand, ITypeOverridable {
	
	public JUnitCommand() {
	}
	
	public JUnitCommand(String projectName, double elapsedTimeInSeconds, ITestElement rootElement) {
		mProjectName = projectName;
		mElapsedTimeInSeconds = elapsedTimeInSeconds;
		mRootData = new TestData(rootElement);
	}
	
	private String mProjectName;
	private double mElapsedTimeInSeconds;
	private TestData mRootData;
	
	private static final String XML_ProjectName_Attr = "projectName";
	private static final String XML_ElapsedTime_Attr = "elapsedTimeInSeconds";

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		String value = null;
		NodeList nodeList = null;
		
		if ((attr = commandElement.getAttributeNode(XML_ProjectName_Attr)) != null) {
			value = attr.getValue();
			mProjectName = value.equals("null") ? null : value;
		}
		else {
			mProjectName = null;
		}
		
		if ((attr = commandElement.getAttributeNode(XML_ElapsedTime_Attr)) != null) {
			mElapsedTimeInSeconds = Double.parseDouble(attr.getValue());
		}
		
		String rootTagName = TestData.ElementType.TestSession.toString();
		if ((nodeList = commandElement.getElementsByTagName(rootTagName)).getLength() > 0) {
			Element xmlElement = (Element) nodeList.item(0);
			mRootData = new TestData(xmlElement);
		}
	}

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
		attrMap.put(XML_ProjectName_Attr, getProjectName());
		attrMap.put(XML_ElapsedTime_Attr, Double.toString(getElapsedTimeInSeconds()));
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
		return "JUnit Run: " + getProjectName()
				+ (getRootData().getSucceeded() ? " (Succeeded)" : " (Failed)");
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
				mName = testCaseElement.getTestClassName() + "." + testCaseElement.getTestMethodName();
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
		
		public TestData(Element xmlElement) {
			mType = ElementType.valueOf(xmlElement.getTagName());
			
			Attr attr;
			String value;
			NodeList nodeList;
			
			// Name
			if ((attr = xmlElement.getAttributeNode("name")) != null) {
				value = attr.getValue();
				mName = value.equals("null") ? null : value;
			}
			
			// Succeeded
			if ((attr = xmlElement.getAttributeNode("succeeded")) != null) {
				mSucceeded = Boolean.parseBoolean(attr.getValue());
			}
			
			// Children
			nodeList = xmlElement.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				if (node instanceof Element) {
					if (mChildren == null) {
						mChildren = new ArrayList<TestData>();
					}
					
					mChildren.add(new TestData((Element) node));
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

	@Override
	public String getTypeForDisplay() {
		if (getRootData() != null) {
			return getCommandType() + "(" + Boolean.toString(getRootData().getSucceeded()) + ")";
		} else {
			return getCommandType();
		}
	}

}
