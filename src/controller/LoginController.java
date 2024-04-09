package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import model.User;
import model.Album;
import model.Photo;
import javafx.scene.image.Image;
import java.util.Calendar;
import model.User;
import util.Helper;

/**
 * The LoginController class handles the login process and initial data setup for the application.
 * It provides functionality to authenticate users, create initial data files if they don't exist,
 * and redirect to the correct dashboard based on the user type (admin or regular user).
 * 
 * @author Isham Khan and Senuri Rupasinghe
 */
public class LoginController {
	@FXML
	private Button loginButton;
	@FXML
	private TextField usernameField;
	ArrayList<User> users;
	User user;

	/**
     * This method initializes the LoginController class. It is called when the login screen is first presented.
     * This method may not be necessary if the stage is passed as a parameter from the main application.
     * 
     * @param stage The primary stage of the application
     */
	public void start(Stage stage) {
		// don't think we need anything here, because we are getting the stage as an input from main photo app
	}
	public void createDataFile(File dataFile) {
		try {
			dataFile.createNewFile();
			Album stockAlbum = new Album("stock");
			File photo;
			 // Specify the folder path
			 File folder = new File("data/stock_images");

			 // List files in the folder
			 File[] files = folder.listFiles();
	 
			 // Iterate through files
			 if (files != null) {
				 for (File file : files) {
					photo = file;
					if (photo != null) {
						Image image = new Image(photo.toURI().toString());
						String name = photo.getName();
						Calendar date = Calendar.getInstance();
						date.setTimeInMillis(photo.lastModified());
						Photo addPhoto = new Photo(name, image, date);
						stockAlbum.getPhotos().add(addPhoto);
					}
				 }

				 User stock = new User("stock");
				 stock.getAlbums().add(stockAlbum);
				 users = new ArrayList<>();
				 users.add(stock);
				 User admin = new User("admin");
				 users.add(admin);
				 Helper.writeUsersToDisk(users);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Folder is empty or does not exist");
		 }
	}

	 /**
     * Handles the login process when the login button is pressed. It checks if the data file exists, creates it if not,
     * reads user data from the file, and authenticates the username entered in the usernameField.
     * If the username is 'admin', it redirects to the admin dashboard. Otherwise, it loads the user dashboard
     * or prompts for user creation if the username does not exist.
     * 
     * @param event The action event generated when the login button is pressed
     */
	public void handleLoginButton(ActionEvent event){
		File dataFile = new File("data/data.dat");
		String username = usernameField.getText(); // extract string form of username
		if (!dataFile.exists() || !dataFile.isFile()) {
			createDataFile(dataFile);
		}

		// file already exists
		try {
			FileInputStream fileInStrm = new FileInputStream("data/data.dat");
			ObjectInputStream objInStrm  = new ObjectInputStream(fileInStrm);
			users = (ArrayList<User>)objInStrm.readObject();
			objInStrm.close();
			fileInStrm.close();
			for (User userPtr: users) {
				if (userPtr.getUsername().equals(username)) {
					user = userPtr;
				}
			}
			if(user != null){
				if (username.equals("admin")){
					// admin user
					redirectToAdmin(event);
				} else {
					// valid user
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserDashboard.fxml"));
					Parent root = loader.load();
					UserController controller = loader.<UserController>getController();
					Scene scene = new Scene(root);
					Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
					controller.Start(user, users);
					stage.setScene(scene);
					stage.show();
				}				
			} else {
				// user DNE
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("User Does Not Exist");
				alert.setHeaderText(null);
				alert.setContentText("User does not exist. Please check spelling or create user through admin.");
				alert.showAndWait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	 /**
     * Redirects to the admin dashboard screen. This method loads the AdminDashboard.fxml file, initializes the scene,
     * and switches to the new scene on the existing stage.
     * 
     * @param event The action event generated when the redirection to the admin dashboard is needed.
     */
	public void redirectToAdmin(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboard.fxml"));
			Parent root = loader.load();
			AdminController controller = loader.<AdminController>getController();
			Scene scene = new Scene(root);
			Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			controller.start(users);
			stage.setScene(scene);
			stage.show();
		}  catch (Exception e) {
			e.printStackTrace();
		}		
		
	}
}
/**
     * Redirects the user to the administration dashboard view upon invocation. 
     * This method is called when an administrator needs to access the admin panel, typically after 
     * successful authentication or when the user requests to switch to the admin view manually.
     * It loads the AdminDashboard.fxml file, sets up the controller, and changes the scene on the 
     * current stage to the admin dashboard view.
     * 
     * @param event The ActionEvent triggered by the user action which calls for redirection to the admin dashboard.
     */
