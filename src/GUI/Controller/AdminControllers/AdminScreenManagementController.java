package GUI.Controller.AdminControllers;

import BE.Screen;
import GUI.Controller.PopupControllers.ConfirmationController;
import GUI.Model.ScreenModel;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class AdminScreenManagementController implements Initializable {
    @FXML
    private FlowPane root;

    private double xOffset = 0;
    private double yOffset = 0;

    private ScreenModel screenModel = new ScreenModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(Screen s : screenModel.getAllScreens()){
            handleNewScreen(s);
        }
    }

    private void handleNewScreen(Screen screen) {
        Pane newPane = new Pane();
        newPane.setPrefSize(150, 150);

        Rectangle newRectangle = new Rectangle(150, 150);
        newRectangle.setArcHeight(50);
        newRectangle.setArcWidth(50);
        newRectangle.setFill(Paint.valueOf("#154c5d"));
        newRectangle.getStyleClass().add("SMButtons");

        MaterialDesignIconView settings = new MaterialDesignIconView();
        settings.setIcon(MaterialDesignIcon.SETTINGS);
        settings.setFill(Paint.valueOf("#0d262e"));
        settings.getStyleClass().add("SMButtons");
        settings.setLayoutX(116);
        settings.setLayoutY(31);
        settings.setSize(String.valueOf(20));

        MaterialDesignIconView desktop = new MaterialDesignIconView();
        desktop.setIcon(MaterialDesignIcon.MONITOR);
        desktop.setFill(Paint.valueOf("#0d262e"));
        desktop.getStyleClass().add("SMButtons");
        desktop.setLayoutX(39);
        desktop.setLayoutY(102);
        desktop.setSize(String.valueOf(72));

        Label label = new Label();
        label.setText(screen.getName());
        label.setTextFill(Paint.valueOf("#FFFFFF"));
        label.setFont(new Font("System", 16));
        label.setStyle("-fx-font-weight: bold; -fx-font-style: italic");
        label.setPrefSize(133,25);
        label.setLayoutX(9);
        label.setLayoutY(111);
        label.setAlignment(Pos.TOP_CENTER);
        label.setContentDisplay(ContentDisplay.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);

        newPane.getChildren().addAll(newRectangle, settings, desktop, label);

        root.getChildren().add(0, newPane);

        FlowPane.setMargin(newPane, new Insets(25, 25, 0, 25));

        desktop.setOnMouseClicked(mouseEvent -> {
            try {
                handleScreenCreator(screen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        newRectangle.setOnMouseClicked(mouseEvent -> {
            try {
                handleScreenCreator(screen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        settings.setOnMouseClicked(mouseEvent -> {
            try {
                handleEditScreen(screen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleScreenCreator(Screen screen) throws IOException {
        Stage pickerDashboard = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GUI/VIEW/AdminViews/PickerDashboard.fxml"));

        Parent root = (Parent) loader.load();
        PickerDashboardController pickerDashboardController = loader.getController();
        pickerDashboardController.setTitle(screen.getName());
        pickerDashboardController.init(screen);

        Scene pickerScene = new Scene(root);

        pickerScene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        pickerScene.setOnMouseDragged(event -> {
            pickerDashboard.setX(event.getScreenX() - xOffset);
            pickerDashboard.setY(event.getScreenY() - yOffset);
            pickerDashboard.setOpacity(0.8f);
        });

        pickerScene.setOnMouseDragExited((event) -> {
            pickerDashboard.setOpacity(1.0f);
        });

        pickerScene.setOnMouseReleased((event) -> {
            pickerDashboard.setOpacity(1.0f);
        });

        pickerDashboard.initStyle(StageStyle.UNDECORATED);
        pickerDashboard.setScene(pickerScene);
        pickerDashboard.show();
    }


    public void handleCreateScreen() throws IOException {
        NewScreenDialog screenDialog = new NewScreenDialog("Test");

        Optional<String> result = screenDialog.showAndWait();

        if (result.isPresent()) {
            //TODO VIRKER, men skal lige finde ud af hvad det kræver at indsætte i DB'en
            //screenModel.addScreen(new Screen(result.get()));

            handleNewScreen(new Screen(result.get()));
        }
    }

    public void handleEditScreen(Screen screen) throws IOException {
        //TODO lav fxml til edit screen.
        Stage editScreenStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GUI/VIEW/AdminViews/EditScreen.fxml"));

        Parent root = (Parent) loader.load();
        EditScreenController editScreenController = loader.getController();
        editScreenController.setScreen(screen);

        Scene editScreenScene = new Scene(root);

        editScreenScene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        editScreenScene.setOnMouseDragged(event -> {
            editScreenStage.setX(event.getScreenX() - xOffset);
            editScreenStage.setY(event.getScreenY() - yOffset);
            editScreenStage.setOpacity(0.8f);
        });

        editScreenScene.setOnMouseDragExited((event) -> {
            editScreenStage.setOpacity(1.0f);
        });

        editScreenScene.setOnMouseReleased((event) -> {
            editScreenStage.setOpacity(1.0f);
        });

        editScreenStage.initStyle(StageStyle.UNDECORATED);
        editScreenStage.setScene(editScreenScene);
        editScreenStage.show();
    }


}