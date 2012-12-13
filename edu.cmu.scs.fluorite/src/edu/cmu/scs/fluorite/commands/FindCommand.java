package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.dialogs.FindConfigureDialog;
import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;

public class FindCommand extends AbstractCommand {

	public FindCommand() {
		mSearchString = null;
		mSearchForward = true;
		mReplaceAll = false;
		mCaseSensitive = false;
		mMatchWholeWord = false;
		mRegExpMode = false;
		mReplaceString = null;
		mWrapSearch = false;
		mScopeIsSelection = false;
		// mFindAfterReplace=false;
		mOkPressed = false;
	}

	public FindCommand(String findString) {
		this();
		mSearchString = findString;
	}

	private boolean mReplaceAll;
	private boolean mScopeIsSelection;
	// private boolean mFindAfterReplace;
	private boolean mWrapSearch;
	private boolean mSearchForward;
	private boolean mCaseSensitive;
	private boolean mMatchWholeWord;
	private boolean mRegExpMode;
	private String mSearchString;
	private String mReplaceString;
	private boolean mOkPressed;
	private int mOffset;
	private String mSelection;

	public static final String XML_Selection_Attr = "selection";
	public static final String XML_Offset_Attr = "offset";
	public static final String XML_ReplaceAll_Attr = "replaceAll";
	public static final String XML_SelectionScope_Attr = "selectionScope";
	// public static final String XML_FindAfterReplace_Attr="findAfterReplace";
	public static final String XML_WrapSearch_Attr = "wrapSearch";
	public static final String XML_Forward_Attr = "forward";
	public static final String XML_CaseSensitive_Attr = "caseSensitive";
	public static final String XML_MatchWord_Attr = "matchWord";
	public static final String XML_RegExp_Attr = "regexp";
	public static final String XML_SearchString_Tag = "searchString";
	public static final String XML_ReplaceString_Tag = "replaceString";

	private void configureWithSearchTerm(Shell shell, String initialSearchString) {
		FindConfigureDialog dlg = new FindConfigureDialog(shell,
				initialSearchString, Utilities.getSourceViewer(EventRecorder
						.getInstance().getEditor()));
		dlg.open();
	}

	public void configureNew(Shell shell) {
		if (FindConfigureDialog.getInstance() != null) {
			// Do nothing.
			return;
		}

		String initialSearchString = null;
		IEditorPart editor = Utilities.getActiveEditor();
		if (editor != null) {
			StyledText widget = Utilities.getStyledText(editor);
			if (widget != null) {
				initialSearchString = widget.getSelectionText();
				if (initialSearchString.length() == 0)
					initialSearchString = null;
			}
		}
		configureWithSearchTerm(shell, initialSearchString);
	}

	public void dump() {
		System.out.println(getDescription());
	}

