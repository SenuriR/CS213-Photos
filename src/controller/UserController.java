package controller;

import java.io.IOException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import util.CommonFunctions;
import model.Album;

public class UserController {
	private User user;
	@FXML
	private Label usernameLabel;
	@FXML
	private TextField albumField;
	@FXML
	private ListView<Album> albumsList;
	@FXML
	private Button logOutButton, addAlbumButton, deleteAlbumButton, renameAlbumButton, openAlbumButton, searchPhotosButton, confirmButton, cancelButton;

	public void Start(User user) {
		this.user = user;
		albumsList.setItems(FXCollections.observableArrayList(user.getAlbums()));
		usernameLabel.setText("User Dashboard For - " + user.getUsername().toString().toUpperCase());

	}
	public void handleCancelButton(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("User Dashboard COnfirmation");
		alert.setHeaderText("Cancellation confirmation");
		alert.setContentText("Confirm cancellation?");
		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> res = alert.showAndWait();
		if(res.get().equals(ButtonType.YES)) {
			albumField.clear();
		}
	}
	public void handleConfirmButton(ActionEvent event) {
		// something
	}
	public void handleSearchPhotosButton(ActionEvent event) {
		// something
	}
	public void handleOpenAlbumButton(ActionEvent event) {
		Album albumToView = albumsList.getSelectionModel().getSelectedItem();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumDisplay.fxml"));
			Parent root = loader.load();
			PhotoManagerController controller = loader.<PhotoManagerController>getController();
			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.Start(user, albumToView);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	public void handleRenameAlbumButton(ActionEvent event) {
		// something
		((Album) albumsList.getSelectionModel().getSelectedItems()).setName(albumField.getText());
		albumField.clear();
	}
	public void handleDeleteAlbumButton(ActionEvent event) {
		// delete albumToDelete from Album arraylist
		Album album = albumsList.getSelectionModel().getSelectedItem();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("User Dashboard Confirmation");
		alert.setHeaderText("Album deletion confirmation.");
		alert.setContentText(("Delete album: " + album.getName() + "\"?"));
		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> res = alert.showAndWait();
		if (res.get().equals(ButtonType.YES)) {
			user.getAlbums().remove(album);
			albumsList.getItems().remove(album);
			// save data
			Alert alert1 = new Alert(AlertType.INFORMATION);
			alert1.setTitle("User Dashboard Confirmation");
			alert1.setHeaderText("Album deleted");
			alert1.setContentText("Album " + album.getName() + " was deleted");
		}
	}
	
	public void handleAddAlbumButton(ActionEvent event) {
		// create an album instance, add to album arraylist
		// probably redirect to AlbumDashboard
	}
	public void handleLogOutButton(ActionEvent event) {
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