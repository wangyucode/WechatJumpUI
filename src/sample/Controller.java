package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller {

    @FXML
    Button btnStart;

    @FXML
    TextArea taLog;

    @FXML
    ImageView image;

    private Hack hack;

    @FXML
    protected void onStartClicked(ActionEvent event) {
        btnStart.setDisable(true);
        hack = new Hack(this);
        hack.start();
    }

    @FXML
    protected void onStopClicked(ActionEvent event) {
        hack.isStop = true;
    }

    @FXML
    protected void onClearClicked(ActionEvent event) {
        taLog.setText("");
    }

    public void appendText(String s) {
        taLog.appendText(s);
    }

    public void setStopped() {
        btnStart.setDisable(false);
    }

    public void setImage(Image img) {
        Utils.onFXThread(image.imageProperty(),img);
    }
}
