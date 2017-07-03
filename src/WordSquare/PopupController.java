package WordSquare;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Created by andyg on 6/21/2017.
 */
public class PopupController {
    StageReference stageReference = new StageReference();

    @FXML public Button okButton;
    @FXML public Label popText;

    public void setPopText(String text) {
        popText.setText(text);
    }

    public void okButtonClick() {
        Stage myStage = stageReference.getPopupStage();
        myStage.hide();
    }
}
