package WordSquare;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A JavaFX controller class to provide a GUI for the operation of
 * {@code WordSquare}.
 */
public class WordSquareController {
    protected WordSquare ws = new WordSquare();
    // A local copy of solution list for reordering.
    protected static ArrayList<Solution> solutionList =
            new ArrayList<Solution>();
    // The current solution to display.
    private int solPos = 0;
    // A local copy of squareWords for the display of incomplete word squares.
    private String[] squareWords;
    // A JavaFX Service to monitor running searches.
    private SearchWatcher searchWatcher = new SearchWatcher();
    // During search, automatically display the first found solution.
    private boolean firstSolutionsUpdate = true;
    // Maintain references to scenes and controllers for switching between
    // popup and main application windows.
    private static StageReference stageReference = new StageReference();
    // Maintain a DisplayRow for each squareWords position for display.
    private DisplayRow r1 = new DisplayRow();
    private DisplayRow r2 = new DisplayRow();
    private DisplayRow r3 = new DisplayRow();
    private DisplayRow r4 = new DisplayRow();
    private DisplayRow r5 = new DisplayRow();
    private DisplayRow r6 = new DisplayRow();
    // These values will be used to update the display table.
    private final ObservableList<DisplayRow> displayText =
            FXCollections.observableArrayList(r1, r2, r3, r4, r5, r6);

    /*------------------------------------------------
     *
     * Make JavaFX fxml elements available as class members.
     *
     -------------------------------------------------*/
    @FXML public ChoiceBox wordPosition;
    @FXML public TextField textIn;
    @FXML public TableView textOut;
    @FXML public TableColumn c0;
    @FXML public TableColumn c1;
    @FXML public TableColumn c2;
    @FXML public TableColumn c3;
    @FXML public TableColumn c4;
    @FXML public TableColumn c5;
    @FXML public TableColumn c6;
    @FXML public CheckBox lock0;
    @FXML public CheckBox lock1;
    @FXML public CheckBox lock2;
    @FXML public CheckBox lock3;
    @FXML public CheckBox lock4;
    @FXML public CheckBox lock5;
    @FXML public Label totalSol;
    @FXML public TextField solPosDisplay;
    @FXML public ProgressBar progressBar;
    @FXML public ChoiceBox sortStyle;
    @FXML public Button sortButton;
    @FXML public AnchorPane searchPane;
    @FXML public Button cancelButton;
    @FXML public Button clearButton;


    /**
     * Initialize GUI elements and helpers.  This method is called once by the
     * Application at the start of the program.
     */
    public void initialize() {
        // Reset search watcher so that progressBar can 'lock on.'
        searchWatcher.reset();
        searchWatcher.start();

        // Populate word position drop down
        wordPosition.getItems().removeAll(wordPosition.getItems());
        wordPosition.getItems().addAll(1,2,3,4,5,6);
        wordPosition.getSelectionModel().select(0);

        // Populate searchStyle drop down
        sortStyle.getItems().removeAll(sortStyle.getItems());
        sortStyle.getItems().addAll("Total", "Low Word", "Average");
        sortStyle.getSelectionModel().select(1);
        sortStyle.setDisable(true);
        sortButton.setDisable(true);

        // Set up value factories for the textOut table.
        textOut.setEditable(false);
        c0.setCellValueFactory(new PropertyValueFactory<DisplayRow, Integer>("displayPos"));
        c1.setCellValueFactory(new PropertyValueFactory<DisplayRow, Character>("c1"));
        c2.setCellValueFactory(new PropertyValueFactory<DisplayRow, Character>("c2"));
        c3.setCellValueFactory(new PropertyValueFactory<DisplayRow, Character>("c3"));
        c4.setCellValueFactory(new PropertyValueFactory<DisplayRow, Character>("c4"));
        c5.setCellValueFactory(new PropertyValueFactory<DisplayRow, Character>("c5"));
        c6.setCellValueFactory(new PropertyValueFactory<DisplayRow, Character>("c6"));
        textOut.setItems(displayText);
        squareWords = ws.getSquareWords();
        updateDisplay(squareWords);

        // Bind progressBar to SearchWatcher.
        progressBar.progressProperty().bind(searchWatcher.progressProperty());
    }

    /*------------------------------------------------
    *
    * Methods to respond to user input.
    *
    * ------------------------------------------------*/

