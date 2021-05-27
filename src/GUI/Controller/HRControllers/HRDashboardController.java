package GUI.Controller.HRControllers;

import BE.*;
import BLL.LoginManager;
import GUI.Controller.DPT.DepartmentStageController;
import GUI.Controller.DPT.DepartmentViewController;
import GUI.Controller.InfoboardDashboardController;
import GUI.Controller.ManagerControllers.ManagerMessageController;
import GUI.Controller.ManagerControllers.ManagerScreenViewController;
import GUI.Controller.PopupControllers.ConfirmationDialog;
import GUI.Controller.PopupControllers.WarningController;
import GUI.Model.DepartmentModel;
import GUI.Model.ScreenModel;
import GUI.Model.UserModel;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class HRDashboardController implements Initializable {
    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblBar;
    @FXML
    private AnchorPane root;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private ImageView image;
    private User currentUser;
    private boolean isMaximized = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = LoginManager.getCurrentUser();
        ClockCalender.initClock(dateTimeLabel);

        image.setImage(currentUser.getPhotoPath() == null ? new Image("/GUI/Resources/defaultPerson.png") : new Image(currentUser.getPhotoPath()));
        lblWelcome.setText("Welcome " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");
        lblBar.setText("HR Dashboard - " + currentUser.getFirstName() + " " + currentUser.getLastName());
        try {
            handleDeptManagement();
        } catch (IOException e) {
            e.printStackTrace();
            WarningController.createWarning("Oh no! Something went wrong trying to read the Managers create message view." +
                    " Please try again. If the problem persists, please contact an IT-Administrator");

        }
    }

    @FXML
    private void handleOrgDiagramCreator() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/DPT/DepartmentStage.fxml"));
        AnchorPane node = loader.load();
        DepartmentStageController con2 = loader.getController();
        for(Department department : DepartmentModel.getInstance().getSuperDepartment())
        con2.addChildrenNode(department);
        JFXButton b = new JFXButton("Save");
        con2.getChildrenNodes().add(b);

        b.setOnAction((save) -> {
            con2.getDepartmentViewControllers().forEach(vc -> {
                vc.getRemoveIcon().setDisable(true);
                vc.getAllSubDepartments().forEach(item -> {
                    List<User> users = new ArrayList<>(item.getUsers());
                    users.removeIf(u->u.getUserName().isEmpty() || u.getUserRole()!= UserType.Admin);
                    if (!users.isEmpty() && item.getManager() == null) {
                        item.setManager(users.get(0));
                        DepartmentModel.getInstance().addDepartment(item);

                        for (Department dpt : vc.getDepartment().getAllSubDepartments()) {
                            if (dpt.getSubDepartments().contains(item)) {
                                DepartmentModel.getInstance().addSubDepartment(dpt, item);
                                break;
                            }
                        }
                    } else if (item.getManager() == null) {
                        User placeholderUser = new User();
                        placeholderUser.setUserName("admtest");
                        item.setManager(placeholderUser);
                    }
                });
                UserModel.getInstance().updateUserDepartment(vc.getAllSubDepartments());
            });
        });

        borderPane.setCenter(node);
    }

    public void handleDeptManagement() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/GUI/View/HRViews/HRDepartment.fxml"));
        Parent root = fxmlLoader.load();
        HRDepartmentController controller = fxmlLoader.getController();
        borderPane.setCenter(root);
    }

    public void handleShowInfoboard() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/GUI/VIEW/InfoboardDashboard.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Infoboard");
        stage.getIcons().addAll(
                new Image("/GUI/Resources/AppIcons/icon16x16.png"),
                new Image("/GUI/Resources/AppIcons/icon24x24.png"),
                new Image("/GUI/Resources/AppIcons/icon32x32.png"),
                new Image("/GUI/Resources/AppIcons/icon48x48.png"),
                new Image("/GUI/Resources/AppIcons/icon64x64.png"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        InfoboardDashboardController controller = fxmlLoader.getController();

        SceneMover mover = new SceneMover();
        mover.move(stage,controller.getRootBorderPane().getTop());
    }

    public void minimize() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setIconified(true);
    }

    public void maximize(){
        isMaximized = !isMaximized;
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.setMaximized(isMaximized);
    }

    public void exit() {
        System.exit(0);
    }

    public void Logout(ActionEvent actionEvent) throws IOException {
        ConfirmationDialog confirmation = new ConfirmationDialog("Are you sure you want to logout of the application?");

        Optional<Boolean> result = confirmation.showAndWait();

        if (result.isPresent()) {
            if (result.get()) {
                SceneMover sceneMover = new SceneMover();
                // Reset the singleton instance so we don't leave any cache behind.
                UserModel.getInstance().resetSingleton();
                ScreenModel.getInstance().resetSingleton();
                
                Stage root1 = (Stage) root.getScene().getWindow();

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/GUI/VIEW/Login.fxml"));

                Scene scene = new Scene(fxmlLoader.load());
                stage.setTitle("Login");
                stage.getIcons().addAll(
                        new Image("/GUI/Resources/AppIcons/icon16x16.png"),
                        new Image("/GUI/Resources/AppIcons/icon24x24.png"),
                        new Image("/GUI/Resources/AppIcons/icon32x32.png"),
                        new Image("/GUI/Resources/AppIcons/icon48x48.png"),
                        new Image("/GUI/Resources/AppIcons/icon64x64.png"));
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.show();

                BorderPane borderPane = (BorderPane) stage.getScene().getRoot();

                sceneMover.move(stage, borderPane.getTop());

                LoginManager.setCurrentUser(null);
                root1.close();
            }
        }
    }
}
