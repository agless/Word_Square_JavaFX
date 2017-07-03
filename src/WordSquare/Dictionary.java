package WordSquare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andyg on 5/29/2017.
 */
class Dictionary {
    //An object to hold and access the wordBank.

    //Class variables
    private static final String WORDBANK = "resources/wordBank.csv";
    private static ArrayList<String> wb2 = new ArrayList<>();
    private static ArrayList<String> wb3 = new ArrayList<>();
    private static ArrayList<String> wb4 = new ArrayList<>();
    private static ArrayList<String> wb5 = new ArrayList<>();
    private static ArrayList<String> wb6 = new ArrayList<>();
    private Pattern searchPattern;
    private int dictionaryPos;

    public Dictionary() {
        //Load wordBank into memory, but only once
        if (wb2.size() <= 0) {
            try {
                InputStreamReader ir = new InputStreamReader(getClass().getResourceAsStream(WORDBANK));
                BufferedReader reader = new BufferedReader(ir);
                String workingLine;
                Boolean kill = false;
                while (!kill) {
                    workingLine = reader.readLine();
                    if (workingLine == null) {
                        kill = true;
                    } else {
                        workingLine = workingLine.trim();
                        int len = workingLine.length();
                        switch (len) {
                            case 2: wb2.add(workingLine); break;
                            case 3: wb3.add(workingLine); break;
                            case 4: wb4.add(workingLine); break;
                            case 5: wb5.add(workingLine); break;
                            case 6: wb6.add(workingLine); break;
                            default: break;
                        }
                    }
                }

                //Randomize word banks for smoother progress bar movement
                ArrayList[] wbRef = {wb2, wb3, wb4, wb5, wb6};
                for (int i = 0; i < 5; i++) {
                    ArrayList<String> wb = wbRef[i];
                    long seed = System.nanoTime();
                    Collections.shuffle(wb, new Random(seed));

                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    //Build a regular expression for searchPattern.
    public void buildSearchPattern(String[] squareWords, int pos, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String word = squareWords[i];
            if (word == null) {
                sb.append("[a-z]");
            } else {
                String add = Character.toString(word.charAt(pos));
                sb.append(add);
            }
        }
        String searchString = sb.toString();
        searchPattern = searchPattern.compile(searchString);
    }

    //spot check a user-input word to see if it fits a partial word square
    public boolean wordFits(String[] squareWords, String word, int pos) {
        //double check correct length all around
        int len = word.length();
        for (int i = 0; i < squareWords.length; i++ ) {
            String checkWord = squareWords[i];
            if (checkWord != null) {
                if (checkWord.length() != len) {
                    return false;
                }
            }
        }

        buildSearchPattern(squareWords, pos, len);
        Matcher m = searchPattern.matcher(word);
        return m.matches();
    }

    //Find the next word in the wordBank that matches the current searchPattern.
    public String getNextMatch(int pos, int newLen) {
        ArrayList<String> wordBank;
        dictionaryPos = pos;
        int len = newLen;
        switch (len) {
            case 2: wordBank = wb2; break;
            case 3: wordBank = wb3; break;
            case 4: wordBank = wb4; break;
            case 5: wordBank = wb5; break;
            case 6: wordBank = wb6; break;
            default: System.out.println("Dictioanry:  Invalid word length."); return null;
        }
        //Find the next match and return it.
        //buildSearchPattern() must be run first!!
        boolean matchFound = false;
        while (matchFound == false) {  //change to while (true)??

            if (dictionaryPos >= wordBank.size()) {
                String testWord = null;
                return testWord;
            }

            String testWord = wordBank.get(dictionaryPos);
            Matcher m = searchPattern.matcher(testWord);

            if (m.matches()) {
                return testWord;
            } else {
                dictionaryPos++;
            }
        }
        return null;
    }

    //Report the wordBank position for the last match
    public int getDictionaryPos() {
        return dictionaryPos;
    }  //maybe this should be changed so that find next match returns a position and this one returns the corresponding word?

    public int getDictionaryLength(int len) {
        ArrayList<String> wordBank = new ArrayList<>();
        switch (len) {
            case 2: wordBank = wb2; break;
            case 3: wordBank = wb3; break;
            case 4: wordBank = wb4; break;
            case 5: wordBank = wb5; break;
            case 6: wordBank = wb6; break;
            default: return 0;
        }
        return wordBank.size();
    }
}