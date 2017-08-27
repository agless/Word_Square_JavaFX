package WordSquare;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * {@code PopupController} is a JavaFX controller class for the
 * popup window scene.
 */
public class PopupController {
    StageReference stageReference = new StageReference();

    /*------------------------------------------------
    *
    * Make JavaFX FXML elements available as class members.
    *
    * ------------------------------------------------*/
    @FXML public Button okButton;
    @FXML public Label popText;

    /**
     * Set the text for the popup window.
     * @param text The text to be displayed in the popup window.
     */
    public void setPopText(String text) {
        popText.setText(text);
    }

    /**
     * Hide the popup window when the 'OK' button is pressed.
     */
    public void okButtonClick() {
        Stage myStage = stageReference.getPopupStage();
        myStage.hide();
    }
}