package WordSquare;

/**
 * Created by andyg on 7/9/2017.
 */
public class Match {
    private String word;
    private int dictPos;

    public Match(String word, int dictPos) {
        this.word = word;
        this.dictPos = dictPos;
    }

    public String getWord() {
        return word;
    }

    public int getDictPos() {
        return dictPos;
    }
}
