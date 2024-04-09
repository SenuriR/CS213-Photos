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

public class EditPhotoController {
	@FXML
    Button backButton, logoutButton, addTagButton, deleteTagButton, editCaptionButton, editExistingTag;
    @FXML
    ImageView imageView;
    @FXML
    Label photoNameText, captionText, dateTakenText;
    @FXML
    TextField tagTypeField, tagValueField, captionField;
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
    
    public void handleEditCaption(ActionEvent event) {
        String caption = captionField.getText();
        if (caption.isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Edit Caption Confirmation");
            alert.setHeaderText("Clear Caption");
            alert.setContentText("Are you sure you want to clear the caption?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> res = alert.showAndWait();
            if(res.get().equals(ButtonType.YES)) {
                captionField.clear();
            } else {
                return;
            }
        }
        photo.setCaption(caption);
        captionField.clear();
        this.captionText.setText(photo.getCaption());
        Helper.writeUsersToDisk(users);
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

    public void handleEditTag(ActionEvent event) {
        Tag tagSelected = (Tag) tagsList.getSelectionModel().getSelectedItem();
        for (Tag t : tags) {
            if (t.equals(tagSelected)) {
                tagSelected = t;
            }
        }
        if (tagSelected != null) {
            tags.remove(tagSelected);
            tagTypeField.setText(tagSelected.getName());
            tagValueField.setText(tagSelected.getValue());
        } else {
            Alert alert0 = new Alert(AlertType.ERROR);
            alert0.setTitle("Delete Tag Error");
            alert0.setHeaderText("No Tag Selected");
            alert0.setContentText("Please select a tag to delete from list below.");
            alert0.showAndWait();
            return;
        }
        Helper.writeUsersToDisk(users);
        tagsList.setItems(FXCollections.observableArrayList(tags));
        tagsList.refresh();
    }

    public void handleAddTagButton(ActionEvent event) {
        String tagName = tagTypeField.getText().trim();
        String tagValue = tagValueField.getText().trim();
        Tag tagToAdd = new Tag(tagName, tagValue);
        for (Tag tag : tags) {
            if ((tag.getValue().toUpperCase()).equals(tagToAdd.getValue().toUpperCase()) || (tag.getName().toUpperCase().equals("LOCATION") && tagToAdd.getName().toUpperCase().equals("LOCATION"))) {
                Alert alert0 = new Alert(AlertType.ERROR);
                alert0.setTitle("Edit Photo Error");
                alert0.setHeaderText("Duplicate Tag");
                alert0.setContentText("Tag already exists.");
                alert0.showAndWait();
                tagTypeField.clear();
                tagValueField.clear();
                return;
            }
        }
        tags.add(tagToAdd);
        Helper.writeUsersToDisk(users);
        tagsList.setItems(FXCollections.observableArrayList(tags));
        tagsList.refresh();
        tagTypeField.clear();
        tagValueField.clear();
    }

    public void handleDeleteTagButton(ActionEvent event) {
        Tag tagToRemove = (Tag) tagsList.getSelectionModel().getSelectedItem();
        if (tagToRemove != null) {
            tags.remove(tagToRemove);
        } else {
            Alert alert0 = new Alert(AlertType.ERROR);
            alert0.setTitle("Delete Tag Error");
            alert0.setHeaderText("No Tag Selected");
            alert0.setContentText("Please select a tag to delete from list below.");
            alert0.showAndWait();
            return;
        }
        Helper.writeUsersToDisk(users);
        tagsList.setItems(FXCollections.observableArrayList(tags));
        tagsList.refresh();
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