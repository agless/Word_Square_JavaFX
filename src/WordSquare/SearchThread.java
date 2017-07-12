package WordSquare;

import java.util.regex.Pattern;

public class SearchThread implements Runnable {  //better to extend Thread?
    private WordSquare ws;

    SearchThread(WordSquare ws) {
        this.ws = ws;
    }

    @Override
    public void run() {

        Dictionary dict = new Dictionary();
        Score scorer = new Score();
        String[] squareWords = ws.getSquareWords();
        int[][] searchRows = ws.getSearchRows();
        String branchWord = ws.getBranchWord();
        squareWords[searchRows[0][0]] = branchWord;
        int len = branchWord.length();
        int pos = 1;
        int squarePos;
        int dictPos;

        while (pos >= 1) {
            //Recursively iterate through each row in searchRow
            squarePos = searchRows[pos][0];
            squareWords[squarePos] = null;
            dictPos = searchRows[pos][1];
            Pattern searchPattern = dict.buildSearchPattern(squareWords, squarePos, len);
            Match match = dict.getNextMatch(searchPattern, dictPos, len);
            if (match == null) { //end of wordBank reached without a match
                //set dictPos back to zero for this failed row
                searchRows[pos][1] = 0;
                pos--; //drop down a row to search the next branch

            } else {
                //Add the new word to squareWords and move on to the next row
                squareWords[squarePos] = match.getWord();
                searchRows[pos][1] = (match.getDictPos() + 1);
                pos++;
                if (pos == searchRows.length) { //a complete solution has been found
                    //Make a solution object and add it to the solutions list.
                    //Solution object needs an array with no empty spaces
                    String[] solWords = new String[len];
                    System.arraycopy(squareWords, 0, solWords, 0, len);
                    Solution sol = new Solution(solWords, scorer);
                    ws.addSolution(sol);
                    //Drop back down to keep searching
                    pos--;
                }
            }
        }
        ws.searchThreadComplete();
    }
}
