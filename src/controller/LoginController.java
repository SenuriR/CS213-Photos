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


public class LoginController {
	@FXML
	private Button loginButton;
	@FXML
	private TextField usernameField;
	ArrayList<User> users;
	User user;

	public void start(Stage stage) {
		// don't think we need anything here, because we are getting the stage as an input from main photo app
	}
	public void createDataFile(File dataFile) {
		try {
			dataFile.createNewFile();
			Album stockAlbum = new Album("stock");
			File photo;
			 // Specify the folder path
			 File folder = new File("data");

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
				alert.setContentText("User does not exist. Please create a user through admin.");
				alert.showAndWait();
				redirectToAdmin(event);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

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
