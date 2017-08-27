package WordSquare;

/**
 * {@code Solution} is a class to hold a valid word square and three score
 * metrics for ranking a set of solutions.
 */
public class Solution {
    private String[] solutionWords; // A valid word square.
    private long totalScore = 0; // All word scores combined.
    private long averageScore = 0; // The average of all word scores.
    private long lowScore = 0; // The lowest individual word score.

    /**
     * Each {@code Solution} calculates its own scores on construction.
     * @param squareWords A valid word square.
     * @param scorer A {@code Score} instance for this {@code Solution}
     * instance to query.
     */
    public Solution(String[] squareWords, Score scorer) {
        // Get wordScores
        solutionWords = squareWords;
        int len = solutionWords.length;
        long[] wordScores = new long[len];
        for(int i = 0; i < len; i++) {
            String word = solutionWords[i];
            long points = scorer.getWordScore(word);
            wordScores[i] = points;
        }

        // Calculate totalScore; Find lowScore
        for (int i = 0; i < solutionWords.length; i++) {
            long thisScore = wordScores[i];
            totalScore += thisScore;
            if (i == 0) {
                lowScore = thisScore;
            } else if (thisScore < lowScore) {
                lowScore = thisScore;
            }
        }
        // Calculate averageScore
        averageScore = (totalScore / solutionWords.length);
    }

    /**
     * Get a copy of the square words for this {@code Solution}.
     * @return Returns an array of Strings, of length between 2 and 6.
     */
    public String[] getSolutionWords() {
        return solutionWords.clone();
    }

    /**
     * Get the total score for all square words.
     * @return Returns the long total word score.
     */
    public long getTotalScore() {
        return totalScore;
    }

    /**
     * Get the average score for all square words.
     * @return Returns the long average word score.
     */
    public long getAverageScore() {
        return averageScore;
    }

    /**
     * Get the lowest individual word score.
     * @return Returns the lowest individual word score.
     */
    public long getLowScore() {
        return lowScore;
    }
}
