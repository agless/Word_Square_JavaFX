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
 * {@code Dictionary} is a class to load, hold, and search a word bank for
 * words that fit a particular word square.  The word bank is segregated
 * by word length for faster searching, since only words of identical length
 * can be used to construct valid word squares.  {@code Dictionary} returns matches
 * in the form of {@code Match} objects, which hold a word and its word bank
 * position.
 */
class Dictionary {

    // Room for improvement:  Allow user to load an alternate word bank.
    private static final String WORDBANK = "resources/wordBank.csv";

    // Word bank is stored in like-length lists for faster searching.
    private static ArrayList<String> wb2 = new ArrayList<>();
    private static ArrayList<String> wb3 = new ArrayList<>();
    private static ArrayList<String> wb4 = new ArrayList<>();
    private static ArrayList<String> wb5 = new ArrayList<>();
    private static ArrayList<String> wb6 = new ArrayList<>();

    /**
     * The constructor loads the static word banks into memory if they
     * have not already been loaded by another {@code Dictionary} object.
     */
    public Dictionary() {

        // Load wordBank into memory, but only if it's not already loaded.
        if (wb2.size() <= 0) {
            try {

                // Open word bank file as a stream.
                InputStreamReader ir = new InputStreamReader(
                        getClass().getResourceAsStream(WORDBANK));
                BufferedReader reader = new BufferedReader(ir);

                // Initialize local variable with first line from the stream.
                String workingLine = reader.readLine();

                // Iterate through every line in the stream.
                // Room for improvement:  Ensure that all words
                // are lower case and stripped of whitespace.
                do {
                    // Separate word banks by length.
                    int len = workingLine.length();
                    switch (len) {
                        case 2: wb2.add(workingLine); break;
                        case 3: wb3.add(workingLine); break;
                        case 4: wb4.add(workingLine); break;
                        case 5: wb5.add(workingLine); break;
                        case 6: wb6.add(workingLine); break;
                        default: break;  //Disregard the rest.
                    }
                    // Grab the next line
                    workingLine = reader.readLine();

                    // Stop at end of stream.
                    // Though it's the first blank line, really.
                    // (Room for improvement.)
                } while (workingLine != null);

                // Randomize word banks for smoother progress bar movement
                ArrayList[] wbRef = {wb2, wb3, wb4, wb5, wb6};
                for (int i = 0; i < 5; i++) {
                    ArrayList wb = wbRef[i];
                    long seed = System.nanoTime();
                    Collections.shuffle(wb, new Random(seed));

                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    /**
     * Builds a regular expression for a particular word square position
     * in order to match word bank words that fit the pattern.
     * @param squareWords The incomplete word square.
     * @param pos The word square position to base the search pattern on.
     * @param len The length of words that will fit this square.
     * @return Returns a regex Pattern for the position to be filled.
     */
    public Pattern buildSearchPattern(String[] squareWords, int pos, int len) {

        // String builder is quick and has lower overhead than concatenation.
        StringBuilder sb = new StringBuilder();

        // Build a string, character-by-character.
        for (int i = 0; i < len; i++) {

            // Load the fixed word that intersects with this character
            // position, if any.
            String word = squareWords[i];

            if (word == null) {
                // If this row is blank, add a wildcard.
                sb.append("[a-z]");

            } else {
                // Otherwise, add the character from the relevant position.
                sb.append(Character.toString(word.charAt(pos)));
            }
        }
        // Assemble the string.
        String searchString = sb.toString();

        // Make the searchPattern and return it.
        Pattern searchPattern = null;
        searchPattern = searchPattern.compile(searchString);

        return searchPattern;
    }

    /**
     * Spot check a user-input word to see if it fits the word square.
     * @param squareWords The incomplete word square.
     * @param word The user-input word to check.
     * @param pos The word square position to check for fit.
     * @return Returns {@code true} if the word fits.  Otherwise
     * returns {@code false}.
     */
    public boolean wordFits(String[] squareWords, String word, int pos) {
        // Check for null test.
        if (word == null) return true;


        // Check the test word against all squareWords for appropriate length.
        int len = word.length();
        for (int i = 0; i < squareWords.length; i++ ) {

            // Load the squareWord for this row, if any.
            String checkWord = squareWords[i];
            if (checkWord != null) {
                if (checkWord.length() != len) {
                    // Return false if any words are the wrong length.
                    return false;
                }
            }
        }

        // Create a regular expression to check against.
        Pattern searchPattern = buildSearchPattern(squareWords, pos, len);

        // Create a Matcher to perform the check.
        Matcher m = searchPattern.matcher(word);

        // Report whether the test word fits the pattern for this row.
        return m.matches();
    }

    /**
     * Find and return the next match from the word bank.
     * @param searchPattern The regex pattern that matches must match.
     * @param bankPos The starting word bank position for this search.
     * @param len The length for valid match words.
     * @return Returns a {@code Match} object that holds a valid word
     * for this position and the word bank index for that word.  Returns
     * {@code null} if no match is found.
     */
    public Match getNextMatch(Pattern searchPattern, int bankPos, int len) {

        // Make a temporary variable to reference the correct length word bank.
        ArrayList<String> wordBank;
        switch (len) {
            case 2: wordBank = wb2; break;
            case 3: wordBank = wb3; break;
            case 4: wordBank = wb4; break;
            case 5: wordBank = wb5; break;
            case 6: wordBank = wb6; break;
            default: return null;
        }

        // Find the next match and return it.
        // If no match can be found, return null.
        while (true) {

            // If the end of the word bank is reached without a match...
            if (bankPos >= wordBank.size()) return null;

            // Load a word to test against.
            String testWord = wordBank.get(bankPos);

            // Create an instance of Matcher to perform the check.
            Matcher m = searchPattern.matcher(testWord);

            // Check if a match has been found.
            if (m.matches()) {

                // If so, create a Match and return it.
                Match match = new Match(testWord, bankPos);
                return match;

            } else {
                // If not, increment position to check the next word.
                bankPos++;
            }
        }
    }

    /**
     * Checks the number of word bank words of a given length.
     * @param len The length of relevant word bank words.
     * @return Returns the number of word bank words of this length.
     */
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
