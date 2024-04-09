package controller;

import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.User;
import util.Helper;
import model.Album;


/**
 * The UserController class shows user specific albums.
 * It allows the user to view albums and perform edit album operations
 * 
 * @author Isham Khan and Senuri Rupasinghe
 */
public class UserController {
	private User user;
	private ArrayList<Album> userAlbums;
	private ArrayList<User> users;
	@FXML
	private Label usernameLabel;
	@FXML
	private TextField albumField;
	@FXML
	private ListView<Album> albumsList;
	@FXML
	private Button logOutButton, addAlbumButton, deleteAlbumButton, renameAlbumButton, openAlbumButton, searchPhotosButton;

	public void Start(User user, ArrayList<User> users) {
		this.user = user;
		this.users = users;
		this.userAlbums = user.getAlbums();
		albumsList.setItems(FXCollections.observableArrayList(userAlbums));
		usernameLabel.setText("User Dashboard For - " + user.getUsername().toString().toUpperCase());
	}

	public void handleSearchPhotosButton(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchPhotos.fxml"));
			Parent root = loader.load();
			PhotoSearchController controller = loader.<PhotoSearchController>getController();
			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.Start(users, user);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void handleOpenAlbumButton(ActionEvent event) { // WORK ON THIS PART NOW
		Album albumToView = albumsList.getSelectionModel().getSelectedItem();
		if (albumToView == null) {
			Alert alert0 = new Alert(AlertType.ERROR);
			alert0.setTitle("User Dashboard Error");
			alert0.setHeaderText("No Album Selected");
			alert0.setContentText("Please select an album to open.");
			alert0.showAndWait();
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumDisplay.fxml"));
			Parent root = loader.load();
			PhotoManagerController controller = loader.<PhotoManagerController>getController();
			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.Start(user, albumToView, users);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean albumAlreadyExists(Album newAlbum){
        for (Album a : user.getAlbums()) {
            if (a.equals(newAlbum)) {
                return true;
            }
        }
        return false;
    }

	public void handleRenameAlbumButton(ActionEvent event) {
		Album albumToEdit = albumsList.getSelectionModel().getSelectedItem();
		if (albumToEdit == null) {
			Alert alert0 = new Alert(AlertType.ERROR);
			alert0.setTitle("User Dashboard Error");
			alert0.setHeaderText("No Album Selected");
			alert0.setContentText("Please select an album to rename.");
			alert0.showAndWait();
			return;
		}
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("User Dashboard");
		dialog.setHeaderText("Rename Album ");
		dialog.setContentText("Please enter new album name:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String albumName = result.get();
			Album newAlbum = new Album(albumName);
			if(albumAlreadyExists(newAlbum)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Create Album Error");
				alert.setHeaderText("Album Name Taken");
				alert.setContentText("Entered album name already exists. Please add a different album name.");
				alert.showAndWait();
				return;
			} else {
				albumToEdit.setName(albumName);
				Alert alert1 = new Alert(AlertType.INFORMATION);
				alert1.setTitle("User Dashboard Confirmation");
				alert1.setHeaderText("Album Renamed");
				alert1.setContentText("Album has been successfully");
				alert1.showAndWait();
			}
			Helper.writeUsersToDisk(users);
			Start(user, users);
			// Proceed with using the new album name
		} else {
			// User clicked Cancel, handle this scenario accordingly
			return;
		}
	}
	public void handleDeleteAlbumButton(ActionEvent event) {
		// delete albumToDelete from Album arraylist
		Album album = albumsList.getSelectionModel().getSelectedItem();
		if (album == null) {
			Alert alert0 = new Alert(AlertType.ERROR);
			alert0.setTitle("User Dashboard Error");
			alert0.setHeaderText("No Album Selected");
			alert0.setContentText("Please select an album to delete.");
			alert0.showAndWait();
			return;
		}
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
			alert1.showAndWait();
		}
		Helper.writeUsersToDisk(users);
	}

	public void handleAddAlbumButton(ActionEvent event) {
		// create an album instance, add to album arraylist
		// probably redirect to AlbumDashboard
		if (albumField.getText().trim().isEmpty()) {
			Alert alert0 = new Alert(AlertType.ERROR);
			alert0.setTitle("User Dashboard Error");
			alert0.setHeaderText("No Album Name");
			alert0.setContentText("Please type an album name.");
			alert0.showAndWait();
			return;
		}
		Album albumToAdd = new Album(albumField.getText());
		if(albumAlreadyExists(albumToAdd)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Create Album Error");
			alert.setHeaderText("Album Name Taken");
			alert.setContentText("Entered album name already exists. Please add a different album name.");
			alert.showAndWait();
			return;
		} else {
			userAlbums.add(albumToAdd);
			Helper.writeUsersToDisk(users);
			Alert alert1 = new Alert(AlertType.INFORMATION);
			alert1.setTitle("Create Album Confirmation");
			alert1.setHeaderText("Album Created");
			alert1.setContentText("Album " + albumToAdd.getName() + " was created");
			alert1.showAndWait();
			albumsList.setItems(FXCollections.observableArrayList(userAlbums));
		}

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
			Helper.writeUsersToDisk(users);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}