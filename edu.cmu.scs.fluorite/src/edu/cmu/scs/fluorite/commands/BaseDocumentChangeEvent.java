package edu.cmu.scs.fluorite.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;

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
		ASTParser parser = ASTParser.newParser(AST.JLS3); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		parser.setSource(documentContent.toCharArray()); // set source
		parser.setStatementsRecovery(true);
//		parser.setResolveBindings(true);
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
		
		@SuppressWarnings("unchecked")
		List<Comment> commentList = compilationUnit.getCommentList();
		int commentLengthTotal = 0;
		for (Comment comment : commentList) {
			commentLengthTotal += comment.getLength();
		}
		
		// ActiveCodeLength (document length - comment length)
		mNumericalValues.put("docActiveCodeLength", documentContent.length() - commentLengthTotal);
		
		// ASTNode Count
		NodeCountVisitor ncVisitor = new NodeCountVisitor();
		compilationUnit.accept(ncVisitor);
		mNumericalValues.put("docASTNodeCount", ncVisitor.getCount());
		
		// Expression Node Count
		ExpressionCountVisitor ecVisitor = new ExpressionCountVisitor();
		compilationUnit.accept(ecVisitor);
		mNumericalValues.put("docExpressionCount", ecVisitor.getCount());
	}

}
