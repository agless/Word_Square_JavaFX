package WordSquare;

/**
 * {@code Match} is a class to cleanly pass matching words and their
 * word bank position from a {@code DictionaryBrute} object to a search thread.
 */
public class Match {
    private String word;
    private int dictPos;

    /**
     * A {@code Match} object requires a matching word and its word bank
     * position for construction.
     * @param word The matching word.
     * @param dictPos The word bank position for the matching word.
     */
    public Match(String word, int dictPos) {
        this.word = word;
        this.dictPos = dictPos;
    }

    /**
     * Check the matching word.
     * @return Returns the matching word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Check the word bank position of the matching word.
     * @return Returns the word bank position of the matching word.
     */
    public int getDictPos() {
        return dictPos;
    }
}
