package WordSquare;

import javafx.stage.Stage;

/**
 * {@code StageReference} is a class to hold and provide access to GUI stages
 * and controllers.
 */
public class StageReference {
    private static Stage primaryStage;
    private static Stage popupStage;
    private static WordSquareController wordSquareController;
    private static PopupController popupController;

    /**
     * Set the primary stage reference.
     * @param primaryStage A reference to the primary stage.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Set the popup stage reference.
     * @param popupStage A reference to the popup stage.
     */
    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }

    /**
     * Set the GUI controller for the main program.
     * @param wordSquareController A reference to the main program GUI
     *                             controller.
     */
    public void setWordSquareController(WordSquareController wordSquareController) {
        this.wordSquareController = wordSquareController;
    }

    /**
     * Set the GUI controller for the popup window.
     * @param popupController A reference to the popup window GUI
     *                        controller.
     */
    public void setPopupController(PopupController popupController) {
        this.popupController = popupController;
    }

    /**
     * Get the primary stage reference.
     * @return Returns a reference to the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Get the popup stage reference.
     * @return Returns a reference to the popup stage.
     */
    public Stage getPopupStage() {
        return popupStage;
    }

    /**
     * Get the GUI controller for the main program.
     * @return Returns a reference to the GUI controller for the main program.
     */
    public WordSquareController getWordSquareController() {
        return wordSquareController;
    }

    /**
     * Get the GUI controller for the popup window.
     * @return Returns a reference to the GUI controller for the popup window.
     */
    public PopupController getPopupController() {
        return popupController;
    }
}
