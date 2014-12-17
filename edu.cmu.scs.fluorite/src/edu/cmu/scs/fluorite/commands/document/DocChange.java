package edu.cmu.scs.fluorite.commands.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.fraser.neil.plaintext.diff_match_patch;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.commands.AbstractCommand;
import edu.cmu.scs.fluorite.visitors.ExpressionCountVisitor;
import edu.cmu.scs.fluorite.visitors.NodeCountVisitor;

public abstract class DocChange extends AbstractCommand {
	
	protected Range mDeletionRange;
	
	protected Range mInsertionRange;

	private Map<String, Integer> mNumericalValues;

	protected Map<String, Integer> getNumericalValues() {
		return mNumericalValues;
	}
	
	protected void setNumericalValues(Map<String, Integer> numericalValues) {
		mNumericalValues = numericalValues;
	}

	protected void replaceNumericalValues(DocChange other) {
		mNumericalValues = other.getNumericalValues();
	}

	protected void calcNumericalValues(String documentContent) {
		mNumericalValues = new HashMap<String, Integer>();

		// Document Length
		mNumericalValues.put("docLength", documentContent.length());

		// Parse AST here.
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		parser.setSource(documentContent.toCharArray()); // set source
		parser.setStatementsRecovery(true);
		// parser.setResolveBindings(true);
		CompilationUnit compilationUnit = (CompilationUnit) parser
				.createAST(null /* IProgressMonitor */); // parse

		@SuppressWarnings("unchecked")
		List<Comment> commentList = compilationUnit.getCommentList();
		int commentLengthTotal = 0;
		for (Comment comment : commentList) {
			commentLengthTotal += comment.getLength();
		}

		// ActiveCodeLength (document length - comment length)
		mNumericalValues.put("docActiveCodeLength", documentContent.length()
				- commentLengthTotal);

		// ASTNode Count
		NodeCountVisitor ncVisitor = new NodeCountVisitor();
		compilationUnit.accept(ncVisitor);
		mNumericalValues.put("docASTNodeCount", ncVisitor.getCount());

		// Expression Node Count
		ExpressionCountVisitor ecVisitor = new ExpressionCountVisitor();
		compilationUnit.accept(ecVisitor);
		mNumericalValues.put("docExpressionCount", ecVisitor.getCount());
	}
	
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		mNumericalValues = new HashMap<String, Integer>();
		
		Attr attr = null;
		
		if ((attr = commandElement.getAttributeNode("docLength")) != null) {
			mNumericalValues.put("docLength", Integer.parseInt(attr.getValue()));
		}
		
		if ((attr = commandElement.getAttributeNode("docActiveCodeLength")) != null) {
			mNumericalValues.put("docActiveCodeLength", Integer.parseInt(attr.getValue()));
		}
		
		if ((attr = commandElement.getAttributeNode("docASTNodeCount")) != null) {
			mNumericalValues.put("docASTNodeCount", Integer.parseInt(attr.getValue()));
		}
		
		if ((attr = commandElement.getAttributeNode("docExpressionCount")) != null) {
			mNumericalValues.put("docExpressionCount", Integer.parseInt(attr.getValue()));
		}
		
