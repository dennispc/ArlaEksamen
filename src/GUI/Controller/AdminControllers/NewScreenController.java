package GUI.Controller.AdminControllers;

import BE.Screen;
import GUI.Model.ScreenModel;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NewScreenController {
    @FXML
    private JFXTextField txtScreenName;
    @FXML
    private AnchorPane root;

    private ScreenModel screenModel = new ScreenModel();


    public String handleContinue() {
        if (!txtScreenName.getText().isEmpty()){
            screenModel.getAllScreens().add(0, new Screen(txtScreenName.getText()));

            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
            return txtScreenName.getText();
        }
        return null;
    }

    public void handleCancel() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }


}