    /**
     * Attempts to set the word currently in the text entry box as a fixed
     * word square word at the position indicated by the word position drop
     * down menu.
     */
    public void setSquareWord() {
        // Get word and position to set.
        int pos = wordPosition.getSelectionModel().getSelectedIndex();
        String word = textIn.getText();

        // Check to make sure word is legal length and legal position.
        if (word != null) {
            int len = word.length();
            if ((len < 2) || (len > 6)) {
                popup("Entry must be between 2 and 6 letters long.");
                return;
            }
            if ((pos+1) > len) {
                popup("Invalid position.  Word Squares must begin at position 1, 1.");
                return;
            }

            // Make word lowercase.
            word = word.toLowerCase();

            // Check to see if word fits the pattern
            if (ws.wordFits(word, pos)) {
                ws.setWord(word, pos);
            } else {
                popup("That word won't fit here.");
                return;
            }
        }

        // Switch display state.
        solPos = 0;
        sortStyle.setDisable(true);
        sortButton.setDisable(true);

        // Update display.
        squareWords = ws.getSquareWords();
        updateDisplay(squareWords);

        // Lock the new word in for eventual search.
        switch (pos) {
            case 0: lock0.setSelected(true); break;
            case 1: lock1.setSelected(true); break;
            case 2: lock2.setSelected(true); break;
            case 3: lock3.setSelected(true); break;
            case 4: lock4.setSelected(true); break;
            case 5: lock5.setSelected(true); break;
        }
    }

    /**
     * Initiate the search process.
     */
    public void buildWordSquares() {
        // Clear old solution list, if any
        solutionList.clear();
        // Clear display elements for new solutions.
        solPos = 0;
        solPosDisplay.setText("0");
        updateSolutionCount();
        // Switch display state.
        searchPane.setDisable(true);
        // Prepare to jump display to the first found solution.
        firstSolutionsUpdate = true;

        // Loop through the checkboxes to set fixed words and blanks
        CheckBox[] lockList = {lock0, lock1, lock2, lock3, lock4, lock5};
        for (int i = 0; i < 6; i++) {
            CheckBox lock = lockList[i];
            if (lock.isSelected()) {
                ws.setWord(squareWords[i], i);
            } else {
                ws.setWord(null, i);
            }
        }
        // Call WordSquare to start the search.
        ws.buildAllSolutions();

        // Reinitialize search watcher for this search.
        searchWatcher.reset();
        searchWatcher.start();
    }

    /**
     * Stop a search in progress.
     */
    public void cancelSearch() {
        ws.killSearch();
        sortSolutions();
    }

    /**
     * Display the next solution in the list.  Wrap to index zero when
     * end of list is reached.
     */
    public void showNextSolution() {
        if (solutionList.size() > 0) {
            solPos++;
            if (solPos >= solutionList.size()) {
                solPos = 0;
            }
            showSolution();
        }
    }

    /**
     * Display the previous solution in the list.  Wrap to last item
     * when index zero is reached.
     */
    public void showPreviousSolution() {
        if (solutionList.size() > 0) {
            solPos--;
            if (solPos < 0) {
                solPos = (solutionList.size() - 1);
            }
            showSolution();
        }
    }

    /**
     * Display a specific solution by its index in the solution list.
     * Index is adjusted to eliminate zero-index confusion for users (i.e.
     * the user may treat the first solution in the list as {@code 1} rather
     * than {@code 0}.
     */
    public void jumpToSolution() {
        // Deal with invalid entries.
        try {
            // Get the request.
            int solPosRequest = solPosDisplayToInt();
            // If request is negative, just show the first solution.
            if (solPosRequest < 0) {
                solPosRequest = 0;
            }
            // If request is greater than list length,
            // just show the last solution.
            if (solPosRequest < solutionList.size()) {
                solPos = solPosRequest;
            } else {
                solPos = solutionList.size() - 1;
            }
            // Update display with the new solution.
            showSolution();
        } catch (NumberFormatException e) {
            // If user entered something other than a number, make a popup.
            popup("Enter a number.");
        }
    }

    /**
     * Show the popup window scene with a custom message.
     * @param text The message to display.
     */
    private void popup(String text) {
        try {
            PopupController pc = stageReference.getPopupController();
            pc.setPopText(text);
            Stage popupStage = stageReference.getPopupStage();
            popupStage.show();

        } catch (Exception e) {e.printStackTrace();}
    }

    /*------------------------------------------------
    *
    * Methods to update data and GUI elements.
    *
    * ------------------------------------------------*/

    /**
     * Display a partial word square or a valid solution in the display table.
     * @param squareWords The array of words to display.  Must be no more than
     *                    six words of length no longer than six characters.
     */
    public void updateDisplay(String[] squareWords) {
        r1.setChars(squareWords, 0);
        r2.setChars(squareWords, 1);
        r3.setChars(squareWords, 2);
        r4.setChars(squareWords, 3);
        r5.setChars(squareWords, 4);
        r6.setChars(squareWords, 5);
        textOut.refresh();
    }

    /**
     * Clear the local copy of squareWords from memory.
     */
    public void clearLocalSquareWords() {
        for (int i = 0; i < 6; i++) {
            squareWords[i] = null;
        }
    }

