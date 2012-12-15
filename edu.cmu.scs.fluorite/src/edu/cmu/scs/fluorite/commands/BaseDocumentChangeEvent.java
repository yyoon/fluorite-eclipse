package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.IDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.cmu.scs.fluorite.visitors.ExpressionCountVisitor;
import edu.cmu.scs.fluorite.visitors.NodeCountVisitor;

public abstract class BaseDocumentChangeEvent extends AbstractCommand {

	private Map<String, Integer> mNumericalValues;

	protected Map<String, Integer> getNumericalValues() {
		return mNumericalValues;
	}

	protected void replaceNumericalValues(BaseDocumentChangeEvent other) {
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
	
	protected String normalizeText(String text, int desiredLength) {
		if (text.length() != desiredLength) {
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
		
		return text;
	}

	public abstract void applyToDocument(IDocument doc);

}
