package WordSquare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private StageReference sr = new StageReference();

    @Override
    public void start(Stage primaryStage) throws Exception{
        //main scene
        FXMLLoader primaryLoader = new FXMLLoader();
        Parent root = primaryLoader.load(getClass().getResource("WordSquareGUI.fxml").openStream());
        WordSquareController wsc = primaryLoader.getController();
        sr.setWordSquareController(wsc);
        sr.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Word Square");
        primaryStage.setScene(new Scene(root, 560, 360));
        primaryStage.show();

        //popup scene
        Stage popupStage = new Stage();
        FXMLLoader popupLoader = new FXMLLoader();
        Parent popRoot = popupLoader.load(getClass().getResource("Popup.fxml").openStream());
        PopupController pc = popupLoader.getController();
        popupLoader.setController(pc);
        sr.setPopupController(pc);
        sr.setPopupStage(popupStage);
        popupStage.setTitle("Word Square");
        popupStage.setScene(new Scene(popRoot, 300, 200));
    }

    public static void main(String[] args) {
        launch(args);
    }

}