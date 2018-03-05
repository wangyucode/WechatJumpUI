package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Controller {

    @FXML
    Button btnStart;

    @FXML
    TextArea taLog;


    @FXML
    protected void onStartClicked(ActionEvent event) {
        btnStart.setDisable(true);
        new Hack(taLog).start();
    }
}
