package name.fraser.neil.plaintext;

import java.util.LinkedList;

public class diff_match_patch_ext extends diff_match_patch {

	/**
	 * Line-based diffs.
	 * https://code.google.com/p/google-diff-match-patch/wiki/LineOrWordDiffs
	 * 
	 * @param text1 The first text
	 * @param text2 The second text
	 * @return list of Diff objects.
	 */
	public LinkedList<Diff> diff_lines_only(String text1, String text2) {
		final LinesToCharsResult lines = diff_linesToChars(text1, text2);
		final LinkedList<Diff> diffs = diff_main(lines.chars1, lines.chars2);
		
		diff_charsToLines(diffs, lines.lineArray);
		
		diff_cleanupSemantic(diffs);
		
		return diffs;
	}
	
}
