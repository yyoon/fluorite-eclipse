package edu.cmu.scs.fluorite.model;

import java.util.HashMap;
import java.util.Map;

public class FileSnapshotManager {

	private Map<String, String> mSnapshotMap;

	public FileSnapshotManager() {
		mSnapshotMap = new HashMap<String, String>();
	}
	
	public boolean hasFile(String fullPath) {
		return mSnapshotMap.containsKey(fullPath);
	}
	
	public String getContent(String fullPath) {
		if (!hasFile(fullPath)) {
			return null;
		}
		
		return (String) mSnapshotMap.get(fullPath);
	}

	public boolean isSame(String fullPath, String currentContent) {
		if (fullPath == null) {
			return false;
		}

		if (!mSnapshotMap.containsKey(fullPath)) {
			return false;
		}

		return mSnapshotMap.get(fullPath).equals(currentContent);
	}

	public void updateSnapshot(String fullPath, String currentContent) {
		if (fullPath == null) {
			return;
		}

		mSnapshotMap.put(fullPath, currentContent);
	}
}
