package edu.cmu.scs.fluorite.commands.document;

import org.eclipse.jdt.core.ISourceRange;

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