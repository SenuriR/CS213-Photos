package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import util.CommonFunctions;
import util.PhotoCell;

/**
 * The Album Controller manipulates the photo list and feeds new updates into
 * the list viewer. There are different functionalities that are implemented for
 * an album on specific photos.
 * 
 * @author Ayush Joshi
 * @author Anil Tilve
 *
 */

public class AlbumController {
	@FXML
	private Button addPhotoButton, deletePhotoButton, moveToAlbumButton, copyToAlbumButton, recaptionButton,
			cancelButton, confirmButton, buttonType, LogoutButton;
	@FXML
	private TextField captionField;
	@FXML
	private Label albumNameLabel, captionLabelFXML, AlbumName;
	@FXML
	private ChoiceBox<String> albumNameField;
	@FXML
	private ListView<Photo> photos;
	private ArrayList<User> users;
	private User user;
	private Album selectedAlbum;

	/**
	 * On start the users, current user and selected albums are initialized. as well
	 * as all the photos from the selected album are set on the list viewer.
	 * 
	 * @param users
	 *            takes the users list.
	 * @param user
	 *            takes current logged in user.
	 * @param selectedAlbum
	 *            takes the selected album from the list of albums.
	 */
	public void start(ArrayList<User> users, User user, Album selectedAlbum) {
		this.users = users;
		this.user = user;
		this.selectedAlbum = selectedAlbum;
		AlbumName.setText(selectedAlbum.getName());

		photos.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
			@Override
			public ListCell<Photo> call(ListView<Photo> photoList) {
				return new PhotoCell();
			}
		});

		photos.setItems(FXCollections.observableArrayList(selectedAlbum.getPhotos()));
		photos.getSelectionModel().select(0);
		disableInput(true);

		ArrayList<String> albumnames = new ArrayList<String>();
		albumnames.add(0, " ");
		ArrayList<Album> allalbums = user.getAlbums();
		for (Album curralbum : allalbums) {
			albumnames.add(curralbum.getName());
		}

		albumNameField.setItems(FXCollections.observableArrayList(albumnames));
		albumNameField.setValue(" ");

		albumNameField.setDisable(true);
		albumNameLabel.setDisable(true);
		captionLabelFXML.setDisable(true);
		captionField.setDisable(true);
	}

	/**
	 * Handles the Add photo functionality, by loading the photo from the file
	 * chooser by calling photo model and storing the photo in album after checking
	 * the photo is new, as well as updating the photo list viewer. And saves the
	 * data using common function.
	 */
	public void handleAddPhotoButton() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose Image");
		chooser.getExtensionFilters().addAll(
				new ExtensionFilter("Image Files", "*.bmp", "*.BMP", "*.gif", "*.GIF", "*.jpg", "*.JPG", "*.png",
						"*.PNG"),
				new ExtensionFilter("Bitmap Files", "*.bmp", "*.BMP"),
				new ExtensionFilter("GIF Files", "*.gif", "*.GIF"), new ExtensionFilter("JPEG Files", "*.jpg", "*.JPG"),
				new ExtensionFilter("PNG Files", "*.png", "*.PNG"));
		File selectedFile = chooser.showOpenDialog(null);

		if (selectedFile != null) {
			Image image = new Image(selectedFile.toURI().toString());
			String name = selectedFile.getName();
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(selectedFile.lastModified());
			Photo newPhoto = new Photo(name, image, date);

			for (Photo currentPhoto : selectedAlbum.getPhotos()) {
				if (currentPhoto.equals(newPhoto)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Album Display Error");
					alert.setHeaderText("Photo Add Error.");
					alert.setContentText("A photo with this name already exists.");

					alert.showAndWait();
					return;
				}
			}

			photos.getItems().add(newPhoto);
			selectedAlbum.getPhotos().add(newPhoto);
			CommonFunctions.saveData(users);
		}
	}

	/**
	 * Handles the copy and move button events, depending on specific button a
	 * specific textfield is enabled as well as confirm button text is changed.
	 * 
	 * @param event
	 *            takes the mouse event.
	 */
	public void handleAlterPhotoButton(ActionEvent event) {
		buttonType = (Button) event.getSource();
		disableInput(false);

		if (buttonType.equals(recaptionButton)) {
			captionLabelFXML.setDisable(false);
			captionField.setDisable(false);
			captionField.setText(photos.getSelectionModel().getSelectedItem().getCaption());
			confirmButton.setText("Confirm Caption");
		} else if (buttonType.equals(moveToAlbumButton)) {
			albumNameLabel.setDisable(false);
			albumNameField.setDisable(false);
			confirmButton.setText("Confirm Move");
		} else if (buttonType.equals(copyToAlbumButton)) {
			albumNameLabel.setDisable(false);
			albumNameField.setDisable(false);
			confirmButton.setText("Confirm Copy");
		}

	}

	/**
	 * Handles the Cancel event, and confirms with user if user wants to cancel the
	 * action. Sets the specific confirm button to normal confirm button.
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
			confirmButton.setText("Confirm");

			if (buttonType.equals(recaptionButton)) {
				captionLabelFXML.setDisable(true);
				captionField.setDisable(true);
				captionField.clear();
			} else {
				albumNameLabel.setDisable(true);
				albumNameField.setDisable(true);
				albumNameField.setValue(" ");
				;
			}
		}
	}

	/**
	 * Handles the confirm button cases. Depending on specific case, it either
	 * re-caption the photo, or moves the photo or copies the photo. Also checks for
	 * the error cases if on copy or move duplicate photos will exist. And saves the
	 * data using common function.
	 */
	public void handleConfirmButton() {
		if (buttonType.equals(recaptionButton)) {
			photos.getSelectionModel().getSelectedItem().setCaption(captionField.getText());
			photos.refresh();
			captionLabelFXML.setDisable(true);
			captionField.setDisable(true);
			confirmButton.setText("Confirm");
			captionField.clear();
		} else {
			String destAlbum = albumNameField.getSelectionModel().getSelectedItem();
			Photo toBeMoved = photos.getSelectionModel().getSelectedItem();

			for (Album currentAlbum : user.getAlbums()) {
				if (currentAlbum.getName().equals(destAlbum)) {
					for (Photo currentPhoto : currentAlbum.getPhotos()) {
						if (currentPhoto.equals(toBeMoved)) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Album Display Error");
							alert.setHeaderText("Error Moving Photo.");
							alert.setContentText("A photo with this name already exists in the destination album.");

							alert.showAndWait();
							return;
						}
					}
					currentAlbum.getPhotos().add(toBeMoved);
					if (buttonType.equals(moveToAlbumButton)) {
						photos.getSelectionModel().select(0);
						handleDeletePhotoButton();
					}
					albumNameLabel.setDisable(true);
					albumNameField.setDisable(true);
					albumNameField.setValue(" ");
					disableInput(true);
					CommonFunctions.saveData(users);
					return;
				}
			}
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Album Display Error");
			alert.setHeaderText("Error Moving Photo.");
			alert.setContentText("An album with the name \"" + destAlbum + "\" does not exist.");

			alert.showAndWait();
			return;
		}
		disableInput(true);
		CommonFunctions.saveData(users);
	}

	/**
	 * Handles the delete button and removes the selected photo from the album.
	 */
	public void handleDeletePhotoButton() {
		selectedAlbum.getPhotos().remove(photos.getSelectionModel().getSelectedItem());
		photos.getItems().remove(photos.getSelectionModel().getSelectedItem());
		photos.refresh();
		photos.getSelectionModel().select(0);
		CommonFunctions.saveData(users);
	}

	/**
	 * Handles an event to open the selected photo by calling the loader to open
	 * photo viewer and setting a new stage.
	 * 
	 * @param event
	 *            takes the mouse event
	 */
	public void handleOpenPhotoButton(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoView.fxml"));
			Parent parent = (Parent) loader.load();
			PhotoController controller = loader.<PhotoController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.start(users, photos, user, selectedAlbum);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Handles the back button and takes back user to the UserDashboard.
	 * 
	 * @param event
	 *            takes the mouse event.
	 */
	public void handleBackButton(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserDashboard.fxml"));
			Parent parent = (Parent) loader.load();
			UserController controller = loader.<UserController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.start(user, users);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * At any given point the user can logout and it will take user to the main
	 * login screen.
	 * 
	 * @param event
	 *            takes the mouse event.
	 */
	public void handleLogoutButton(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
			Parent parent = (Parent) loader.load();
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
	 * Common functionality to switch on and off the button depending on the
	 * specific situations.
	 * 
	 * @param value
	 *            takes the boolean value.
	 */
	protected void disableInput(boolean value) {
		confirmButton.setDisable(value);
		cancelButton.setDisable(value);
		addPhotoButton.setDisable(!value);
		deletePhotoButton.setDisable(!value);
		copyToAlbumButton.setDisable(!value);
		moveToAlbumButton.setDisable(!value);
		copyToAlbumButton.setDisable(!value);
	}
}
