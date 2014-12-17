package edu.cmu.scs.fluorite.commands.document;

import org.eclipse.jdt.core.ISourceRange;

public class Range implements ISourceRange {
	private int offset;
	private int length;
	
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
}