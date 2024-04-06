package util;

import java.io.IOException;
import java.util.Optional;

import controller.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Helper {
    public void handleLogout(ActionEvent event, Stage primaryStage) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("Logout?");

		Optional<ButtonType> res = alert.showAndWait();
		if (res.isPresent() && res.get() == ButtonType.OK) {
			try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
                Parent root = loader.load();
                LoginController controller = loader.<LoginController>getController();
                Scene scene = new Scene(root);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                // controller.setPrimaryStage(primaryStage);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Error!");
                alert1.setHeaderText(null);
                alert1.setContentText("Cannot access login page");
                alert1.showAndWait();
            }
		}
	}

}
