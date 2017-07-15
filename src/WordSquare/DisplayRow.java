package WordSquare;

/*
* DisplayRow is a class to find and hold the characters that make up each
* individual square word for display in a grid.
* Word position and characters are stored in member variables to be watched by
* a PropertyValueFactory declared by WordSquareController.  */
public class DisplayRow {
    private int displayPos;
    private char c1;
    private char c2;
    private char c3;
    private char c4;
    private char c5;
    private char c6;

    /*Assign member variables to display the word at the given position ('pos')
    * from the given word square ('squareWords').  */
    public void setChars (String[] squareWords, int pos) {

        //Correct for counting from zero for human consumption.
        displayPos = (pos + 1);

        //Load the word for this row.
        String word = squareWords[pos];

        //Create an array to hold each column's characters during the loop.
        char[] colRef = new char[6];

        //Create a variable to keep track of where we are in the loop.
        int colPos;

        if (word != null) { //If my row has a word...

            //Loop through the word, putting each character in the array.
            int len = word.length();
            for (colPos = 0; colPos < len; colPos++) {

                //Store each character in the temporary array.
                char c = word.charAt(colPos);
                colRef[colPos] = c;

                //Avoid overwriting the last column in 6-letter words during
                //filler loop below.
                if (colPos == 5) colPos = 6;
            }

            //For words shorter than 6 letters long, fill remaining columns
            //with a placeholder for display.
            while (colPos < 6) {
                //put a "." in extra columns
                char c = '.';
                colRef[colPos] = c;
                colPos++;
            }
        } else {  //If my row doesn't have a word...

            //Iterate through squareWords grabbing the letter at pos for each column.
            colPos = 0;
            while (colPos < 6) {

                //Get the square word for this row, if any.
                word = squareWords[colPos];

                if (word == null) {

                    //If there's no word, add a placeholder.
                    char c = '.';
                    colRef[colPos] = c;

                } else if (pos < word.length()) {

                    //If there is a word, and it's long enough,
                    //get the letter for this column.
                    char c = word.charAt(pos);
                    colRef[colPos] = c;

                } else {

                    //Otherwise, add a placeholder for this column.
                    char c = '.';
                    colRef[colPos] = c;
                }

                colPos++;
            }
        }

        //Make all characters upper case
        for (int i = 0; i < colRef.length; i++) {

            //Convert to string.
            char c = colRef[i];
            String s = Character.toString(c);

            //Make uppercase.
            s = s.toUpperCase();

            //Convert back.
            c = s.charAt(0);
            colRef[i] = c;
        }

        //Set watched variables for display by pulling from temporary array.
        c1 = colRef[0];
        c2 = colRef[1];
        c3 = colRef[2];
        c4 = colRef[3];
        c5 = colRef[4];
        c6 = colRef[5];
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
