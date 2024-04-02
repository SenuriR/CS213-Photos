package controller;

import java.text.SimpleDateFormat;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import util.CommonFunctions;

/**
 * The Photo Controller allows user to view the enlarged photo helps add/remove
 * tags and displays details relevant to photo.
 * 
 *
 */
public class PhotoController {
	ArrayList<User> users;
	ListView<Photo> photos;
	@FXML
	private ImageView imageView;
	@FXML
	private Label photoNameText, captionText, dateTakenText;
	@FXML
	private Button nextButton, previousButton, backButton, cancelButton, confirmButton, addTagButton, deleteTagButton,
			LogoutButton;
	@FXML
	private TextField tagTypeField, tagValueField;
	@FXML
	private ListView<Tag> tags;
	private Album selectedAlbum;
	private User user;
	SimpleDateFormat dateTimeformat = new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm a");

	/**
	 * On Start the list of users, list of photos, current user and selected album
	 * is initialized. Also information related to photo is set to the fields.
	 * 
	 * @param users
	 *            takes the list of users
	 * @param photos
	 *            takes the list of photos
	 * @param user
	 *            current user
	 * @param selectedAlbum
	 *            current album
	 */
	public void start(ArrayList<User> users, ListView<Photo> photos, User user, Album selectedAlbum) {
		this.users = users;
		this.photos = photos;
		this.user = user;
		this.selectedAlbum = selectedAlbum;
		Photo selectedPhoto = photos.getSelectionModel().getSelectedItem();
		imageView.setImage(selectedPhoto.getImage());
		photoNameText.setText(selectedPhoto.getName());
		captionText.setText(selectedPhoto.getCaption());
		dateTakenText.setText(dateTimeformat.format(selectedPhoto.getDate().getTime()));
		tags.setItems(FXCollections.observableArrayList(photos.getSelectionModel().getSelectedItem().getTags()));
		tags.getSelectionModel().select(0);
		disableInput(true);
	}

	/**
	 * Handles the switch photo buttons to change the photos.
	 * 
	 * @param event
	 */
	public void handleSwitchButton(ActionEvent event) {
		int currentIndex = photos.getSelectionModel().getSelectedIndex();

		if (((Button) event.getSource()).equals(nextButton)) {
			if (currentIndex < photos.getItems().size())
				currentIndex++;
			else
				currentIndex = 0;
		} else {
			if (currentIndex > 0)
				currentIndex--;
			else
				currentIndex = photos.getItems().size() - 1;
		}

		photos.getSelectionModel().select(currentIndex);
		Photo selectedPhoto = photos.getSelectionModel().getSelectedItem();
		imageView.setImage(selectedPhoto.getImage());
		photoNameText.setText(selectedPhoto.getName());
		captionText.setText(selectedPhoto.getCaption());
		dateTakenText.setText(dateTimeformat.format(selectedPhoto.getDate().getTime()));
	}

	/**
	 * Handles add tag button and enables to text fields and buttons.
	 */
	public void handleAddTagButton() {
		disableInput(false);
	}

	/**
	 * Handles the delete button and deletes the selected tag. Saves the users data.
	 * 
	 * @param event
	 */
	public void handleDeleteTagButton(ActionEvent event) {
		photos.getSelectionModel().getSelectedItem().getTags().remove(tags.getSelectionModel().getSelectedItem());
		CommonFunctions.saveData(users);
		tags.getItems().remove(tags.getSelectionModel().getSelectedItem());
		tags.refresh();
		tags.getSelectionModel().select(0);
	}

	/**
	 * Handles the confirm button and after checking if the tag doesn't exist, it
	 * adds the new tag and saves the users data.
	 */
	public void handleConfirmButton() {
		ArrayList<Tag> tagList = photos.getSelectionModel().getSelectedItem().getTags();
		Tag newTag = new Tag(tagTypeField.getText(), tagValueField.getText());
		tagTypeField.clear();
		tagValueField.clear();

		for (Tag currentTag : tagList) {
			if (currentTag.equals(newTag)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Photo View Error");
				alert.setHeaderText("Tag Add Error.");
				alert.setContentText("A tag with this name and value already exists.");

				alert.showAndWait();
				return;
			}
		}

		disableInput(true);
		tagList.add(newTag);
		CommonFunctions.saveData(users);
		tags.getItems().add(newTag);
		tags.refresh();
		tags.getSelectionModel().select(0);
	}

	/**
	 * Handles the cancel button, confirms with user if user wants to cancel the
	 * action and clears the fields and dissables the buttons.
	 * 
	 * @param event
	 *            takes the mouse click event.
	 */
	public void handleCancelTagButton(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Photo View Confirmation");
		alert.setHeaderText("Cancellation confirmation.");
		alert.setContentText("Are you sure you want to cancel this action?");

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get().equals(ButtonType.YES)) {
			tagTypeField.clear();
			tagValueField.clear();
			this.disableInput(true);
		}

	}

	/**
	 * Handles the back button and takes the current user to display album scene.
	 * 
	 * @param event
	 *            takes the mouse click event.
	 */
	public void handleBackButton(ActionEvent event) {
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
	 * Common functionality to switch on and off the button depending on the
	 * specific situations.
	 * 
	 * @param value
	 *            takes the boolean value.
	 */
	protected void disableInput(boolean value) {
		confirmButton.setDisable(value);
		cancelButton.setDisable(value);
		tagTypeField.setDisable(value);
		tagValueField.setDisable(value);
		addTagButton.setDisable(!value);
		deleteTagButton.setDisable(!value);
		nextButton.setDisable(!value);
		previousButton.setDisable(!value);
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
}
