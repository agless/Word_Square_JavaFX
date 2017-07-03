package WordSquare;

import javafx.stage.Stage;

/**
 * Created by andyg on 6/22/2017.
 */
public class StageReference {
    private static Stage primaryStage;
    private static Stage popupStage;
    private static WordSquareController wordSquareController;
    private static PopupController popupController;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }

    public void setWordSquareController(WordSquareController wordSquareController) {
        this.wordSquareController = wordSquareController;
    }

    public void setPopupController(PopupController popupController) {
        this.popupController = popupController;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Stage getPopupStage() {
        return popupStage;
    }

    public WordSquareController getWordSquareController() {
        return wordSquareController;
    }

    public PopupController getPopupController() {
        return popupController;
    }
}
