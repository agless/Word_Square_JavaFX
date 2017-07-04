package WordSquare;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by andyg on 5/29/2017.
 */
public class WordSquare {
    private Dictionary dict = new Dictionary();
    protected static Score scorer = new Score();
    protected static ArrayList<Solution> solutionList = new ArrayList<>();
    private String[] squareWords = new String[6];
    private boolean searchActive = false;
    private int[] searchProgress = new int[2];
    private Boolean kill = false;

    public void killSearch() {
        if (searchActive) {
            kill = true;
        }
    }

    public void clearSquareWords() {
        for (int i = 0; i < 6; i++) {
            squareWords[i] = null;
        }
    }

    public boolean wordFits (String word, int pos) {
        String [] testWords = squareWords.clone();
        testWords[pos] = null;
        return dict.wordFits(testWords, word, pos);
    }

    public void setWord(String word, int pos) {
        squareWords[pos] = word;
    }

    public Solution getSolution(int pos) {
        Solution solution = solutionList.get(pos);
        return solution;
    }

    public String[] getSquareWords() {
        return squareWords.clone();
    }

    public ArrayList<Solution> getSolutionList() {
        return new ArrayList<Solution>(solutionList);
    }

    public void clearSolutions() {
        solutionList.clear();
    }

    public int[] getSearchProgress() {
        if (searchActive == true) {
            return searchProgress.clone();
        } else {
            return null;
        }
    }

    public void buildAllSolutions() {
        //initialize variables
        int len = 0;
        int squarePos = 0;
        String word = null;

        //get word square size based on first fixed word
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

        //check that all words in squareWords are same length;
        //tally up the number of blank words to fill;
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

        //check to make sure there's at least one empty space to search
        if (searchWordCount == 0) { return; }

        //make searchRows[squareWords position][current wordBank position][difficulty]
        int[][] searchRows = new int[searchWordCount][3];
        int searchRowPos = 0;
        for (int i = 0; i < len; i++) {
            if (squareWords[i] == null) {
                searchRows[searchRowPos][0] = i;
                searchRows[searchRowPos][1] = 0;
                searchRows[searchRowPos][2] = scoreSearchRow(i, len);
                searchRowPos++;
            }
        }

        //Rank searchRows by difficulty for faster searches (cut branches, not leaves)
        //(lower score is more difficult)
        Arrays.sort(searchRows, (a,b) -> Integer.compare(a[2], b[2]));

        //Set initial search progress (0 / wordBank length)
        searchProgress[0] = 0;
        searchProgress[1] = dict.getDictionaryLength(len);

        int pos = 0;
        int dictPos;
        searchActive = true;

        while ((pos >= 0) && (!kill)) {
            //Recursively iterate through each row in searchRow
            squarePos = searchRows[pos][0];
            squareWords[squarePos] = null;
            dictPos = searchRows[pos][1];

            dict.buildSearchPattern(squareWords, squarePos, len);

            /*This should be changed so that dictionary returns the position, rather
            * than the word.  Idea is to make this threadsafe so that eventually
            * there will be multiple simultaneous search threads handed out based on
            * unique searchRows[0][] matches.
            * Once this is implementd, the check will be for if (newWord == -1) to show
            * that end of wordBank was reached without a match.
            * Dictionary will return position, then we will request the word at that
            * position to add to squareWords.
            *
            * ACTUALLY:
            * Dictionary is a bottleneck.  If we try to rely on a single,
            * shared dictionary object, multithreaded searches probably won't be any
            * faster than single-thread.  Maybe the better solution is to give each search thread its own
            * dictionary object to work with.  No changes would be necessary here or in
            * the dictionary class to pull this off.
            *
            * The question then becomes, will instantiating a fresh dictionary object for
            * each of the thousands of searchRows[0][] branches cause enough overhead to make
            * multithreaded searching not worthwhile?  Note that only the first instance
            * of Dictionary needs to read the word banks from disk.
            *
            * Seems like the answer is to create a fixed thread pool here.
            * Create a child of WordSquare that implements runnable and does the while loop
            * (except checks for pos '1' rather than '0').  A thread will be created for each searchRows[0][] match.
            * All of the threads will be submitted to the thread pool.  Will have to track progress (threads processed
            * / total initial threads).  */

            String newWord = dict.getNextMatch(dictPos, len);

            if (newWord == null) { //end of wordBank reached without a match
                //set dictPos back to zero for this failed row
                searchRows[pos][1] = 0;
                pos--; //drop down a row to search the next branch

            } else {
                //Add the newWord to squareWords
                squareWords[squarePos] = newWord;
                //Get and save the new dictioary position for this row
                dictPos = dict.getDictionaryPos();
                if (pos == 0) { searchProgress[0] = dictPos; }
                searchRows[pos][1] = (dictPos + 1);
                pos++;
                if (pos == searchRows.length) { //a complete solution has been found
                    //Make a solution object and add it to the solutions list.
                    //Solution object needs an array with no empty spaces
                    String[] solWords = new String[len];
                    for (int i = 0; i < len; i++) {
                        solWords[i] = squareWords[i];
                    }
                    Solution sol = new Solution(solWords, scorer);
                    solutionList.add(sol);
                    //Drop back down to keep searching
                    pos--;
                }
            }
        }
        kill = false;
        searchActive = false;
        for (int i = 0; i < 6; i++) {
            squareWords[i] = null;
        }
    }

    private int scoreSearchRow(int pos, int len) {
        int difficulty = 0;
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

}