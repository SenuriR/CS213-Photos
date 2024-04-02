package controller;

import java.util.ArrayList;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Album;
import model.User;
import util.CommonFunctions;

/**
 * The User Controller allows user to manipulate the albums and view albums as a
 * list. Also give functionality to rename albums and open search scene.
 * 
 *
 */
public class UserController {
	@FXML
	private Button openAlbumButton, addAlbumButton, deleteAlbumButton, renameAlbumButton, searchPhotosButton,
			cancelButton, confirmButton, logOutButton, actionButton;
	@FXML
	private TextField albumField;
	@FXML
	private ListView<Album> albums;
	private ArrayList<User> users;
	private User user;

	@FXML
	private Label Username;

	public boolean rename = false;
	public String albumname;

	/**
	 * On Start the user-list and current user is initialized. And all the albums
	 * for the current user are set into list view. As well as Username Label is
	 * modified for current user.
	 * 
	 * @param user
	 *            takes the current user.
	 * @param users
	 *            takes the user list.
	 */
	public void start(User user, ArrayList<User> users) {
		this.disableInput(true);
		this.user = user;
		this.users = users;
		albums.setItems(FXCollections.observableArrayList(user.getAlbums()));
		albums.getSelectionModel().select(0);
		Username.setText("User Dashboard For - " + user.getUsername().toString().toUpperCase());
	}

	/**
	 * Common functionality to switch on and off the button depending on the
	 * specific situations.
	 * 
	 * @param value
	 *            takes the boolean value.
	 */
	protected void disableInput(boolean value) {
		albumField.setDisable(value);
		confirmButton.setDisable(value);
		cancelButton.setDisable(value);
		openAlbumButton.setDisable(!value);
		addAlbumButton.setDisable(!value);
		deleteAlbumButton.setDisable(!value);
		renameAlbumButton.setDisable(!value);
		searchPhotosButton.setDisable(!value);
	}

	/**
	 * At any given point the user can logout and it will take user to the main
	 * login screen.
	 * 
	 * @param event
	 *            takes the mouse event.
	 */
	public void handleLogOutButton(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
			Parent parent = (Parent) loader.load();
			@SuppressWarnings("unused")
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

	/**
	 * Handles the cancel button and confirms with the user to cancel the action.
	 */
	public void handleCancelButton() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("User Dashboard Confirmation");
		alert.setHeaderText("Cancellation confirmation.");
		alert.setContentText("Are you sure you want to cancel this action?");

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get().equals(ButtonType.YES)) {
			this.disableInput(true);
			albumField.clear();
		}
	}

	/**
	 * Handles the confirm button and for rename case it will rename the album else
	 * it will create a new album and add to the album list for user. Then save the
	 * data using common functionality.
	 */
	public void handleConfirmButton() {
		ObservableList<Album> albumList = albums.getItems();

		for (Album currentAlbum : albumList) {
			if (currentAlbum.getName().equals(albumField.getText())) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("User Dashboard Error");
				alert.setHeaderText(rename ? "Error renaming album." : "Error adding album.");
				alert.setContentText("An album with this name already exists.");

				alert.showAndWait();
				return;
			}
		}

		if (rename) {
			albums.getSelectionModel().getSelectedItem().setName(albumField.getText());
			albums.refresh();
			CommonFunctions.saveData(users);
			albumField.clear();
			this.disableInput(true);
			rename = false;
		} else {
			Album newAlbum = new Album(albumField.getText());
			user.getAlbums().add(newAlbum);
			albums.getItems().add(newAlbum);
			albums.getSelectionModel().select(newAlbum);
			albums.refresh();
			CommonFunctions.saveData(users);
			albumField.clear();
			this.disableInput(true);
		}
	}

	/**
	 * Handles add button and enables the text field to fill album name.
	 */
	public void handleAddAlbumButton() {
		disableInput(false);
	}

	/**
	 * Handles the Open album button and takes the selected album and passes it to
	 * Album display after loading the Album Display scene.
	 * 
	 * @param event
	 */
	public void handleOpenAlbumButton(ActionEvent event) {
		Album selectedAlbum = albums.getSelectionModel().getSelectedItem();

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumDisplay.fxml"));
			Parent parent = (Parent) loader.load();
			AlbumController controller = loader.<AlbumController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.start(users, user, selectedAlbum);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Handles the delete button and deletes the selected album after confirming
	 * from user. Then saves the data.
	 */
	public void handleDeleteAlbumButton() {
		Album album = albums.getSelectionModel().getSelectedItem();

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("User Dashboard Confirmation");
		alert.setHeaderText("Album deletion confirmation.");
		alert.setContentText("Are you sure you want to delete \"" + album.getName() + "\"?");
		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get().equals(ButtonType.YES)) {
			user.getAlbums().remove(album);
			albums.getItems().remove(album);
			albums.refresh();
			CommonFunctions.saveData(users);
			Alert done = new Alert(AlertType.INFORMATION);
			done.setTitle("User Dashboard Confirmation");
			done.setHeaderText("Album deletion confirmation.");
			done.setContentText("\"" + album.getName() + "\" was Deleted");
			CommonFunctions.saveData(users);
		}

	}

	/**
	 * Handles the rename button and loads the current name of the album in the text
	 * field for user to modify.
	 */
	public void handleRenameButton() {
		Album currentAlbum = albums.getSelectionModel().getSelectedItem();
		this.disableInput(false);
		albumField.setText(currentAlbum.getName());
		albumname = currentAlbum.getName();
		rename = true;

	}

	/**
	 * Handles the search button and loads the search scene for the user.
	 * 
	 * @param event
	 */
	public void handlesearchPhotosButton(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchPhotos.fxml"));
			Parent parent = (Parent) loader.load();
			PhotoSearchController controller = loader.<PhotoSearchController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.start(user, users);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}
}
