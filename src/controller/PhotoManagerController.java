package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import model.Album;

public class PhotoManagerController {
    @FXML
    Label albumName;
    @FXML
    Button logout, addPhoto, deletePhoto, editCaption, addTag, removeTag;
    @FXML
    TextField captionField, tagField;
    @FXML
    ListView photoList;

    public void Start(User user, Album album) {
        // handle start
    }

    public void removeSelectedTag(ActionEvent event) {
        // handle remove selected tag
    }

    public void addTag(ActionEvent event) {
        // handle add tag
    }

    public void editCaption(ActionEvent event) {
        // handle edit caption
    }
    
    public void deletePhoto(ActionEvent event) {
        // handle delte photo
    }
    
    public void addPhoto(ActionEvent event) {
        // handle add photo
    }

    public void handleLogout(ActionEvent event) {
        // handle logout
        try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
			Parent parent = loader.load();
			LoginController controller = loader.<LoginController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.start(stage);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
    }
}
