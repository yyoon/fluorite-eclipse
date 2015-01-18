package edu.cmu.scs.fluorite.commands.document;

import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Represents a range in code.
 * Works as an immutable object.
 * 
 * @author YoungSeok Yoon
 *
 */
public class Range implements ISourceRange {
	private final int offset;
	private final int length;
	
	public Range(ASTNode node) {
		this(node.getStartPosition(), node.getLength());
	}
	
	public Range(int offset, int length) {
		this.offset = offset;
		this.length = length;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public int getEndOffset() {
		return getOffset() + getLength();
	}
	
	public boolean contains(Range other) {
		return contains(other, true, true);
	}
	
	public boolean contains(Range other, boolean leftInclusive, boolean rightInclusive) {
		return	contains(other.getOffset(), leftInclusive, rightInclusive) &&
				contains(other.getEndOffset(), leftInclusive, rightInclusive);
	}
	
	public boolean contains(int offset) {
		return contains(offset, true, true);
	}
	
	public boolean contains(int offset, boolean leftInclusive, boolean rightInclusive) {
		if (offset == getOffset()) { return leftInclusive; }
		if (offset == getEndOffset()) { return rightInclusive; }
		return getOffset() < offset && offset < getEndOffset();
	}
	
	public static boolean overlap(Range r1, Range r2) {
		return r1.getEndOffset() >= r2.getOffset() && r2.getEndOffset() >= r1.getOffset();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + offset;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		
		Range other = (Range) obj;
		if (length != other.length) { return false; }
		if (offset != other.offset) { return false; }
		return true;
	}
}