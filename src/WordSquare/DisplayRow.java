package WordSquare;

/**
 * Created by andyg on 6/4/2017.
 */
public class DisplayRow {
    private int displayPos;
    private char c1;
    private char c2;
    private char c3;
    private char c4;
    private char c5;
    private char c6;

    public void setChars (String[] squareWords, int pos) {
        displayPos = (pos + 1);
        String word = null;
        if (pos < squareWords.length) {
            word = squareWords[pos];
        }
        char[] colRef = new char[6];
        int colPos;
        if (word != null) {
            //put each word letter in a column
            int len = word.length();
            for (colPos = 0; colPos < len; colPos++) {
                char c = word.charAt(colPos);
                colRef[colPos] = c;
                if (colPos == 5) { colPos = 6; }
            }
            while (colPos < 6) {
                //put a "." in extra columns
                char c = '.';
                colRef[colPos] = c;
                colPos++;
            }
        } else {
            //iterate through squareWords grabbing the letter at pos for each column
            colPos = 0;
            while (colPos < squareWords.length) {
                word = squareWords[colPos];
                if (word == null) {
                    char c = '.';
                    colRef[colPos] = c;
                } else {
                    if (pos >= word.length()) {
                        char c = '.';
                        colRef[colPos] = c;
                    } else {
                        char c = word.charAt(pos);
                        colRef[colPos] = c;
                    }
                }
                colPos++;
            }
        }

        //Make all characters upper case
        for (int i = 0; i < colRef.length; i++) {
            char c = colRef[i];
            String s = Character.toString(c);
            s = s.toUpperCase();
            c = s.charAt(0);
            colRef[i] = c;
        }

        //Set watched variables for display
        c1 = colRef[0];
        c2 = colRef[1];
        c3 = colRef[2];
        c4 = colRef[3];
        c5 = colRef[4];
        c6 = colRef[5];

        //add a line to make each char uppercase and bold (if possible?)
    }

    public int getDisplayPos() {
        return displayPos;
    }

    public char getC1() {
        return c1;
    }

    public char getC2() {
        return c2;
    }

    public char getC3() {
        return c3;
    }

    public char getC4() {
        return c4;
    }

    public char getC5() {
        return c5;
    }

    public char getC6() {
        return c6;
    }
}
