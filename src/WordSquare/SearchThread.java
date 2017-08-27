package WordSquare;

import java.util.regex.Pattern;

/**
 * {@code SearchThread} is a runnable process to find all valid word squares
 * for a single search branch.  A {@code SearchThread} treats all user-input
 * words plus a single "branch word" as fixed for this word square and then
 * attempts to fill the empty positions with word bank words.
 * Upon search, {@code WordSquare} generates a list of branch words and then
 * creates a {@code SearchThread} instance for each branch word.
 * {@code SearchThread} instances run simultaneously, so special care has
 * been taken to avoid thread interference.
 */
public class SearchThread implements Runnable {  //better to extend Thread?
    private WordSquare ws;

    /**
     * The constructor requires a reference to the {@code WordSquare} instance
     * that created the {@code SearchThread}.  This is necessary since the
     * {@code SearchThread} retrieves search information from {@code WordSquare}
     * and then adds completed word square solutions to a list maintained by the
     * {@code WordSquare} instance.
     * @param ws The {@code WordSquare} instance coordinating the search.
     */
    SearchThread(WordSquare ws) {
        this.ws = ws;
    }

    /**
     * {@code SearchThread} runs a brute-force algorithm to find all valid
     * word square solutions.  A 2D array {@code searchRows} acts as a road
     * map to direct construction of a valid word square.  Blank rows are
     * filled in order (as provided by {@code SearchRows}, requesting matching
     * words from a {@code Dictionary} instance.  If a match is found, search
     * moves to the next blank row.  If no match is found, search moves back
     * a row to continue with the next valid match for the previous row.
     */
    @Override
    public void run() {
        Dictionary dict = new Dictionary();
        Score scorer = new Score();
        String[] squareWords = ws.getSquareWords(); // A partial word square
        int[][] searchRows = ws.getSearchRows(); // The blank rows to search
        String branchWord = ws.getBranchWord(); // Unique to this thread
        squareWords[searchRows[0][0]] = branchWord; // Make branch word fixed
        int len = branchWord.length(); // The length of valid matches
        int pos = 1; // Set first position to search. (0 is branch word.)
        int squarePos; // Pulled from searchRows during the loop.
        int dictPos; // Pulled from searchRows during the loop.

        /*--------------------------------------------
        * Iterate forward and backward through searchRows attempting to build
        * complete word squares.  If search position reaches the end, a valid
        * solution is found.  If search position reaches zero, no more
        * solutions are possible (since index 0 is the branch word, which
        * is fixed for each individual search thread).
        *
        * Note that searchRows have been reordered to prune non-productive
        * branches. Accordingly, searchRows index ('pos') is
        * not related to word square position ('squarePos').
        * --------------------------------------------*/
        while (pos >= 1) {
            // Get squarePos and current dictPos for this row.
            // Remove prior match (if any) from squareWords.
            squarePos = searchRows[pos][0];
            dictPos = searchRows[pos][1];
            squareWords[squarePos] = null;

            // Try to make a match for this row.
            Pattern searchPattern = dict.buildSearchPattern(
                    squareWords, squarePos, len);
            Match match = dict.getNextMatch(searchPattern, dictPos, len);

            if (match == null) { // end of wordBank reached without a match
                // Set dictPos back to zero for this failed row.
                searchRows[pos][1] = 0;
                pos--; // Drop down a row to search the next branch.

            } else {
                // Add the new word to squareWords and move on to the next row.
                squareWords[squarePos] = match.getWord();
                searchRows[pos][1] = (match.getDictPos() + 1);
                pos++;
                if (pos == searchRows.length) { // A complete solution has been found.
                    //Make a solution object and add it to the solutions list.
                    //Solution object needs an array with no empty spaces
                    String[] solWords = new String[len];
                    System.arraycopy(squareWords, 0, solWords, 0, len);
                    Solution sol = new Solution(solWords, scorer);
                    ws.addSolution(sol);
                    //Drop back down to keep searching
                    pos--;
                }
            }
        }
        // Report completion for progress bar update.
        ws.searchThreadComplete();
    }
}