	public boolean execute(IEditorPart target) {
		IFindReplaceTarget findTarget = Utilities.getFindReplaceTarget(target);
		if (findTarget != null) {
			if (findTarget instanceof IFindReplaceTargetExtension) {
				IFindReplaceTargetExtension findTarget1 = (IFindReplaceTargetExtension) findTarget;
				findTarget1.beginSession();
				try {
					StyledText widget = Utilities.getStyledText(target);
					if (findTarget instanceof IFindReplaceTargetExtension3) {
						IFindReplaceTargetExtension3 findTarget3 = (IFindReplaceTargetExtension3) findTarget;

						int boundaryStart = 0;
						int boundaryEnd = widget.getCharCount() - 1;
						if (mScopeIsSelection) {
							Point sel = widget.getSelectionRange();
							boundaryStart = sel.x;
							boundaryEnd = sel.x + sel.y;
							findTarget1.setScope(new Region(sel.x, sel.y));
						} else {
							findTarget1.setScope(null);
						}

						// if we have a replace string
						if (mReplaceString != null && mReplaceAll) {
							// by convention, replaceall always returns true
							doReplaceAll(findTarget3, widget);
							return true;
						}

						String searchString = mSearchString;
						if (searchString == null) {
							// try getting search text from clipboard, if not
							// clipboard text, then use current selection
							try {
								Clipboard cb = new Clipboard(
										widget.getDisplay());
								TextTransfer transfer = TextTransfer
										.getInstance();
								searchString = (String) cb
										.getContents(transfer);
							} catch (IllegalArgumentException e) {
								// no text on clipboard; don't print error
							} catch (Exception e) {
								e.printStackTrace();
							}

							// backup strategy is to use the selection
							if (searchString == null)
								searchString = widget.getSelectionText();
						}
						if (searchString == null || searchString.length() == 0)
							return false;

						int startPos = widget.getCaretOffset() - 1;
						if (mScopeIsSelection) {
							if (mSearchForward)
								startPos = boundaryStart;
							else
								startPos = boundaryEnd;
						}
						int findPos = ((IFindReplaceTargetExtension3) findTarget)
								.findAndSelect(startPos, searchString,
										mSearchForward, mCaseSensitive,
										mMatchWholeWord, mRegExpMode);
						if (!isInRange(searchString, boundaryStart,
								boundaryEnd, findPos) && mWrapSearch) {
							if (!mScopeIsSelection) {
								if (mSearchForward)
									startPos = boundaryStart;
								else
									startPos = boundaryEnd;

								findPos = ((IFindReplaceTargetExtension3) findTarget)
										.findAndSelect(startPos, searchString,
												mSearchForward, mCaseSensitive,
												mMatchWholeWord, mRegExpMode);
							}
						}

						boolean inRange = (isInRange(searchString,
								boundaryStart, boundaryEnd, findPos));
						if (inRange && mReplaceString != null) {
							findTarget3.replaceSelection(mReplaceString,
									mRegExpMode);
						}
						return inRange;
					}
				} finally {
					((IFindReplaceTargetExtension) findTarget).endSession();
				}
			}
		}

		return false;
	}

	private boolean isInRange(String searchString, int boundaryStart,
			int boundaryEnd, int foundPos) {
		if (foundPos >= boundaryStart
				&& foundPos + searchString.length() <= boundaryEnd)
			return true;
		return false;
	}

	private void doReplaceAll(IFindReplaceTargetExtension3 findTarget,
			StyledText widget) {
		int docPos = 0;
		int endBoundary = 0;
		if (mScopeIsSelection) {
			docPos = widget.getSelection().x;
			endBoundary = widget.getSelection().y;
		}

		while (true) {
			// perform a find
			int foundPos = findTarget.findAndSelect(docPos, mSearchString,
					mSearchForward, mCaseSensitive, mMatchWholeWord,
					mRegExpMode);
			if (foundPos < 0 || (mScopeIsSelection && foundPos >= endBoundary))
				break;

			// replace the text
			int oldDocLength = widget.getCharCount();
			int searchStringSize = widget.getSelectionCount(); // accounts for
																// regular
																// expressions
			findTarget.replaceSelection(mReplaceString, mRegExpMode);
			int newDocLength = widget.getCharCount();
			int amtAdded = newDocLength - oldDocLength;
			docPos = foundPos + searchStringSize + amtAdded;
			endBoundary += amtAdded;

		}
	}

	public String getCategory() {
		return EventRecorder.MacroCommandCategory;
	}

	public String getCategoryID() {
		return EventRecorder.MacroCommandCategoryID;
	}

