package WordSquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * {@code Score} is a class to load, hold and retrieve word/score data.
 * Word scoring is used to rank completed word squares for quality.
 */
public class Score {
    private static HashMap<String, Long> wordScores = new HashMap<>();
    private static final String SCORES = "resources/scores.txt";

    /**
     * The constructor loads the static word/score data structure into memory
     * only if it has not already been loaded by another {@code Score} object.
     */
    public Score() {
        if (wordScores.size() <= 0) {
            try {
                InputStreamReader ir = new InputStreamReader(getClass().getResourceAsStream(SCORES));
                BufferedReader reader = new BufferedReader(ir);
                String workingLine;
                String[] lineArray;

                // Add each word/score pair line by line
                while (true) {
                    workingLine = reader.readLine();
                    if (workingLine == null) {
                        break;
                    } else {
                        lineArray = workingLine.split("\t");
                        String word = lineArray[0];
                        Long wordScore = Long.parseLong(lineArray[1]);
                        wordScores.put(word, wordScore);
                    }
                }

                // Close the file stream.
                reader.close();

            } catch (IOException ex) {
                // Try again??? We have to have a score table.
                // Make a popup?  With a button to try to load the score table again?
                ex.printStackTrace();
            }
        }
    }

    /**
     * Finds the score for a given word.
     * @param word The word to score.
     * @return Returns the n-gram score of the given word, if any. Otherwise,
     * returns a score of 1.
     */
    public long getWordScore(String word) {

        if (wordScores.containsKey(word)) {
            Long wordScore = wordScores.get(word);
            return wordScore;
        } else {
            return 1;
        }
    }
}
