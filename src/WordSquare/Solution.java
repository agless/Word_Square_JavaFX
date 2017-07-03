package WordSquare;

/**
 * Created by andyg on 5/29/2017.
 */
public class Solution {
    private String[] solutionWords;
    private long totalScore = 0;
    private long averageScore = 0;
    private long lowScore = 0;

    public Solution(String[] squareWords, Score scorer) {
        //Get wordScores
        solutionWords = squareWords;
        int len = solutionWords.length;
        long[] wordScores = new long[len];
        for(int i = 0; i < len; i++) {
            String word = solutionWords[i];
            long points = scorer.getWordScore(word);
            wordScores[i] = points;
        }

        //Calculate totalScore; Find lowScore
        for (int i = 0; i < solutionWords.length; i++) {
            long thisScore = wordScores[i];
            totalScore += thisScore;
            if (i == 0) {
                lowScore = thisScore;
            } else if (thisScore < lowScore) {
                lowScore = thisScore;
            }
        }
        //Calculate averageScore
        averageScore = (totalScore / solutionWords.length);
    }

    public String[] getSolutionWords() {
        return solutionWords.clone();
    }

    public long getTotalScore() {
        return totalScore;
    }

    public long getAverageScore() {
        return averageScore;
    }

    public long getLowScore() {
        return lowScore;
    }
}
