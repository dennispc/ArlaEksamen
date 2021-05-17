package GUI.Controller;

import BE.*;
import BLL.LoginManager;
import GUI.Controller.PopupControllers.BugReportDialog;
import GUI.Controller.PopupControllers.ConfirmationDialog;
import GUI.Controller.PopupControllers.EScreenSelectDialog;
import GUI.Controller.PopupControllers.WarningController;
import GUI.Model.MessageModel;
import GUI.Model.ScreenModel;
import GUI.Model.UserModel;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static BLL.DataNodes.ViewType.Image;

public class EmployeeScreenController implements Initializable {
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label lblBar;
    @FXML
    private TextArea txtMessage;
    @FXML
    private JFXComboBox<ScreenBit> comboScreens;
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private List<Message> userMessages = new ArrayList<>();

    private User currentUser;
    private boolean isMaximized = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = LoginManager.getCurrentUser();

        comboScreens.getItems().addAll(currentUser.getAssignedScreen());

        if (!currentUser.getAssignedScreen().isEmpty()) {
            if (currentUser.getAssignedScreen().size() == 1) {
                try {
                    setScreen(currentUser.getAssignedScreen().get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    EScreenSelectDialog selectDialog = new EScreenSelectDialog(currentUser.getAssignedScreen());

                    Optional<ScreenBit> results = selectDialog.showAndWait();

                    if (results.isPresent()) {
                        ScreenBit s = results.get();
                        lblBar.setText("Employee Screen - " + s.getName() + " - " + currentUser.getFirstName() + " " + currentUser.getLastName());
                        comboScreens.getSelectionModel().select(s);
                        try {
                            setScreen(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            lblBar.setText("Employee Screen - NONE Contact admin - " + currentUser.getFirstName() + " " + currentUser.getLastName());
            try {
                displayNoScreenWarning();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        comboScreens.setOnAction(e -> {
            if (comboScreens.getSelectionModel().getSelectedItem() != null) {
                try {
                    setScreen(comboScreens.getValue());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        service.scheduleAtFixedRate(new Thread(() -> {
            userMessages = MessageModel.getInstance().getUsersMessages(currentUser);
            userMessages.forEach(message->{
            if (txtMessage.getText() == message.getMessage() || LocalDateTime.now().isBefore(message.getMessageStartTime()) || LocalDateTime.now().isAfter(message.getMessageEndTime())) {
            }
            else
                updateMessage(message);
            });
        }),0,5, TimeUnit.MINUTES);
    }

    private void updateMessage(Message message){
        Platform.runLater(new Thread(()->{
            String textColor = String.format("rgb( %s , %s , %s )",message.getTextColor().getRed()*255,message.getTextColor().getGreen()*255,message.getTextColor().getBlue()*255);
            String highLightTextFillColor = String.format("rgb( %s , %s , %s )",message.getTextColor().brighter().getRed()*255,message.getTextColor().brighter().getGreen()*255,message.getTextColor().brighter().getBlue()*255);
            String hightLightColor = String.format("rgb( %s , %s , %s )",message.getTextColor().darker().getRed()*255,message.getTextColor().darker().getGreen()*255,message.getTextColor().darker().getBlue()*255);
            txtMessage.setStyle(String.format("-fx-text-fill: %s; -fx-highlight-text-fill: %s; -fx-highlight-fill: %s;",textColor,highLightTextFillColor,hightLightColor));
            txtMessage.setText(message.getMessage());
        }));
    }

    private void displayNoScreenWarning() throws IOException {
        String text = "You have not been assigned a screen yet. \n\n" +
                "Please, contact an IT administrator as soon as possible, to resolve this problem.";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GUI/View/PopUpViews/Warning.fxml"));
        Parent root = (Parent) loader.load();

        WarningController warningController = loader.getController();
        warningController.setText(text);
        Stage stage = new Stage();

        Scene scene = new Scene(root);

        stage.getIcons().addAll(
                new Image("/GUI/Resources/AppIcons/icon16x16.png"),
                new Image("/GUI/Resources/AppIcons/icon24x24.png"),
                new Image("/GUI/Resources/AppIcons/icon32x32.png"),
                new Image("/GUI/Resources/AppIcons/icon48x48.png"),
                new Image("/GUI/Resources/AppIcons/icon64x64.png"));
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();

    }

    private void setScreen(ScreenBit s) throws Exception {
        StageBuilder stageBuilder = new StageBuilder();
        Node screen = stageBuilder.makeStage(s.getScreenInfo());
        stageBuilder.getRootController().lockPanes();
        borderPane.setCenter(screen);
    }

    public void handleLogout() throws IOException {
        ConfirmationDialog confirmation = new ConfirmationDialog("Are you sure you want to logout of the application?");

        Optional<Boolean> result = confirmation.showAndWait();

        if (result.isPresent()) {
            if (result.get()) {
                Stage root1 = (Stage) borderPane.getScene().getWindow();

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/GUI/VIEW/Login.fxml"));

                Scene scene = new Scene(fxmlLoader.load());

                stage.getIcons().addAll(
                        new Image("/GUI/Resources/AppIcons/icon16x16.png"),
                        new Image("/GUI/Resources/AppIcons/icon24x24.png"),
                        new Image("/GUI/Resources/AppIcons/icon32x32.png"),
                        new Image("/GUI/Resources/AppIcons/icon48x48.png"),
                        new Image("/GUI/Resources/AppIcons/icon64x64.png"));
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.show();
                LoginManager.setCurrentUser(null);
                root1.close();

                BorderPane borderPane1 = (BorderPane) stage.getScene().getRoot();

                SceneMover sceneMover = new SceneMover();
                sceneMover.move(stage, borderPane1.getTop());
            }
        }
    }

    @FXML
    private void handleMinimize(MouseEvent mouseEvent) {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize(MouseEvent mouseEvent) {
        isMaximized = !isMaximized;
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.setMaximized(isMaximized);
    }

    @FXML
    private void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.close();
    }

    public void handleReportIssue() throws IOException {
        BugReportDialog reportDialog = new BugReportDialog();
        Optional<String> result = reportDialog.showAndWait();

        if (result.isPresent()) {
            if (!result.get().equals("CANCELED")) {
                //TODO Send ned til dal når det er lavet
                System.out.println(result.get());
            }
        }
    }
}
