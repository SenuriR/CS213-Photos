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


/**
 * The PhotoController class manages the interactions within the photo view in the application.
 * It allows the user to navigate between photos, see photo information
 * and log out or return to the album display.
 * 
 * @author Isham Khan and Senuri Rupasinghe
 */
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

     /**
     * Starts the photo display view with data of the selected photo, album, and user information.
     * It sets up the UI elements with the photo's details, tags, and initializes navigation functionality.
     *
     * @param photo  The photo to be displayed.
     * @param album  The album that contains the photo.
     * @param users  The list of all users.
     * @param user   The current user viewing the photo.
     */
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
    
   	/**
     * Handles the action of the back button which returns the user to the album display.
     * This method loads the AlbumDisplay view and passes along the necessary data.
     *
     * @param event The action event triggered by pressing the back button.
     */
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


    /**
     * Changes the displayed photo in the view based on the index provided.
     * The photo at the specified index from the album's photo list is displayed.
     *
     * @param event The action event triggered by navigation buttons.
     * @param index The index of the photo to display in the album's photo list.
     */
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

    	/**
     * Handles the action of the previous button to navigate to the previous photo in the album.
     * If the first photo is being displayed, this button will be disabled.
     *
     * @param event The action event triggered by pressing the previous button.
     */
    public void handlePreviousButton(ActionEvent event) {
        if ((indexOfPhoto == 0)) {
            previousButton.setDisable(true);
        } else {
            changePhoto(event, indexOfPhoto-1);
        }
    }

    /**
     * Handles the action of the next button to navigate to the next photo in the album.
     * If the last photo is being displayed, this button will be disabled.
     *
     * @param event The action event triggered by pressing the next button.
     */
    public void handleNextButton(ActionEvent event) {
        if ((indexOfPhoto == (album.getPhotos().size()-1))) {
            nextButton.setDisable(true);
        } else {
            changePhoto(event, indexOfPhoto+1);
        }
    }

    /**
     * Handles the logout action which returns the user to the login screen.
     * Current user state is saved before logging out.
     *
     * @param event The action event triggered by pressing the logout button.
     */
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