	public String getDescription() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Perform Find with the following parameters:\n");
		buffer.append("Search String: " + mSearchString + "\n");
		buffer.append("Case Sensitive: " + mCaseSensitive + "\n");
		buffer.append("Search Forward: " + mSearchForward + "\n");
		buffer.append("Match whole word: " + mMatchWholeWord + "\n");
		buffer.append("Selection scope: " + mScopeIsSelection + "\n");
		buffer.append("Wrap Search: " + mWrapSearch + "\n");
		// buffer.append("FindAfterReplace: "+mFindAfterReplace+"\n");
		buffer.append("Reg Exp Mode: " + mRegExpMode + "\n");
		buffer.append("Replace All: " + mReplaceAll + "\n");
		buffer.append("Replace String: " + mReplaceString + "\n");
		return buffer.toString();
	}

	public String getName() {
		return "Find "
				+ ((mSearchString != null) ? mSearchString
						: "<clipboard/selection>");
	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put(XML_Selection_Attr, mSelection);
		attrMap.put(XML_Offset_Attr, Integer.toString(mOffset));
		attrMap.put(XML_Forward_Attr, Boolean.toString(mSearchForward));
		attrMap.put(XML_CaseSensitive_Attr, Boolean.toString(mCaseSensitive));
		attrMap.put(XML_RegExp_Attr, Boolean.toString(mRegExpMode));
		attrMap.put(XML_MatchWord_Attr, Boolean.toString(mMatchWholeWord));
		attrMap.put(XML_ReplaceAll_Attr, Boolean.toString(mReplaceAll));
		// attrMap.put(XML_FindAfterReplace_Attr,
		// Boolean.toString(mFindAfterReplace));
		attrMap.put(XML_SelectionScope_Attr,
				Boolean.toString(mScopeIsSelection));
		attrMap.put(XML_WrapSearch_Attr, Boolean.toString(mWrapSearch));
		return attrMap;
	}

	@Override
	public void createFrom(Element commandElement) {
		throw new RuntimeException("not implemented");
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (mSearchString != null)
			dataMap.put(XML_SearchString_Tag, mSearchString);
		if (mReplaceString != null)
			dataMap.put(XML_ReplaceString_Tag, mReplaceString);

		return dataMap;
	}

	public String getCommandType() {
		return "FindCommand";
	}

	public void setSearchForward(boolean forward) {
		mSearchForward = forward;
	}

	public boolean isReplaceAll() {
		return mReplaceAll;
	}

	public void setReplaceAll(boolean replaceAll) {
		mReplaceAll = replaceAll;
	}

	public boolean isCaseSensitive() {
		return mCaseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		mCaseSensitive = caseSensitive;
	}

	public boolean isMatchWholeWord() {
		return mMatchWholeWord;
	}

	public void setMatchWholeWord(boolean matchWholeWord) {
		mMatchWholeWord = matchWholeWord;
	}

	public boolean isRegExpMode() {
		return mRegExpMode;
	}

	public void setRegExpMode(boolean regExpMode) {
		mRegExpMode = regExpMode;
	}

	public String getSearchString() {
		return mSearchString;
	}

	public void setSearchString(String searchString) {
		mSearchString = searchString;
	}

	public String getReplaceString() {
		return mReplaceString;
	}

	public void setReplaceString(String replaceString) {
		mReplaceString = replaceString;
	}

	public boolean isSearchForward() {
		return mSearchForward;
	}

	public boolean isScopeIsSelection() {
		return mScopeIsSelection;
	}

	public void setScopeIsSelection(boolean scopeIsSelection) {
		mScopeIsSelection = scopeIsSelection;
	}

	// public boolean isFindAfterReplace() {
	// return mFindAfterReplace;
	// }
	//
	// public void setFindAfterReplace(boolean findAfterReplace) {
	// mFindAfterReplace = findAfterReplace;
	// }

	public boolean isWrapSearch() {
		return mWrapSearch;
	}

	public void setWrapSearch(boolean wrapSearch) {
		mWrapSearch = wrapSearch;
	}

	public int getOffset() {
		return mOffset;
	}

	public void setOffset(int offset) {
		mOffset = offset;
	}

	public String getSelection() {
		return mSelection;
	}

	public void setSelection(String selection) {
		mSelection = selection;
	}

	public void setOkPressed(boolean okPressed) {
		mOkPressed = okPressed;
	}

	public boolean getOkPressed() {
		return mOkPressed;
	}

	public boolean combine(ICommand anotherCommand) {
		return false;
	}
}
