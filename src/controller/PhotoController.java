package controller;

import java.text.SimpleDateFormat;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Photo;
import model.Tag;
import util.Helper;
import util.PhotoListCell;
import model.Album;
import model.Tag;
import model.User;

public class PhotoController {
	@FXML
    Button backButton, logoutButton, previousButton, nextButton;
    @FXML
    ImageView imageView;
    @FXML
    Label photoNameText, captionText, dateTakenText;
    @FXML
    ListView tagsList;
    private Photo photo;
    private Album album;
    private ArrayList<Tag> tags;
    private User user;
    private ArrayList<User> users;
    private int indexOfPhoto;

    public void Start(Photo photo, Album album, ArrayList<User> users, User user) {
        this.user = user;
        this.users = users;
        this.photo = photo; 
        this.album = album;
        this.tags = photo.getTags();
        tagsList.setItems(FXCollections.observableArrayList(tags));
        this.photoNameText.setText(photo.getName());
        this.captionText.setText(photo.getCaption());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format((photo.getDate()).getTime());
        this.dateTakenText.setText(dateString);
        this.indexOfPhoto = (album.getPhotos()).indexOf(photo);
        this.imageView.setImage(photo.getImage());
    }
    
   
    public void handleBackButton(ActionEvent event) {
        // handle back button -- context of returning to album...
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumDisplay.fxml"));
            Parent root = loader.load();
            PhotoManagerController controller = loader.<PhotoManagerController>getController();
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            controller.Start(user, album, users);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void changePhoto(ActionEvent event, int index) {
        Photo photoSelected = album.getPhotos().get(index);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PhotoView.fxml"));
            Parent root = loader.load();
            PhotoController controller = loader.<PhotoController>getController();
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            controller.Start(photoSelected, album, users, user);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handlePreviousButton(ActionEvent event) {
        if ((indexOfPhoto == 0)) {
            previousButton.setDisable(true);
        } else {
            changePhoto(event, indexOfPhoto-1);
        }
    }

    public void handleNextButton(ActionEvent event) {
        if ((indexOfPhoto == (album.getPhotos().size()-1))) {
            nextButton.setDisable(true);
        } else {
            changePhoto(event, indexOfPhoto+1);
        }
    }

    public void handleLogoutButton(ActionEvent event) {
        // handle logout
        try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
			Parent parent = loader.load();
			LoginController controller = loader.<LoginController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.start(stage);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
    }
}