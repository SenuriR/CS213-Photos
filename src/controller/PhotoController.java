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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Photo;
import model.Album;

public class PhotoController {
	@FXML
    Button backButton, logoutButton, previousButton, nextButton, addTagButton, deleteTagButton;
    @FXML
    ImageView imageView;
    @FXML
    Label photoNameText, captionText, dateTakenText;
    @FXML
    TextField tagTypeField, tagValueField;
    @FXML
    ListView tagsList;
    private Photo photo;
    private Album album;

    public void Start(Photo photo, Album album) {
        this.photo = photo; 
        this.album = album;
    }

    public void handleBackButton(ActionEvent event) {
        // handle back button -- context of returning to album...
    }

    public void handlePreviousButton(ActionEvent event) {
        // handle previous button -- context of previous photo in current album
    }

    public void handleNextButton(ActionEvent event) {
        // handle next button
    }
    
    public void handleAddTagButton(ActionEvent event) {
        // handle add tag button
    }

    public void handleDeleteTagButton(ActionEvent event) {
        // handle delete tag button
    }

    public void handleLogoutButton(ActionEvent event) {
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