    /**
     * Clear all user-input data and data derived therefrom from memory,
     * and reset the GUI elements.
     */
    public void clearAll() {
        cancelSearch();
        ws.killSearch();
        ws = new WordSquare();
        clearLocalSquareWords();
        solutionList.clear();
        updateDisplay(squareWords);
        updateSolutionCount();
        firstSolutionsUpdate = true;
        solPos = 0;
        solPosDisplay.setText("0");
        textIn.clear();
    }

    /**
     * Called by {@code SearchWatcher} when new solutions are found.
     */
    public void updateSolutionCount() {
        // Get the current number of solutions and update the GUI.
        int size = solutionList.size();
        totalSol.setText(Integer.toString(size));
        // If this is the first solution found, show it right away.
        if ((size > 0) && (firstSolutionsUpdate)) {
            showSolution();
            firstSolutionsUpdate = false;
        }
    }

    /**
     * Update the display with a new solution.
     */
    public void showSolution() {
        clearLocalSquareWords();
        Solution sol = solutionList.get(solPos);
        String[] solWords = sol.getSolutionWords();
        for (int i = 0; i < solWords.length; i++) {
            squareWords[i] = solWords[i];
        }
        updateDisplay(squareWords);
        solPosDisplay.setText(Integer.toString(solPos + 1));
    }

    /**
     * Accept user-input solution index position.
     * @return Returns an {@code int} representing the solution index
     * requested by the user, adjusted for zero-index.
     * @throws NumberFormatException If user input is something other
     * than a number.
     */
    private int solPosDisplayToInt() throws NumberFormatException {
        int solPosNum;
        String raw = solPosDisplay.getText();
        raw = raw.trim();
        solPosNum = Integer.parseInt(raw);
        solPosNum--;
        return solPosNum;
    }

    /**
     * Sorts the solution list based on one of three rankings (total score,
     * average score, worst individual word score).  Ranking style is indicated
     * by the {@code sortStyle} dropdown menu.
     */
    public void sortSolutions() {
        // Only perform the sort if there's multiple solutions.
        if (solutionList.size() >= 2) {
            // Get the sort style requested by user.
            int sortStyleSelection = sortStyle.getSelectionModel().getSelectedIndex();
            // Sort according to user preference.
            switch (sortStyleSelection) {
                // Rank by total score.
                case 0:
                    solutionList.sort(new Comparator<Solution>() {
                        @Override
                        public int compare(Solution o1, Solution o2) {
                            return Long.compare(o2.getTotalScore(), o1.getTotalScore());
                        }
                    });
                    break;
                // Rank by low word.
                case 1:
                    solutionList.sort(new Comparator<Solution>() {
                        @Override
                        public int compare(Solution o1, Solution o2) {
                            return Long.compare(o2.getLowScore(), o1.getLowScore());
                        }
                    });
                    break;
                // Rank by average word score.
                case 2:
                    solutionList.sort(new Comparator<Solution>() {
                        @Override
                        public int compare(Solution o1, Solution o2) {
                            return Long.compare(o2.getAverageScore(), o1.getAverageScore());
                        }
                    });
                    break;
            }
            // Show the top solution and update display.
            solPos = 0;
            solPosDisplay.setText(Integer.toString(solPos + 1));
            showSolution();
        }
    }

    /**
     * A JavaFX Service to monitor long-running searches.  As search proceeds,
     * {@code SearchWatcher} updates the progress bar and the total solution
     * count for display.  A separate service is needed so that the GUI doesn't
     * freeze when a long search is running.
     */
    private class SearchWatcher extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Track progress.
                    int[] progress;
                    // Keep running until WordSquare completes its search.
                    do {
                        // Update search progress.
                        progress = ws.getSearchProgress();
                        updateProgress(progress[0], progress[1]);

                        // Make sure the GUI only automatically jumps
                        // to the first solution found.
                        if (firstSolutionsUpdate) {
                            if (ws.getSolutionList().size() > 0) {
                                firstSolutionsUpdate = false;
                            }
                        }

                        // Get the latest solution list and ask the GUI
                        // to update at its leisure.
                        solutionList = ws.getSolutionList();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                updateSolutionCount();
                            }
                        });

                        // Wait a bit before checking for more solutions.
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (progress[0] < progress[1]);

                    // Switch display state when search is complete.
                    sortStyle.setDisable(false);
                    sortButton.setDisable(false);
                    searchPane.setDisable(false);
                    if (solPos == 0) { sortSolutions(); }
                    firstSolutionsUpdate = true;
                    ws.killSearch();
                    ws = new WordSquare();
                    return null;
                }
            };
        }
    }
}
