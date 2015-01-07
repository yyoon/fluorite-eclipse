package edu.cmu.scs.fluorite.commands.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import edu.cmu.scs.fluorite.commands.ICommand;

public class DocChangeWrapper extends DocChange {
	
	private DocChange original;
	private List<Integer> mergedFrom;
	
	public DocChangeWrapper(DocChange original) {
		if (original == null) {
			throw new IllegalArgumentException("original document change object must not be null!");
		}
		
		this.original = original;
		this.mergedFrom = new ArrayList<Integer>();
	}
	
	public DocChange getOriginal() {
		return this.original;
	}
	
	public List<Integer> getMergedFrom() {
		return Collections.unmodifiableList(this.mergedFrom);
	}
	
	public void addMergeIds(List<Integer> mergedFrom) {
		this.mergedFrom.addAll(mergedFrom);
	}
	
	public void addMergeId(int id) {
		this.mergedFrom.add(id);
	}

	@Override
	public boolean execute(IEditorPart target) {
		return this.original.execute(target);
	}

	@Override
	public void dump() {
		this.original.dump();
	}

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> map = this.original.getAttributesMap();
		map.put("mergeCount", Integer.toString(this.mergedFrom.size()));
		
		if (!this.mergedFrom.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			builder.append(this.mergedFrom.get(0));
			for (int i = 1; i < this.mergedFrom.size(); ++i) {
				builder.append(", " + this.mergedFrom.get(i));
			}
			builder.append("]");
			
			map.put("mergeIDs", builder.toString());
		}
		
		return map;
	}

	@Override
	public Map<String, String> getDataMap() {
		return this.original.getDataMap();
	}

	@Override
	public String getCommandType() {
		return this.original.getCommandType();
	}

	@Override
	public String getName() {
		return this.original.getName();
	}

	@Override
	public String getDescription() {
		return this.original.getDescription();
	}

	@Override
	public String getCategory() {
		return this.original.getCategory();
	}

	@Override
	public String getCategoryID() {
		return this.original.getCategoryID();
	}

	@Override
	public void apply(IDocument doc) {
		this.original.apply(doc);;
	}

	@Override
	public String apply(String original) {
		return this.original.apply(original);
	}

	@Override
	public void apply(StringBuilder builder) {
		this.original.apply(builder);
	}
	
	@Override
	public Range apply(Range range) {
		return this.original.apply(range);
	}

	@Override
	public void applyInverse(IDocument doc) {
		this.original.applyInverse(doc);
	}

	@Override
	public String applyInverse(String original) {
		return this.original.applyInverse(original);
	}

	@Override
	public void applyInverse(StringBuilder builder) {
		this.original.applyInverse(builder);
	}
	
	@Override
	public Range applyInverse(Range range) {
		return this.original.applyInverse(range);
	}

	@Override
	public double getY1() {
		return this.original.getY1();
	}

	@Override
	public double getY2() {
		return this.original.getY2();
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		return this.original.combine(anotherCommand);
	}

	@Override
	public Range getDeletionRange() {
		return this.original.getDeletionRange();
	}

	@Override
	public String getDeletedText() {
		return this.original.getDeletedText();
	}

	@Override
	public Range getInsertionRange() {
		return this.original.getInsertionRange();
	}

	@Override
	public String getInsertedText() {
		return this.original.getInsertedText();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[DocWrapper:");
		builder.append(getCommandIndex());
		builder.append("] ");
		
		List<Integer> mergedFrom = getMergedFrom();
		for (int i = 0; i < mergedFrom.size(); ++i) {
			if (i != 0) { builder.append(", "); }
			builder.append(mergedFrom.get(i));
		}
		
		builder.append("\n");
		builder.append(getOriginal().toString());
		
		return builder.toString();
	}
	
}
