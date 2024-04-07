package controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.User;
import util.Helper;
import util.PhotoListCell;
import model.Album;
import model.Tag;
import model.Photo;

public class PhotoSearchController {
    @FXML
    ChoiceBox tagChoiceBox;
    @FXML
    DatePicker fromDate, toDate;
    @FXML
    ListView tagsListView, photoListView;
    @FXML
    Button searchTags, searchDates, backButton, logoutButton, createAlbumBtn;
    private User user;
    private ArrayList<User> users;
    private Album album;
    private Boolean searchByTags;
    private ArrayList<Tag> tags;
    private ArrayList<Tag> tagsToSearch;
    private ArrayList<Photo> photoList;

    public void Start(ArrayList<User> users, User user){
        this.users = users;
        this.user = user;
        this.tagsToSearch = new ArrayList<>();
        tagsListView.setItems(FXCollections.observableArrayList(tagsToSearch));
        this.tags = getAllPossibleTagsForUser(user);
        tagChoiceBox.getItems().addAll(tags);
        this.photoList = new ArrayList<>();
        photoListView.setItems(FXCollections.observableArrayList(photoList));
    }

    public ArrayList<Tag> getAllPossibleTagsForUser(User user) {
        ArrayList<Tag> tagsPossible = new ArrayList<>();
        for (Album a : user.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                for (Tag t : p.getTags()) {
                    tagsPossible.add(t);
                }
            }
        }
        HashSet<Tag> hashSet = new HashSet<>(tagsPossible);
        return new ArrayList<>(hashSet);
    }

    public void handleAddTag(ActionEvent event) {
        Tag tagToAdd = (Tag) tagChoiceBox.getValue();
        if(tagToAdd == null) {
            // error - need to select tag in order to add it
            Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Photo Search Error");
			alert.setHeaderText("No Tag Selected");
			alert.setContentText("Please select a tag to add to search from drop down menu.");
			alert.showAndWait();
			return;
        }
        if (tagsToSearch.size() == 2) {
            // error - can only search up to two tags, cannot add more tags to search
            Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Photo Search Error");
			alert.setHeaderText("Tag Search Limit");
			alert.setContentText("Users can only search by up to 2 tags. Please remove a tag if you would like to add a different tag to search.");
			alert.showAndWait();
			return;
        }
        if (tagsToSearch.indexOf(tagToAdd) != -1) {
            // error -- cannot add duplicate tags to search
            Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Photo Search Error");
			alert.setHeaderText("Tag Search Duplicate");
			alert.setContentText("Tag selected is already in search criteria.");
			alert.showAndWait();
			return;
        }
        tagsToSearch.add((Tag) tagChoiceBox.getValue());
        tagsListView.setItems(FXCollections.observableArrayList(tagsToSearch));
        tagsListView.refresh();
        
    }
    
    public void handleRemoveTag(ActionEvent event) {
        Tag tagToRemove = (Tag) tagsListView.getSelectionModel().getSelectedItem();
        if (tagToRemove != null) {
            tagsToSearch.remove(tagToRemove);
        } else {
            Alert alert0 = new Alert(AlertType.ERROR);
            alert0.setTitle("Delete Tag Error");
            alert0.setHeaderText("No Tag Selected");
            alert0.setContentText("Please select a tag to delete from list below.");
            alert0.showAndWait();
            return;
        }
        tagsListView.setItems(FXCollections.observableArrayList(tagsToSearch));
        tagsListView.refresh();
    }

    public void handleSearchByDates(ActionEvent event) {
        photoList = new ArrayList<>();
        photoListView.setItems(FXCollections.observableArrayList(photoList));
        photoListView.setCellFactory(param -> new PhotoListCell());
        LocalDate photoDate;
        for (Album a : user.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                photoDate = p.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if ((photoDate.isAfter(fromDate.getValue())) && (photoDate.isBefore(toDate.getValue()))) {
                    photoList.add(p);
                }
            }
        }
        photoListView.setItems(FXCollections.observableArrayList(photoList));
        photoListView.setCellFactory(param -> new PhotoListCell());
    }

    public void handleSearchByTags(ActionEvent event) {
        photoList = new ArrayList<>();
        photoListView.setItems(FXCollections.observableArrayList(photoList));
        photoListView.setCellFactory(param -> new PhotoListCell());

        if(tagsToSearch.size() == 0){
			Alert alert0 = new Alert(AlertType.ERROR);
			alert0.setTitle("Photo Search Error");
			alert0.setHeaderText("No Tags Selected");
			alert0.setContentText("Please select tags to search for.");
			alert0.showAndWait();
			return;
        }

        if (tagsToSearch.size() == 1){
            // single tag search
            for (Album a : user.getAlbums()) {
                for (Photo p : a.getPhotos()) {
                    for (Tag t : p.getTags()) {
                        if ((t.equals(tagsToSearch.get(0)))) {
                            photoList.add(p);
                        }
                    }
                }
            }
            photoListView.setItems(FXCollections.observableArrayList(photoList));
            photoListView.setCellFactory(param -> new PhotoListCell());

        } else {
            // double tag search
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Photo Search Confirmation");
            alert.setHeaderText("Multiple Tags in Search");
            alert.setContentText(("Wuld like to search for photos that contain both tags (conjunctive) or you would like to search for photos that contain either tag (disjunctive)."));
            ButtonType conjButtonType = new ButtonType("Conjunctive Search");
            ButtonType disjButtonType = new ButtonType("Disjunctive Search");
            alert.getButtonTypes().setAll(conjButtonType, disjButtonType);
            Optional<ButtonType> res = alert.showAndWait();
            Boolean firstTagFound = false;
            Boolean secondTagFound = false;
            Set<Photo> alreadyAdded = new HashSet<>();
            if (res.get().equals(conjButtonType)) {
                for (Album a : user.getAlbums()) {
                    for (Photo p : a.getPhotos()) {
                        for (Tag t : p.getTags()) {
                            if (t.equals(tagsToSearch.get(0))) {
                                firstTagFound = true;
                            }
                            if (t.equals(tagsToSearch.get(1))) {
                                secondTagFound = true;
                            }
                            if (firstTagFound && secondTagFound) {
                                if (!alreadyAdded.contains(p)) {
                                    photoList.add(p);
                                    alreadyAdded.add(p);
                                }
                            }
                        }
                        firstTagFound = false;
                        secondTagFound = false;
                    }
                }
                photoListView.setItems(FXCollections.observableArrayList(photoList));
                photoListView.setCellFactory(param -> new PhotoListCell());
            } else {
                Set<Photo> alreadyAddedDisj = new HashSet<>();
                for (Album a : user.getAlbums()) {
                    for (Photo p : a.getPhotos()) {
                        for (Tag t : p.getTags()) {
                            if ((t.equals(tagsToSearch.get(0))) || (t.equals(tagsToSearch.get(1)))) {
                                if (!alreadyAddedDisj.contains(p)) {
                                    photoList.add(p);
                                    alreadyAddedDisj.add(p);
                                }
                            }
                        }
                    }
                }
                photoListView.setItems(FXCollections.observableArrayList(photoList));
                photoListView.setCellFactory(param -> new PhotoListCell());
            }
        }
    }

    public boolean albumAlreadyExists(Album newAlbum){
        for (Album a : user.getAlbums()) {
            if (a.equals(newAlbum)) {
                return true;
            }
        }
        return false;
    }
    public void handleCreateAlbumFromResults(ActionEvent event) {
        // handle create album from results
        if(photoList != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Album from Results");
            dialog.setHeaderText("Please enter new album name:");
            dialog.setContentText("Album Name:");
            Optional<String> result = dialog.showAndWait();
            String albumName = result.get();
            Album newAlbum = new Album(albumName);
            if(albumAlreadyExists(newAlbum)) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Create Album Error");
                alert.setHeaderText("Album Name Taken");
                alert.setContentText("Entered album name already exists. Please add a different album name.");
                alert.showAndWait();
                return;
            } else {
                user.getAlbums().add(newAlbum);
                for (Photo p : photoList) {
                    newAlbum.getPhotos().add(p);
                }
                Alert alert1 = new Alert(AlertType.INFORMATION);
                alert1.setTitle("Create Album Confirmation");
                alert1.setHeaderText("Album Created");
                alert1.setContentText("Album " + newAlbum.getName() + " was created");
                alert1.showAndWait();
                Helper.writeUsersToDisk(users);
            }            
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Create Album Error");
            alert.setHeaderText("Search Result Empty");
            alert.setContentText("Search result is empty. Please refine search criteria.");
            alert.showAndWait();
            return;
        }
    }

    public void handleBackToAlbumsButton(ActionEvent event) {
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
            Helper.writeUsersToDisk(users);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
    }

    
}
