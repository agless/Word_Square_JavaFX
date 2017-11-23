package WordSquare;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code WordSquare} is a class to coordinate building word squares.
 * {@code WordSquare} includes methods to accept and hold user-input words
 * to make a partial word square and includes methods to search and complete
 * word squares with word bank words.  {@code WordSquare} provides
 * multi-threaded searching for faster solution generation. {@code WordSquare}
 * also maintains a list to hold each {@code Solution} that may be found by a
 * {@code SearchThread}.  The {@code Solution} solution list is available
 * while a search is in progress.  {@code WordSquare} also provides a method
 * to get its search progress to facilitate the use of a progress bar to
 * track the progress of longer searches.
 */
public class WordSquare {
    /*------------------------------------------------
    *
    * Member variables
    *
    * ------------------------------------------------*/

    // An object to find matching words for a particular word square position.
    // private DictionaryBrute dict = new DictionaryBrute();
    private DictionaryTernary dict = new DictionaryTernary();
    private Score score = new Score();

    // The list of found solutions.
    private ArrayList<Solution> solutionList = new ArrayList<Solution>();

    // A list of words making up a partial word square.
    private String[] squareWords = new String[6];

    // A data structure to act as a road map within a search thread.
    private int[][] searchRows;

    /* -----------------------------------------------
    *
    * Methods to initiate and manage search.
    *
    * ------------------------------------------------*/

    /**
     * Initiate the search process that will find all complete word square
     * solutions for the current user-created partial word square.
     */
    public void buildAllSolutions() {
        // Initialize variables.
        int len = 0;
        int squarePos = 0;
        String word = null;

        // Get word square size based on first fixed word.
        while (word == null) {
            word = squareWords[squarePos];
            if (word == null) {
                squarePos++;
                if (squarePos >= squareWords.length) {
                    return;
                }
            } else {
                len = word.length();
            }
        }

        // Check that all words in squareWords are same length;
        // Tally up the number of blank words to fill.
        int searchWordCount = 0;
        for (int i = 0; i < len; i++) {
            word = squareWords[i];
            if (word != null) {
                if (word.length() != len) {
                    return;
                }
            } else {
                searchWordCount++;
            }
        }

        // Check to make sure there's at least one empty space to search.
        if (searchWordCount == 0) {
            return;
        }

        // Make searchRows:
        // [squareWords position][current wordBank position][difficulty]
        searchRows = new int[searchWordCount][3];
        int searchRowPos = 0;
        for (int i = 0; i < len; i++) {
            if (squareWords[i] == null) {
                searchRows[searchRowPos][0] = i;
                searchRowPos++;
            }
        }

        HashSet[] matches = new HashSet[searchRows.length];

        double start = System.currentTimeMillis();
        // Start the build
        build(0, len, matches);

        double elapsed = System.currentTimeMillis() - start;
        System.out.println("Completed in " + elapsed / 1000 + " seconds.");
    }

    private void build(int pos, int len, HashSet[] matches) {
        if (pos <= searchRows.length) {
            String pattern = getPattern(searchRows[pos][0], len);
            matches[pos] = dict.matchWild(pattern);
            Iterator it = matches[pos].iterator();
            while (it.hasNext()) {
                squareWords[searchRows[pos][0]] = it.next().toString();
                if (pos == searchRows.length - 1) {
                    Solution sol = new Solution(squareWords.clone(), score);
                    solutionList.add(sol);
                }
                else build(pos + 1, len, matches);
            }
            squareWords[searchRows[pos][0]] = null;
        }
    }

    public String getPattern(int pos, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String word = squareWords[i];
            if (word == null) sb.append('.');
            else sb.append(Character.toString(word.charAt(pos)));
        }
        return sb.toString();
    }

    /*-----------------------------------------------------------
    *
    * Methods for SearchThread to interact with this instance.
    *
    * -----------------------------------------------------------*/

    /**
     * Get the partial word square composed of user-input words.
     * @return Returns a six-position array of {@code String}s.  Some
     * positions will be {@code null}.
     */
    public String[] getSquareWords() {
            return squareWords.clone();
    }

    /*------------------------------------------------
    *
    * Additional methods to interact with member variables.
    *
    * ------------------------------------------------*/

    /**
     * Set a word in the current partial word square.
     * @param word The word to add.
     * @param pos The position to add the word.
     */
    public void setWord(String word, int pos) {
        if (wordFits(word, pos)) squareWords[pos] = word;
    }

    /**
     * Check whether a given word will fit a given position in the
     * current partial word square.
     * @param word The word to check.
     * @param pos The position to check.
     * @return Returns {@code true} if the word fits this position in word
     * square.  Otherwise, returns {@code false}.
     */
    public boolean wordFits(String word, int pos) {
        if (word == null) return true;
        // Check the test word against all squareWords for appropriate length / character
        int len = word.length();
        for (int i = 0; i < squareWords.length; i++ ) {
            String checkWord = squareWords[i];
            if (checkWord != null) {
                if ((checkWord.length() != len) ||
                        (checkWord.charAt(pos) != word.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Clear the current partial word square from memory.
     */
    public void clearSquareWords() {
        for (int i = 0; i < 6; i++) {
            squareWords[i] = null;
        }
    }

    /**
     * Clear the current list of solutions from memory.
     */
    public void clearSolutions() {
        solutionList.clear();
    }

    /**
     * Get a particular {@code Solution} from the list.
     * @param pos The position of the desired {@code Solution}.
     * @return Returns the {@code Solution} at the given position, if any.
     * Otherwise, returns {@code null}.
     */
    public Solution getSolution(int pos) {
        if (solutionList.size() > pos) return solutionList.get(pos);
        else return null;
    }

    /**
     * Get the list of found solutions.
     * @return Returns a new {@code ArrayList} of all solutions found.
     */
    public ArrayList<Solution> getSolutionList() {
        return new ArrayList<Solution>(solutionList);
    }
}
