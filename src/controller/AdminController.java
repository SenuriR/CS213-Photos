package controller;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import model.User;
import javafx.scene.Node;

/**
 * The Admin Controller manipulates the user list and feeds new updates into the
 * view.
 * 
 * @author Ayush Joshi
 * @author Anil Tilve
 *
 */
public class AdminController {
	@FXML
	private Button actionTypeButton, logOutButton, cancelButton, confirmButton, createUserButton, deleteUserButton,
			listUsersButton;
	@FXML
	private TextField userField;
	@FXML
	private ListView<User> users;

	/**
	 * On start the users are initialized.
	 * 
	 * @param users
	 *            is the list of users that are in the system.
	 * 
	 */
	public void start(ArrayList<User> users) {
		this.users.setItems(FXCollections.observableArrayList(users));
		this.users.getSelectionModel().select(0);
		this.users.setVisible(false);
		disableInput(true);
	}

	/**
	 * Confirms the cancellation of the action when trying to add a new user.
	 */
	public void handleCancelButton() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Admin Dashboard Confirmation");
		alert.setHeaderText("Cancellation confirmation.");
		alert.setContentText("Are you sure you want to cancel this action?");

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get().equals(ButtonType.YES)) {
			userField.clear();
			disableInput(true);
		}
	}

	/**
	 * On confirm the new user is checked into the list of users and if exist an
	 * error is thrown otherwise the user is added to users list. Save the data
	 */
	public void handleConfirmButton() {
		User newUser = new User(userField.getText());
		ObservableList<User> userList = users.getItems();

		for (User currentUser : userList) {
			if (currentUser.getUsername().equals(newUser.getUsername())) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Admin Dashboard Error");
				alert.setHeaderText("User add error.");
				alert.setContentText("This user already exists.");

				alert.showAndWait();
				return;
			}
		}

		users.getItems().add(newUser);
		users.refresh();
		saveData();
		userField.clear();
		disableInput(true);
	}

	/**
	 * Confirms if the user wants to delete a user and then remove the user from the
	 * users list.
	 */
	public void handleDeleteUserButton() {
		User user = users.getSelectionModel().getSelectedItem();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Admin Dashboard Confirmation");
		alert.setHeaderText("User deletion confirmation.");
		alert.setContentText("Are you sure you want to delete \"" + user.getUsername() + "\"'s account?");

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get().equals(ButtonType.YES)) {
			users.getItems().remove(user);
			users.refresh();
			saveData();
		}
	}

	/**
	 * Calls the disableInput function to enable the field to let user input the
	 * username.
	 */
	public void handleAddButton() {
		disableInput(false);
	}

	/**
	 * Makes the users visible on click.
	 */
	public void handleListUsersButton() {
		users.setVisible(true);
		users.refresh();
	}

	/**
	 * Common function to enable and disable the field views as well as buttons
	 * depending on the situations.
	 * 
	 * @param value
	 *            : takes the boolean value to turn on or off the fields and
	 *            buttons.
	 */
	protected void disableInput(boolean value) {
		userField.setDisable(value);
		confirmButton.setDisable(value);
		cancelButton.setDisable(value);
		createUserButton.setDisable(!value);
		deleteUserButton.setDisable(!value);
		listUsersButton.setDisable(!value);
	}

	/**
	 * Common functionality to save the users data by converting a listview to
	 * arraylist and writing to data.dat file.
	 */
	private void saveData() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("data/data.dat");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

			objectOutputStream.writeObject(new ArrayList<>(Arrays.asList(users.getItems().toArray())));
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * On event, loads the login screen.
	 * 
	 * @param event
	 *            takes the mouse click event
	 */
	public void handleLogoutButton(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
			Parent parent = (Parent) loader.load();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}
}