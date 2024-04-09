package controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import util.Helper;
import javafx.scene.control.ButtonType;
import java.util.Optional;


/**
 * The AdminController class manages the administration functionalities within the application.
 * It allows an admin user to add, remove, and list users, and provides the option to log out.
 * The admin interface is interacted with via JavaFX controls.
 * 
 * @author Isham Khan and Senuri Rupasinghe
 */

public class AdminController {
	private ArrayList<User> users;
	@FXML
	private TextField usernameField;
	@FXML
	private ListView<User> userListView;
	@FXML
	private Button addUserButton, removeUserButton, showUserButton, logoutButton;

	 /**
     * Initializes the admin controller with a list of users.
     * This method prepares the controller to manage user data.
     * 
     * @param users The list of users to be managed.
     */
	public void start(ArrayList<User> users) {
		this.users = users;
	}

	 /**
     * Handles adding a new user to the application.
     * Checks for non-empty and unique usernames before creating a new user and updating the list view.
     * 
     * @param event The event that triggered the addition of a new user.
     */
	public void handleAddUser(ActionEvent event) {
		
		// get string version of usernameField
		String username = usernameField.getText().trim();
		
		// if no username given as input
		if(username.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Empty Username");
			alert.setHeaderText(null);
			alert.setContentText("Please enter username");
			alert.showAndWait();
			return;
		}
		
		// if username already taken
		if (users != null) {
			for (User user : users) {
				if (user.getUsername().equals(username)){
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Duplicate Username!");
					alert.setHeaderText(null);
					alert.setContentText("Username is taken! Please choose different uesrname.");
					alert.showAndWait();
					return;
				}
			}
		}

		// go ahead and add newUser to userListView
		User newUser = new User(username);
		users.add(newUser);
		Helper.writeUsersToDisk(users);
		usernameField.clear();
		userListView.getItems().add(newUser);		
		Alert alert1 = new Alert(AlertType.INFORMATION);
		alert1.setTitle("Add User");
		alert1.setHeaderText("Add User Confirmation");
		alert1.setContentText("User " + newUser.getUsername() + " succesfully created");
		alert1.showAndWait();
	}

		/**
     * Handles the removal of a selected user from the application.
     * Ensures a user is selected and confirmed for deletion before proceeding.
     * 
     * @param event The event that triggered the removal of a user.
     */

	public void handleRemoveUser(ActionEvent event) {
		User userToDelete = userListView.getSelectionModel().getSelectedItem();

		// make sure a user was chosen
		if (userToDelete == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No User Chosen!");
			alert.setHeaderText(null);
			alert.setContentText("Please choose a user to remove.");
			alert.showAndWait();
			return;
		}
		if (userToDelete.getUsername().toUpperCase().equals("ADMIN")) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Attempt to Delete Admin");
			alert.setHeaderText("Delete Admin");
			alert.setContentText("Cannot delete admin");
			alert.showAndWait();
			return;
		}

		// confirm userToDelete is intended
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Removal of User");
		alert.setHeaderText(null);
		alert.setContentText("Remove user: " + userToDelete.getUsername() + "?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			users.remove(userToDelete);
			Helper.writeUsersToDisk(users);
			userListView.getItems().remove(userToDelete);
		}
	}

	 /**
     * Displays the list of users in the application.
     * If no users exist, it displays an informative message.
     * 
     * @param event The event that triggered the display of user list.
     */
	public void handleShowUsers(ActionEvent event) {
		// show ArrayList<User> users on screen
		if (users != null && !users.isEmpty()) {
			userListView.getItems().clear();
			userListView.getItems().addAll(users);
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No Users");
			alert.setHeaderText(null);
			alert.setContentText("There are no users to display.");
			alert.showAndWait();
		}
	}

	/**
     * Handles the logout process for the admin.
     * Writes the current state of users to disk and redirects to the login screen.
     * 
     * @param event The event that triggered the logout action.
     */
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
			Helper.writeUsersToDisk(users);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}

