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

public class WordSquareController {
    protected WordSquare ws = new WordSquare();
    protected static ArrayList<Solution> solutionList = new ArrayList<Solution>();
    private int solPos = 0;
    private String[] squareWords;
    private SearchWatcher searchWatcher = new SearchWatcher();
    private boolean firstSolutionsUpdate = true;
    private static StageReference stageReference = new StageReference();
    private DisplayRow r1 = new DisplayRow();
    private DisplayRow r2 = new DisplayRow();
    private DisplayRow r3 = new DisplayRow();
    private DisplayRow r4 = new DisplayRow();
    private DisplayRow r5 = new DisplayRow();
    private DisplayRow r6 = new DisplayRow();
    private final ObservableList<DisplayRow> displayText = FXCollections.observableArrayList(r1, r2, r3, r4, r5, r6);

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


    //Initialize elements
    public void initialize() {
        searchWatcher.reset();
        searchWatcher.start();

        //word position drop down
        wordPosition.getItems().removeAll(wordPosition.getItems());
        wordPosition.getItems().addAll(1,2,3,4,5,6);
        wordPosition.getSelectionModel().select(0);

        //searchStyle drop down
        sortStyle.getItems().removeAll(sortStyle.getItems());
        sortStyle.getItems().addAll("Total", "Low Word", "Average");
        sortStyle.getSelectionModel().select(1);
        sortStyle.setDisable(true);
        sortButton.setDisable(true);

        //textOut table
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

        //progress bar
        progressBar.progressProperty().bind(searchWatcher.progressProperty());
    }

    public void updateDisplay(String[] squareWords) {
        r1.setChars(squareWords, 0);
        r2.setChars(squareWords, 1);
        r3.setChars(squareWords, 2);
        r4.setChars(squareWords, 3);
        r5.setChars(squareWords, 4);
        r6.setChars(squareWords, 5);
        textOut.refresh();
    }

    public void clearLocalSquareWords() {
        for (int i = 0; i < 6; i++) {
            squareWords[i] = null;
        }
    }

    public void setSquareWord() {
        int pos = wordPosition.getSelectionModel().getSelectedIndex();
        String word = textIn.getText();
        //check to make sure word is legal length and legal position
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

            //make word lowercase;
            word = word.toLowerCase();

            //check to see if word fits the pattern
            if (ws.wordFits(word, pos)) {
                ws.setWord(word, pos);
            } else {
                popup("That word won't fit here.");
                return;
            }
        }

        solPos = 0;
        sortStyle.setDisable(true);
        sortButton.setDisable(true);

        squareWords = ws.getSquareWords();
        updateDisplay(squareWords);

        switch (pos) {
            case 0: lock0.setSelected(true); break;
            case 1: lock1.setSelected(true); break;
            case 2: lock2.setSelected(true); break;
            case 3: lock3.setSelected(true); break;
            case 4: lock4.setSelected(true); break;
            case 5: lock5.setSelected(true); break;
        }
    }

    public void buildWordSquares() {
        //Clear old solution list, if any
        solutionList.clear();
        solPos = 0;
        solPosDisplay.setText("0");
        updateSolutionCount();
        searchPane.setDisable(true);
        firstSolutionsUpdate = true;

        //Loop through the checkboxes to set fixed words and blanks
        CheckBox[] lockList = {lock0, lock1, lock2, lock3, lock4, lock5};
        for (int i = 0; i < 6; i++) {
            CheckBox lock = lockList[i];
            if (lock.isSelected()) {
                ws.setWord(squareWords[i], i);
            } else {
                ws.setWord(null, i);
            }
        }
        ws.buildAllSolutions();
        searchWatcher.reset();
        searchWatcher.start();
    }

    public void cancelSearch() {
        ws.killSearch();
        sortSolutions();
        ws = new WordSquare();
    }

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

    public void updateSolutionCount() {
        int size = solutionList.size();
        totalSol.setText(Integer.toString(size));
        if ((size > 0) && (firstSolutionsUpdate)) {
            showSolution();
            firstSolutionsUpdate = false;
        }
    }

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

    public void showNextSolution() {
        if (solutionList.size() > 0) {
            solPos++;
            if (solPos >= solutionList.size()) {
                solPos = 0;
            }
            showSolution();
        }
    }

    public void showPreviousSolution() {
        if (solutionList.size() > 0) {
            solPos--;
            if (solPos < 0) {
                solPos = (solutionList.size() - 1);
            }
            showSolution();
        }
    }

    public void jumpToSolution() {
        try {
            int solPosRequest = solPosDisplayToInt();
            if (solPosRequest < 0) {
                solPosRequest = 0;
            }
            if (solPosRequest < solutionList.size()) {
                solPos = solPosRequest;
            } else {
                solPos = solutionList.size() - 1;
            }
            showSolution();
        } catch (NumberFormatException e) {
            popup("Enter a number.");
        }
    }

    private int solPosDisplayToInt() throws NumberFormatException {
        int solPosNum;
        String raw = solPosDisplay.getText();
        raw = raw.trim();
        solPosNum = Integer.parseInt(raw);
        solPosNum--;
        return solPosNum;
    }

    private void popup(String text) {
        try {
            PopupController pc = stageReference.getPopupController();
            pc.setPopText(text);
            Stage popupStage = stageReference.getPopupStage();
            popupStage.show();

        } catch (Exception e) {e.printStackTrace();}
    }

    public void sortSolutions() {
        if (solutionList.size() >= 2) {
            int sortStyleSelection = sortStyle.getSelectionModel().getSelectedIndex();
            switch (sortStyleSelection) {
                //rank by total score
                case 0:
                    solutionList.sort(new Comparator<Solution>() {
                        @Override
                        public int compare(Solution o1, Solution o2) {
                            return Long.compare(o2.getTotalScore(), o1.getTotalScore());
                        }
                    });
                    break;
                //rank by low word
                case 1:
                    solutionList.sort(new Comparator<Solution>() {
                        @Override
                        public int compare(Solution o1, Solution o2) {
                            return Long.compare(o2.getLowScore(), o1.getLowScore());
                        }
                    });
                    break;
                //rank by average word score
                case 2:
                    solutionList.sort(new Comparator<Solution>() {
                        @Override
                        public int compare(Solution o1, Solution o2) {
                            return Long.compare(o2.getAverageScore(), o1.getAverageScore());
                        }
                    });
                    break;
            }
            solPos = 0;
            solPosDisplay.setText(Integer.toString(solPos + 1));
            showSolution();
        }
    }

    private class SearchWatcher extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int [] progress;
                    do {
                        progress = ws.getSearchProgress();
                        updateProgress(progress[0], progress[1]);
                        if (firstSolutionsUpdate) {
                            if (ws.getSolutionList().size() > 0) {
                                firstSolutionsUpdate = false;
                            }
                        }
                        solutionList = ws.getSolutionList();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                updateSolutionCount();
                            }
                        });
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (progress[0] < progress[1]);

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
