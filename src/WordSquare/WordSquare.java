package WordSquare;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private Dictionary dict = new Dictionary();

    // The list of found solutions.
    private ArrayList<Solution> solutionList = new ArrayList<Solution>();

    // A list of words making up a partial word square.
    private String[] squareWords = new String[6];

    // A data structure to act as a road map within a search thread.
    private int[][] searchRows;

    // A list of words matching the first searchRows position.
    // Each of these will be the seed for a SearchThread.
    private ArrayList<String> branchList = new ArrayList<>();

    // [0] == the number of completed SearchThread.
    // [1] == the total number of SearchThread created for this search.
    private int[] searchProgress = {0, 0};

    // A thread pool for multi-threaded searching.
    private ExecutorService searchPool = Executors.newFixedThreadPool(6);

    // Member synchronization locks for thread safety.
    private final Object squareWordsLock = new Object();
    private final Object searchRowsLock = new Object();
    private final Object branchListLock = new Object();
    private final Object addSolutionLock = new Object();
    private final Object searchThreadCompleteLock = new Object();

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
                searchRows[searchRowPos][1] = 0;
                searchRows[searchRowPos][2] = scoreSearchRow(i, len);
                searchRowPos++;
            }
        }

        // Sort searchRows by difficulty for faster searches (prune branches).
        // (Lower score means fewer potential matches, so search here first.)
        Arrays.sort(searchRows, (a, b) -> Integer.compare(a[2], b[2]));

        // Get all searchRows[0][] words and add them to branchList.
        Pattern searchPattern = dict.buildSearchPattern(squareWords, searchRows[0][0], len);
        boolean matchFound = true;
        while (matchFound) {
            Match match = dict.getNextMatch(searchPattern, searchRows[0][1], len);
            if (match == null) {
                matchFound = false;
            } else {
                branchList.add(match.getWord());
                searchRows[0][1] = (match.getDictPos() + 1);
            }
        }

        // Store the total number of branches to create.
        int threadCount = branchList.size();

        // Set initial search progress (0 / threadCount).
        searchProgress[0] = 0;
        searchProgress[1] = threadCount;

        // Start the search threads.
        for (int i = 0; i < threadCount; i++) {
            SearchThread newBranch = new SearchThread(this);
            searchPool.submit(newBranch);
        }
    }

    /**
     * A method to score search positions for partial word squares so that
     * the number of search branches may be pruned.  Search positions that
     * have uncommon fixed letters (and therefore are likely to return fewer
     * matches) are moved closer to the root search node.
     * @param pos The word square position that this search row represents.
     * @param len The length of this word square.
     * @return Returns an {@code int} representing the difficulty of finding
     * valid matches for this search row.  A lower number means fewer
     * potential matches are likely.
     */
    private int scoreSearchRow(int pos, int len) {
        int difficulty = 0;
        // This can be a for each loop.
        for (int i = 0; i < len; i++) {
            String word = squareWords[i];
            if (word != null) {
                char test = word.charAt(pos);
                switch (test) {
                    case 'a': difficulty += 24; break;
                    case 'b': difficulty += 7; break;
                    case 'c': difficulty += 15; break;
                    case 'd': difficulty += 17; break;
                    case 'e': difficulty += 26; break;
                    case 'f': difficulty += 11; break;
                    case 'g': difficulty += 10; break;
                    case 'h': difficulty += 19; break;
                    case 'i': difficulty += 22; break;
                    case 'j': difficulty += 4; break;
                    case 'k': difficulty += 5; break;
                    case 'l': difficulty += 16; break;
                    case 'm': difficulty += 13; break;
                    case 'n': difficulty += 21; break;
                    case 'o': difficulty += 23; break;
                    case 'p': difficulty += 8; break;
                    case 'q': difficulty += 2; break;
                    case 'r': difficulty += 18; break;
                    case 's': difficulty += 20; break;
                    case 't': difficulty += 25; break;
                    case 'u': difficulty += 14; break;
                    case 'v': difficulty += 6; break;
                    case 'w': difficulty += 12; break;
                    case 'x': difficulty += 3; break;
                    case 'y': difficulty += 9; break;
                    case 'z': difficulty += 1; break;
                    default: break;
                }
            }
        }
        return difficulty;
    }

    /**
     * Stop a search in progress by shutting down the search pool.
     */
    public void killSearch() {
        searchPool.shutdownNow();
        searchProgress[0] = 1;
        searchProgress[1] = 1;
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
        synchronized (squareWordsLock) {
            return squareWords.clone();
        }
    }

    /**
     * Get a table containing information about the blank word square rows
     * to be searched.
     * @return Returns a 2D {@code int} array of variable length and
     * width of 3.  Each {@code searchRows} item represents a {@code null}
     * item in {@code squareWords} to be filled by search.  For each item, the
     * following values are tracked at the relative index:
     * [squareWords position][current Dictionary.wordBank search position][difficulty]
     */
    public int[][] getSearchRows() {
        synchronized (searchRowsLock) {
            int[][] copy = new int[searchRows.length][];
            for (int i = 0; i < searchRows.length; i++) {
                copy[i] = Arrays.copyOf(searchRows[i], searchRows[i].length);
            }
            return copy;
        }
    }

    /**
     * Get a unique branch word to search.
     * @return Returns a {@code String} unique branch word to ensure that each
     * {@code SearchThread} does unique work when searching for valid
     * word squares.
     */
    public String getBranchWord() {
        synchronized (branchListLock) {
            String branchWord = null;
            if (branchList.size() > 0) {
                branchWord = branchList.get(0);
                branchList.remove(0);
            }
            return branchWord;
        }
    }

    /**
     * Add a found solution to the list.
     * @param sol The solution to add.
     */
    public void addSolution(Solution sol) {
        synchronized (addSolutionLock) {
            solutionList.add(sol);
        }
    }

    /**
     * Increment search progress.
     * To be called by a {@code SearchThread} when its work is complete.
     */
    public void searchThreadComplete() {
        synchronized (searchThreadCompleteLock) {
            searchProgress[0]++;
        }
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
        String[] testWords = squareWords.clone();
        testWords[pos] = null;
        return dict.wordFits(testWords, word, pos);
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
     * Get the current search progress.
     * @return Returns an {@code int} array containing the number of completed
     * {@code SearchThread}s at index 0 and the total number of
     * {@code SearchThread}s at index 1.
     */
    public int[] getSearchProgress() {
        return searchProgress.clone();
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
