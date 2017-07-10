package WordSquare;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class WordSquare {
    private Dictionary dict = new Dictionary();
    private ArrayList<Solution> solutionList = new ArrayList<Solution>();
    private String[] squareWords = new String[6];
    private int[][] searchRows;
    private ArrayList<String> branchList = new ArrayList<>();
    private int[] searchProgress = {0,0};
    private ExecutorService searchPool = Executors.newFixedThreadPool(4);


    public void killSearch() {
        searchPool.shutdownNow();
        searchProgress[0] = 1;
        searchProgress[1] = 1;
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
        return solutionList.get(pos);
    }

    public synchronized String[] getSquareWords() {
        return squareWords.clone();
    }

    public synchronized int[][] getSearchRows() {
        return searchRows.clone();
    }

    public synchronized String getBranchWord() {
        String branchWord = null;
        if (branchList.size() > 0) {
            branchWord = branchList.get(0);
            branchList.remove(0);
        }
        return branchWord;
    }

    public ArrayList<Solution> getSolutionList() {
        return new ArrayList<Solution>(solutionList);
    }

    public void clearSolutions() {
        solutionList.clear();
    }

    public int[] getSearchProgress() {
        return searchProgress.clone();
    }

    public synchronized void addSolution(Solution sol) {
        solutionList.add(sol);
    }

    public synchronized void searchThreadComplete() {
        searchProgress[0]++;
        System.out.println(searchProgress[0] + " " + searchProgress[1]);
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

        //Sort searchRows by difficulty for faster searches (cut branches, not leaves)
        //(lower score is more difficult)
        Arrays.sort(searchRows, (a,b) -> Integer.compare(a[2], b[2]));

        //get all searchRows[0][] words and add them to branchList
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

        int threadCount = branchList.size();

        //Set initial search progress (0 / branchList.size())
        searchProgress[0] = 0;
        searchProgress[1] = threadCount;

        //start the search threads
        for (int i = 0; i < threadCount; i++) {
            SearchThread newBranch = new SearchThread(this);
            searchPool.submit(newBranch);
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