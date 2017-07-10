package WordSquare;

import java.io.BufferedReader;
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

    public Dictionary() {
        //Load wordBank into memory, but only if it's not already loaded
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
    public synchronized Pattern buildSearchPattern(String[] squareWords, int pos, int len) {
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
        Pattern searchPattern = null;
        searchPattern = searchPattern.compile(searchString);
        return searchPattern;
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

        Pattern searchPattern = buildSearchPattern(squareWords, pos, len);
        Matcher m = searchPattern.matcher(word);
        return m.matches();
    }

    //Find the next word in the wordBank that matches the current searchPattern.
    public synchronized Match getNextMatch(Pattern searchPattern, int pos, int len) {
        ArrayList<String> wordBank;
        int dictionaryPos = pos;
        switch (len) {
            case 2: wordBank = wb2; break;
            case 3: wordBank = wb3; break;
            case 4: wordBank = wb4; break;
            case 5: wordBank = wb5; break;
            case 6: wordBank = wb6; break;
            default: System.out.println("Dictioanry:  Invalid word length."); return null;
        }
        //Find the next match and return it.
        while (true) {  //change to while (true)??

            if (dictionaryPos >= wordBank.size()) {
                return null;
            }

            String testWord = wordBank.get(dictionaryPos);
            Matcher m = searchPattern.matcher(testWord);

            if (m.matches()) {
                Match match = new Match(testWord, dictionaryPos);
                return match;
            } else {
                dictionaryPos++;
            }
        }
    }

    public int getDictionaryLength(int len) {
        ArrayList<String> wordBank;
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
