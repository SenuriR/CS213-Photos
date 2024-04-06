package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

public class PhotoSearchController {
    @FXML
    ChoiceBox tagTypeChoiceBox, tagValueChoiceBox;
    @FXML
    DatePicker fromDate, toDate;
    @FXML
    ListView tagsListView, photoListView;
    @FXML
    Button backButton, logoutButton, createAlbumBtn;

    public void handleCreateAlbumFromResults(ActionEvent event) {
        // handle create album from results
    }
    public void handleBackToAlbumsButton(ActionEvent event) {
        // handle back to albums
    }
    
    public void handleSearchPhotos(ActionEvent event) {
        // handle search photos
    }

    public void handleLogoutButton(ActionEvent event) {
        // handle logout
    }

    
}
