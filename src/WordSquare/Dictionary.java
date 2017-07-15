package WordSquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Dictionary is a an object that loads, holds, and searches a word bank.
 * The word bank is sorted by word length for faster searching, since any given
 * search for square words will only yield words of a particular length.
 * Dictionary returns matches in the form of Match objects, which hold a
 * word and dictionary position.*/
class Dictionary {

    //Room for improvement:  Allow user to load an alternate word bank.
    private static final String WORDBANK = "resources/wordBank.csv";

    //Wordbank is stored in like-length lists for faster searching.
    private static ArrayList<String> wb2 = new ArrayList<>();
    private static ArrayList<String> wb3 = new ArrayList<>();
    private static ArrayList<String> wb4 = new ArrayList<>();
    private static ArrayList<String> wb5 = new ArrayList<>();
    private static ArrayList<String> wb6 = new ArrayList<>();


    /*
    * Constructor loads the static word banks into memory if they have not
    * already been loaded by another Dictionary object.  */
    public Dictionary() {

        //Load wordBank into memory, but only if it's not already loaded.
        if (wb2.size() <= 0) {
            try {

                //Open word bank file as a stream.
                InputStreamReader ir = new InputStreamReader(
                        getClass().getResourceAsStream(WORDBANK));
                BufferedReader reader = new BufferedReader(ir);

                //Grab the first line to check
                String workingLine = reader.readLine();

                //Iterate through every line in the stream.
                //Room for improvement:
                //Ensure that all words are lower case and stripped of whitespace.
                do {
                    //Sort by length.
                    int len = workingLine.length();
                    switch (len) {
                        case 2: wb2.add(workingLine); break;
                        case 3: wb3.add(workingLine); break;
                        case 4: wb4.add(workingLine); break;
                        case 5: wb5.add(workingLine); break;
                        case 6: wb6.add(workingLine); break;
                        default: break;  //Disregard the rest.
                    }
                    //Grab the next line
                    workingLine = reader.readLine();

                    //Stop at end of stream.
                    //Though it's the first blank line, really.  (Room for improvement.)
                } while (workingLine != null);

                //Randomize word banks for smoother progress bar movement
                ArrayList[] wbRef = {wb2, wb3, wb4, wb5, wb6};
                for (int i = 0; i < 5; i++) {
                    ArrayList wb = wbRef[i];
                    long seed = System.nanoTime();
                    Collections.shuffle(wb, new Random(seed));

                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    /*
    * Build a regular expression of the appropriate length ('len') for
    * searchPattern by looping through squareWords and recording the
    * character in the relevant column ('pos').  If an empty spot is found,
    * enter a wildcard for any lower case letter. */
    public Pattern buildSearchPattern(String[] squareWords, int pos, int len) {

        //String builder is quick and has lower overhead than concatenation.
        StringBuilder sb = new StringBuilder();

        //Build a string, spot-by-spot.
        for (int i = 0; i < len; i++) {

            //Load the fixed word in this row, if any.
            String word = squareWords[i];

            if (word == null) {
                //If this row is blank, add a wildcard.
                sb.append("[a-z]");

            } else {
                //Otherwise, add the character from the relevant column.
                sb.append(Character.toString(word.charAt(pos)));
            }
        }
        //Assemble the string.
        String searchString = sb.toString();

        //Make the searchPattern.
        Pattern searchPattern = null;
        searchPattern = searchPattern.compile(searchString);

        return searchPattern;
    }

    /*
    * Spot check a user-input word ('word') to see if it fits at the given
    * position ('pos') in a partial word square ('squareWords').*/
    public boolean wordFits(String[] squareWords, String word, int pos) {

        //Check the test word against all squareWords for length.
        int len = word.length();
        for (int i = 0; i < squareWords.length; i++ ) {

            //Load the squareWord for this row, if any.
            String checkWord = squareWords[i];
            if (checkWord != null) {
                if (checkWord.length() != len) {
                    //Return false if any words are the wrong length.
                    return false;
                }
            }
        }

        //Create a regular expression to check against.
        Pattern searchPattern = buildSearchPattern(squareWords, pos, len);

        //Create a Matcher to perform the check.
        Matcher m = searchPattern.matcher(word);

        //Report whether the test word fits the pattern for this row.
        return m.matches();
    }

    /*
    * Find and return the next Match from the word bank of the given
    * length ('len'), starting from the given position ('pos'), and matching
    * against the given regular expression ('searchPattern').  */
    public Match getNextMatch(Pattern searchPattern, int pos, int len) {

        //Make a variable to reference the correct length word bank.
        ArrayList<String> wordBank;
        switch (len) {
            case 2: wordBank = wb2; break;
            case 3: wordBank = wb3; break;
            case 4: wordBank = wb4; break;
            case 5: wordBank = wb5; break;
            case 6: wordBank = wb6; break;
            default: return null;
        }

        //Find the next match and return it.
        //If no match can be found, return null.
        while (true) {

            //If the end of the word bank is reached without a match...
            if (pos >= wordBank.size()) {
                return null;
            }

            //Load a word to test against.
            String testWord = wordBank.get(pos);

            //Create an instance of Matcher to perform the check.
            Matcher m = searchPattern.matcher(testWord);

            //Check if a match has been found.
            if (m.matches()) {

                //If so, create a Match and return it.
                Match match = new Match(testWord, pos);
                return match;

            } else {
                //If not, increment position to check the next word.
                pos++;
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
