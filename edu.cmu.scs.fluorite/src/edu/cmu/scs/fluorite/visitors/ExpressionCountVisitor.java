package edu.cmu.scs.fluorite.visitors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;

public class ExpressionCountVisitor extends ASTVisitor {

	public ExpressionCountVisitor() {
		this.count = 0;
	}

	private int count;

	public int getCount() {
		return count;
	}

	@Override
	public void preVisit(ASTNode node) {
		super.preVisit(node);

		if (node instanceof Expression) {
			++count;
		}
	}

}
