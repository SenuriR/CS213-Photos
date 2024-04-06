package controller;

import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import model.Album;

public class AlbumController {
	@FXML
	private Label albumName;
	@FXML
	private Button logout, addPhoto, deletePhoto, editCaption, removeTag;
	@FXML
	private TextField captionField, tagField;
	@FXML
	private ListView photoList;

	public void Start(User user, Album album) {

	}
	
	public void removeSelectedTag(ActionEvent event) {
		// something
	}

	public void addTag(ActionEvent event) {
		// something	
	}

	public void editCaption(ActionEvent event) {
		// something
	}

	public void deletePhoto(ActionEvent event) {
		// something
	}

	public void addPhoto(ActionEvent event) {
		// something
	}

	public void handleBack(ActionEvent event) {
		// something
	}

	public void handleLogout(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
			Parent parent = loader.load();
			LoginController controller = loader.<LoginController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			// controller.start();
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}