package WordSquare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by andyg on 5/29/2017.
 */
public class Score {
    //A class to hold the word/score data structure and look up word scores.
    private String word;
    private long wordScore;
    private HashMap<String, Long> wordScores = new HashMap<>();
    private static final String SCORES = "resources/scores.txt";

    public Score() {
        //Get the word/score pairs into a data structure.
        try {
            //Initialize variables.
            InputStreamReader ir = new InputStreamReader(getClass().getResourceAsStream(SCORES));
            BufferedReader reader = new BufferedReader(ir);
            String workingLine;
            String[] lineArray;

            //Add each word/score pair line by line
            while (true) {
                workingLine = reader.readLine();
                if (workingLine == null) {
                    break;
                } else {
                    lineArray = workingLine.split("\t");
                    word = lineArray[0];
                    wordScore = Long.parseLong(lineArray[1]);
                    wordScores.put(word, wordScore);
                }
            }

            //Close the file stream.
            reader.close();

        } catch(IOException ex) {
            //Try again??? We have to have a score table.
            //Make a popup?  With a button to try to load the score table again?
        }
    }

    public long getWordScore(String lookup) {

        word = lookup;
        if (wordScores.containsKey(word)) {
            wordScore = wordScores.get(word);
            return wordScore;
        } else {
            return 1;
        }
    }
}
