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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Tooltip;
import model.User;
import util.Helper;
import model.Album;
import model.Photo;
import model.Tag;
import util.PhotoListCell;

// controller for AlbumDisplay.fxml
public class PhotoManagerController {
    @FXML
    Label albumName;
    @FXML
    Button logout, addPhoto, deletePhoto, editCaption, addTag, removeTag;
    @FXML
    TextField tagField, captionField;
    @FXML
    ListView photoList;
    private User user;
    private ArrayList<User> users;
    private ArrayList<Photo> photos;
    private Album album;
    private ArrayList<Tag> tags;
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void Start(User user, Album album, ArrayList<User> users) {
        // handle start
        this.user = user;
        this.users = users;
        this.album = album;
        this.photos = album.getPhotos();
        this.albumName.setText(album.getName());
        photoList.setItems(FXCollections.observableArrayList(photos));
        photoList.setCellFactory(param -> new PhotoListCell());
    }

    public void removeSelectedTag(ActionEvent event) { // I don't think this is necessary in the album view
        String tagName;
        String tagValue;
        String[] info = tagField.getText().split(":");
        tagName = info[0];
        tagValue = info[1];
        Photo photo = (Photo) photoList.getSelectionModel().getSelectedItem();
        this.tags = photo.getTags();
        Tag tagToRemove = new Tag(tagName, tagValue);
        for (Tag tag : tags) {
            if (tag.equals(tagToRemove)) {
                tags.remove(tag);
            }
        }
        Helper.writeUsersToDisk(users);
        photoList.refresh();
    }

    public void addTag(ActionEvent event) { // I don't think this is necessary as well - the editing of tags should be excluded to the individual photo view only
        String tagName;
        String tagValue;
        String[] info = tagField.getText().split(":");
        tagName = info[0];
        tagValue = info[1];
        Photo photo = (Photo) photoList.getSelectionModel().getSelectedItem();
        this.tags = photo.getTags();
        Tag tagToAdd = new Tag(tagName, tagValue);
        tags.add(tagToAdd);
        Helper.writeUsersToDisk(users);
        photoList.refresh();
    }

    public void editCaption(ActionEvent event) {
        // handle edit caption
        String caption = captionField.getText();
        Photo photo = (Photo) photoList.getSelectionModel().getSelectedItem();
        photo.setCaption(caption);
        captionField.clear();
        photoList.refresh();
        Helper.writeUsersToDisk(users);
        
    }
    
    public void deletePhoto(ActionEvent event) {
        // handle delte photo
        Photo photo = (Photo) photoList.getSelectionModel().getSelectedItem();
		if (photo == null) {
			Alert alert0 = new Alert(AlertType.ERROR);
			alert0.setTitle("Album Dashboard Error");
			alert0.setHeaderText("No Photo Selected");
			alert0.setContentText("Please select an photo to delete.");
			alert0.showAndWait();
			return;
		}
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Album Dashboard Confirmation");
		alert.setHeaderText("Photo Deletion Confirmation.");
		alert.setContentText(("Delete album: " + photo.getName() + "\"?"));
		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> res = alert.showAndWait();
		if (res.get().equals(ButtonType.YES)) {
			photos.remove(photo);
			photoList.getItems().remove(photo);
			// save data
			Alert alert1 = new Alert(AlertType.INFORMATION);
			alert1.setTitle("Album Dashboard Confirmation");
			alert1.setHeaderText("Photo deleted");
			alert1.setContentText("Photo " + photo.getName() + " was deleted");
		}
        photoList.refresh();
        Helper.writeUsersToDisk(users);
        Helper.readUsersFromDisk(users);
    }
    
    public void openSelectedPhoto(ActionEvent event) {
        Photo photoSelected = (Photo) photoList.getSelectionModel().getSelectedItem(); // double check if this correct
		if (photoSelected == null) {
			Alert alert0 = new Alert(AlertType.ERROR);
			alert0.setTitle("Album Dashboard Error");
			alert0.setHeaderText("No Photo Selected");
			alert0.setContentText("Please select a photo to open.");
			alert0.showAndWait();
			return;
		}
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
    public void addPhoto(ActionEvent event) {
        // handle add photos
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Image");
        ExtensionFilter imageFiles = new ExtensionFilter("Image Files", "*.bmp", "*.BMP", "*.gif", "*.GIF", "*.jpg", "*.JPG", "*.png",
        "*.PNG");
        ExtensionFilter bitMapFiles = new ExtensionFilter("Bitmap Files", "*.bmp", "*.BMP");
        ExtensionFilter gifFiles = new ExtensionFilter("GIF Files", "*.gif", "*.GIF");
        ExtensionFilter jpegFiles = new ExtensionFilter("JPEG Files", "*.jpg", "*.JPG");
        ExtensionFilter pngFiles = new ExtensionFilter("PNG Files", "*.png", "*.PNG");
        chooser.getExtensionFilters().addAll(imageFiles, bitMapFiles, gifFiles, jpegFiles, pngFiles);
        File chosenFile = chooser.showOpenDialog(null);
        
        if (chosenFile != null) {
            Image imageToAdd = new Image(chosenFile.toURI().toString());
            String name = chosenFile.getName();
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(chosenFile.lastModified());
            Photo photoToAdd = new Photo(name, imageToAdd, date);
            checkIfDuplicate(photoToAdd);
            photos.add(photoToAdd);
            photoList.setItems(FXCollections.observableArrayList(photos));
            // album.getPhotos().add(photoToAdd);
            Helper.writeUsersToDisk(users);
        }
    }

    public void checkIfDuplicate(Photo photo) {
        for (Photo photoIter : photos) {
            if (photoIter.equals(photo)) {
                // System.out.println(photoIter.getName());
                // System.out.println(photo.getName());
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Album Dashboard Error");
                alert.setHeaderText("Photo Add Error");
                alert.setContentText("Cannot add duplicate photo. Please change name of photo if you would still like to add photo.");
                alert.showAndWait();
                return;
            }
        }
    }
    public void handleBack(ActionEvent event) {
        // handle back (to User Dash)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserDashboard.fxml"));
            Parent root = loader.load();
            UserController controller = loader.<UserController>getController();
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            controller.Start(user, users);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
					
    }
    public void handleLogout(ActionEvent event) {
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
            Helper.writeUsersToDisk(users);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
    }
}