		if (mNumericalValues.isEmpty()) {
			mNumericalValues = null;
		}
	}
	
	protected String checkTextValidity(String text, int desiredLength) {
/*		if (text.length() != desiredLength) {
			String temp = text.replace("\n", "\r\n");
			if (temp.length() == desiredLength) {
				text = temp;
			}
		}
		
		if (text.length() > desiredLength) {
			text = text.substring(0, desiredLength);
		}
		while (text.length() < desiredLength) {
			text = text + "-";
		}
*/		
		if (text.length() != desiredLength) {
			if (text.replace("\r\n", "\n").length() == desiredLength) {
				text = text.replace("\r\n", "\n");
			}
			else if (text.replace("\n", "\r\n").length() == desiredLength) {
				text = text.replace("\n", "\r\n");
			}
			else {
				throw new IllegalArgumentException("Text does not match the desired length!");
			}
		}
		
		return text;
	}

	public abstract void apply(IDocument doc);
	
	public abstract String apply(String original);
	
	public abstract void apply(StringBuilder builder);
	
	public abstract void applyInverse(IDocument doc);
	
	public abstract String applyInverse(String original);
	
	public abstract void applyInverse(StringBuilder builder);
	
	public abstract double getY1();
	
	public abstract double getY2();
	
	public abstract Range getDeletionRange();
	
	public abstract String getDeletedText();
	
	public abstract Range getInsertionRange();
	
	public abstract String getInsertedText();
	
	// --- Helper methods for merging two doc changes into one.
	public static boolean overlap(DocChange oldEvent, DocChange newEvent) {
		return Range.overlap(oldEvent.getInsertionRange(), newEvent.getDeletionRange());
	}
	
	public static DocChange mergeChanges(DocChange oldEvent, DocChange newEvent) {
		return mergeChanges(oldEvent, newEvent, null);
	}
	
	public static DocChange mergeChanges(DocChange oldEvent, DocChange newEvent, Document docBefore) {
		if (overlap(oldEvent, newEvent)) {
			return mergeChangesOverlap(oldEvent, newEvent, docBefore);
		} else {
			return mergeChangesApart(oldEvent, newEvent, docBefore);
		}
	}
	
	private static DocChange mergeChangesApart(
			DocChange oldEvent,
			DocChange newEvent,
			Document docBefore) {
		Range oldDeletionRange = oldEvent.getDeletionRange();
		String oldDeletedText = oldEvent.getDeletedText();
		
		Range oldInsertionRange = oldEvent.getInsertionRange();
		String oldInsertedText = oldEvent.getInsertedText();
		
		Range newDeletionRange = newEvent.getDeletionRange();
		String newDeletedText = newEvent.getDeletedText();
		
		Range newInsertionRange = newEvent.getInsertionRange();
		String newInsertedText = newEvent.getInsertedText();
		
		if (oldInsertionRange.getEndOffset() < newDeletionRange.getOffset()) {
		// Case #1. oldEvent is on the left side of the newEvent
			int midLength = newDeletionRange.getOffset() - oldInsertionRange.getEndOffset();
			String midText = getDocStringOrNullString(docBefore, oldDeletionRange.getEndOffset(), midLength);
			
			Range deletionRange = new Range(
					oldDeletionRange.getOffset(),
					oldDeletionRange.getLength() + midLength + newDeletionRange.getLength());
			String deletedText = oldDeletedText + midText + newDeletedText;
			
			Range insertionRange = new Range(
					oldDeletionRange.getOffset(),
					oldInsertionRange.getLength() + midLength + newInsertionRange.getLength());
			String insertedText = oldInsertedText + midText + newInsertedText;
			
			return createMergedChange(oldEvent, newEvent, docBefore, deletionRange, deletedText, insertionRange, insertedText);
		} else if (oldInsertionRange.getOffset() > newDeletionRange.getEndOffset()) {
		// Case #2. oldEvent is on the left side of the newEvent
			int midLength = oldDeletionRange.getOffset() - newDeletionRange.getEndOffset();
			String midText = getDocStringOrNullString(docBefore, newDeletionRange.getEndOffset(), midLength);
			
			Range deletionRange = new Range(
					newDeletionRange.getOffset(),
					newDeletionRange.getLength() + midLength + oldDeletionRange.getLength());
			String deletedText = newDeletedText + midText + oldDeletedText;
			
			Range insertionRange = new Range(
					newDeletionRange.getOffset(),
					newInsertionRange.getLength() + midLength + oldInsertionRange.getLength());
			String insertedText = newInsertedText + midText + oldInsertedText;
			
			return createMergedChange(oldEvent, newEvent, docBefore, deletionRange, deletedText, insertionRange, insertedText);
		} else {
			return null;
		}
	}
	
	private static String getDocStringOrNullString(Document doc, int pos, int length) {
		if (doc != null) {
			try {
				return doc.get(pos, length);
			} catch (BadLocationException e) {
				e.printStackTrace();
				return getNullStringOfLength(length);
			}
		} else {
			return getNullStringOfLength(length);
		}
	}
	
	private static String getNullStringOfLength(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; ++i) {
			builder.append('X');
		}
		
		return builder.toString();
	}

	private static DocChange mergeChangesOverlap(
			DocChange oldEvent,
			DocChange newEvent,
			Document docBefore) {
		Range oldDeletionRange = oldEvent.getDeletionRange();
		String oldDeletedText = oldEvent.getDeletedText();
		
		Range oldInsertionRange = oldEvent.getInsertionRange();
		String oldInsertedText = oldEvent.getInsertedText();
		
		Range newDeletionRange = newEvent.getDeletionRange();
		String newDeletedText = newEvent.getDeletedText();
		
		Range newInsertionRange = newEvent.getInsertionRange();
		String newInsertedText = newEvent.getInsertedText();
		
		// Deletion range and text.
		int leftGrow = Math.max( oldDeletionRange.getOffset() - newDeletionRange.getOffset(), 0 );
		int rightGrow = Math.max( newDeletionRange.getEndOffset() - oldInsertionRange.getEndOffset(), 0 );
		
		Range deletionRange = new Range(
				Math.min( oldDeletionRange.getOffset(), newDeletionRange.getOffset() ),
				leftGrow + oldDeletionRange.getLength() + rightGrow);
		String deletedText =
				newDeletedText.substring(0, leftGrow)
				+ oldDeletedText
				+ newDeletedText.substring(newDeletedText.length() - rightGrow);
		
		// Insertion range and text.
		Range insertionRange = new Range(
				deletionRange.getOffset(),
				Math.max(newDeletionRange.getOffset(), oldInsertionRange.getOffset()) - oldInsertionRange.getOffset()
				+ oldInsertionRange.getEndOffset() - Math.min(newDeletionRange.getEndOffset(), oldInsertionRange.getEndOffset())
				+ newInsertionRange.getLength());
		String insertedText =
				oldInsertedText.substring(0, Math.max(newDeletionRange.getOffset(), oldInsertionRange.getOffset()) - oldInsertionRange.getOffset())
				+ newInsertedText
				+ oldInsertedText.substring(oldInsertedText.length() - (oldInsertionRange.getEndOffset() - Math.min(newDeletionRange.getEndOffset(), oldInsertionRange.getEndOffset())));
		
		return createMergedChange(oldEvent, newEvent, docBefore, deletionRange, deletedText,
				insertionRange, insertedText);
	}

	private static DocChange createMergedChange(DocChange oldEvent,
			DocChange newEvent, Document docBefore, Range deletionRange,
			String deletedText, Range insertionRange, String insertedText) {
		// Strip out common prefix.
		diff_match_patch dmp = new diff_match_patch();
		int commonPrefix = dmp.diff_commonPrefix(deletedText, insertedText);
		if (commonPrefix > 0) {
			deletionRange = new Range(
					deletionRange.getOffset() + commonPrefix,
					deletionRange.getLength() - commonPrefix);
			deletedText = deletedText.substring(commonPrefix);
			
			insertionRange = new Range(
					insertionRange.getOffset() + commonPrefix,
					insertionRange.getLength() - commonPrefix);
			insertedText = insertedText.substring(commonPrefix);
		}
		
		// Strip out common suffix.
		int commonSuffix = dmp.diff_commonSuffix(deletedText, insertedText);
		if (commonSuffix > 0) {
			deletionRange = new Range(
					deletionRange.getOffset(),
					deletionRange.getLength() - commonSuffix);
			deletedText = deletedText.substring(0, deletedText.length() - commonSuffix);
			
			insertionRange = new Range(
					insertionRange.getOffset(),
					insertionRange.getLength() - commonSuffix);
			insertedText = insertedText.substring(0, insertedText.length() - commonSuffix);
		}
		
		// provide the document, to make it possible to recalculate the numbers.
		DocChange mergeResult = null;

		int startLine = 0;
		int endLine = 0;
		if (docBefore != null) {
			try {
				startLine = docBefore.getLineOfOffset(deletionRange.getOffset());
				endLine = docBefore.getLineOfOffset(deletionRange.getEndOffset());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (deletionRange.getLength() > 0 && insertionRange.getLength() > 0) {
			mergeResult = new Replace(
					deletionRange.getOffset(),
					deletionRange.getLength(),
					startLine,
					endLine,
					insertionRange.getLength(),
					deletedText,
					insertedText,
					docBefore);
		} else if (deletionRange.getLength() > 0) {
			mergeResult = new Delete(
					deletionRange.getOffset(),
					deletionRange.getLength(),
					startLine,
					endLine,
					deletedText,
					docBefore);
		} else if (insertionRange.getLength() > 0) {
			Document docAfter = null;
			if (docBefore != null) {
				docAfter = new Document(docBefore.get());
				try {
					docAfter.replace(insertionRange.getOffset(), 0, insertedText);
				} catch (BadLocationException e) {
					e.printStackTrace();
					docAfter = null;
				}
			}
			
			mergeResult = new Insert(
					insertionRange.getOffset(),
					insertedText,
					docAfter);
		}
		
		if (mergeResult != null) {
			// Wrap it.
			DocChangeWrapper wrapper = new DocChangeWrapper(mergeResult);
			mergeResult = wrapper;
			
			if (oldEvent instanceof DocChangeWrapper) {
				wrapper.addMergeIds(((DocChangeWrapper) oldEvent).getMergedFrom());
			} else {
				wrapper.addMergeId(oldEvent.getCommandIndex());
			}
			wrapper.addMergeId(newEvent.getCommandIndex());
			
			// Adjust the timestamps.
			mergeResult.setTimestamp(oldEvent.getTimestamp());
			mergeResult.setTimestamp2(newEvent.getTimestamp2());
		}
		
		return mergeResult;
	}
	

}
