package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import model.User;
import model.Album;
import model.Photo;
import javafx.scene.image.Image;
import java.util.Calendar;


public class LoginController {
	@FXML
	private Button loginButton;
	@FXML
	private TextField usernameField;
	@FXML
	private TextField passwordField;
	ArrayList<User> users;
	private Stage primaryStage;

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
				 users.add(stock);
				 try {
					FileOutputStream fileOut = new FileOutputStream("data/data.dat");
					ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
					objOut.writeObject(users);
					objOut.close();
					fileOut.close();
				 } catch (Exception e) {
					e.printStackTrace();
				 }
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
		users = new ArrayList<>();
		User user = new User(username);
		users.add(user);
		try {
			if(username.equals("admin")){
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminDashboard.fxml"));
				Parent root = loader.load();
				AdminController controller = loader.<AdminController>getController();
				Scene scene = new Scene(root);
				Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				controller.start(users);
				stage.setScene(scene);
				stage.show();
			}
			if(username.equals("stock")) {
				// implement stock when more of code framework is put together
			} else {
				// redirect to User's Dashboard
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserDashboard.fxml"));
				Parent root = loader.load();
				UserController controller = loader.<UserController>getController();
				Scene scene = new Scene(root);
				Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				controller.Start(user);
				stage.setScene(scene);
				stage.show();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


}