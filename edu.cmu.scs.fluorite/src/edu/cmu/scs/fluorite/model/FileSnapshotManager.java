package edu.cmu.scs.fluorite.model;

import java.util.HashMap;
import java.util.Map;

public class FileSnapshotManager {

	private static FileSnapshotManager instance = null;

	public static FileSnapshotManager getInstance() {
		if (instance == null) {
			instance = new FileSnapshotManager();
		}

		return instance;
	}

	private Map<String, String> mSnapshotMap;

	private FileSnapshotManager() {
		mSnapshotMap = new HashMap<String, String>();
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
