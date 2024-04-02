package controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import util.CommonFunctions;
import util.PhotoCell;
import java.util.Calendar;
import java.util.Locale;

/**
 * The Photo Search Controller implements the search functionality and displays
 * the output result to the user.
 * 
 * @author Ayush Joshi
 * @author Anil Tilve
 *
 */
public class PhotoSearchController {

	@FXML
	private Button createAlbumBtn, LogoutButton;
	@FXML
	private ChoiceBox<String> tagTypeChoiceBox, tagValueChoiceBox;
	@FXML
	private DatePicker fromDate, toDate;
	@FXML
	private ListView<Tag> tags;
	@FXML
	ListView<Photo> photoListView;

	ArrayList<User> users;
	private User user;

	boolean Checked, go;

	/**
	 * On Start the user and users are initialized. The tag-type and tag-value choice
	 * boxes are initialized by loading all the unique tags and values from all the
	 * photos for a user.
	 * 
	 * @param user
	 *            takes the current user.
	 * @param users
	 *            takes the list of users.
	 */
	public void start(User user, ArrayList<User> users) {
		this.user = user;
		this.users = users;

		photoListView.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
			@Override
			public ListCell<Photo> call(ListView<Photo> photoList) {
				return new PhotoCell();
			}
		});

		ArrayList<String> tagtype = new ArrayList<String>();
		ArrayList<String> tagvalue = new ArrayList<String>();
		tagtype.add(0, "Tag Type");
		tagvalue.add(0, "Tag Value");
		ArrayList<Album> allalbums = user.getAlbums();
		for (Album curralbum : allalbums) {
			ArrayList<Photo> allphoto = curralbum.getPhotos();
			for (Photo photo : allphoto) {
				ArrayList<Tag> tag = photo.getTags();
				for (Tag t : tag) {
					if (!tagtype.contains(t.getName()))
						tagtype.add(t.getName());
					if (!tagvalue.contains(t.getValue()))
						tagvalue.add(t.getValue());
				}

			}

		}
		tagTypeChoiceBox.setItems(FXCollections.observableArrayList(tagtype));
		tagTypeChoiceBox.setValue("Tag Type");

		tagValueChoiceBox.setItems(FXCollections.observableArrayList(tagvalue));
		tagValueChoiceBox.setValue("Tag Value");

	}

	/**
	 * At any given point the user can logout and it will take user to the main
	 * login screen.
	 * 
	 * @param event
	 *            takes the mouse event.
	 */
	public void handleLogoutButton(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
			Parent parent = (Parent) loader.load();
			LoginController controller = loader.<LoginController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	/**
	 * Handles the back button and takes the current user to display user dash-board.
	 * with all the albums.
	 * 
	 * @param event
	 *            takes the mouse click event.
	 */
	public void handleBackToAlbumsButton(Event event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserDashboard.fxml"));
			Parent parent = (Parent) loader.load();
			UserController controller = loader.<UserController>getController();
			Scene scene = new Scene(parent);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			controller.start(user, users);
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	/**
	 * Handles add tag functions to add tags to the list view for the search
	 * criteria. The selected tag type and tag value is checked in the list view and
	 * added if it doesn't exist.
	 * 
	 * @param event
	 *            takes the mouse click event.
	 */
	public void handleAddTag(ActionEvent event) {

		ObservableList<Tag> tagList = tags.getItems();

		Tag newTag = new Tag(tagTypeChoiceBox.getSelectionModel().getSelectedItem().toString(),
				tagValueChoiceBox.getSelectionModel().getSelectedItem().toString());
		for (Tag currentTag : tagList) {

			if (currentTag.equals(newTag)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Search View Error");
				alert.setHeaderText("Tag Add Error.");
				alert.setContentText("A tag with this name and value already exists.");
				alert.showAndWait();
				return;
			}
		}

		tags.getItems().add(newTag);
		tags.refresh();
		tags.getSelectionModel().select(0);
		tagTypeChoiceBox.getSelectionModel().select(0);
		tagValueChoiceBox.getSelectionModel().select(0);

	}

	/**
	 * Handles remove tag button and removes the selected tag pair from the list.
	 * 
	 * @param event
	 *            takes the mouse click event.
	 */
	public void handleRemoveTag(ActionEvent event) {
		tags.getItems().remove(tags.getSelectionModel().getSelectedItem());
		tags.refresh();
		tags.getSelectionModel().select(0);

	}

	/**
	 * Handles Search photo button and on click, it goes through every photo in every
	 * album for the user and checks the date range as well as the tag pair and add
	 * to the photo view.
	 * 
	 * @param event
	 *            takes the mouse click event.
	 */
	public void handleSearchPhotos(ActionEvent event) {

		photoListView.getItems().clear();
		Checked = false;
		go = false;

		ArrayList<Album> albumList = user.getAlbums();
		for (Album album : albumList) {
			ArrayList<Photo> photolist = album.getPhotos();
			for (Photo photo : photolist) {
				boolean added = false;
				ArrayList<Tag> phototag = photo.getTags();
				String[] photodate = photo.getDate().getTime().toString().split(" ");

				DateTimeFormatter parser = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ENGLISH);
				TemporalAccessor accessor = parser.parse(photodate[1]);
				int month = accessor.get(ChronoField.MONTH_OF_YEAR);

				LocalDate photoDate = LocalDate.of(Integer.parseInt(photodate[5]), month,
						Integer.parseInt(photodate[2]));

				if (checkdatefields() && go) {

					// FromDate
					String frdate = fromDate.getValue().toString();
					String[] fromdate = frdate.split("-");
					LocalDate f_date = LocalDate.of(Integer.parseInt(fromdate[0]), Integer.parseInt(fromdate[1]),
							Integer.parseInt(fromdate[2]));

					// ToDate
					String todate = toDate.getValue().toString();
					String[] tdate = todate.split("-");
					LocalDate t_date = LocalDate.of(Integer.parseInt(tdate[0]), Integer.parseInt(tdate[1]),
							Integer.parseInt(tdate[2]));

					if (photoDate.isAfter(f_date) && photoDate.isBefore(t_date)) {

						if (photoListView.getItems().contains(photo)) {
							continue;
						} else {
							added = true;
							photoListView.getItems().add(photo);
							photoListView.refresh();
						}

					}
				}

				if (tags.getItems() != null && phototag != null && added == false) {
					for (Tag currTag : tags.getItems()) {
						for (Tag pTag : phototag) {
							if (currTag.getName().matches(pTag.getName()) && currTag.getValue().matches(pTag.getValue())) {
								if (photoListView.getItems().contains(photo)) {
									continue;
								} else {
									photoListView.getItems().add(photo);
									photoListView.refresh();
								}

							}

						}
					}

				}
			}
		}

	}

	/**
	 * Checks if the dates are available in the datepicker box and prompts the alert
	 * of only one of them is selected and returns false. Returns true if both the
	 * dates are filled.
	 * 
	 * @return boolean value true or false.
	 */
	public boolean checkdatefields() {

		if (Checked == false) {

			Checked = true;

			if (toDate.getValue() != null && fromDate.getValue() == null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Search View Error");
				alert.setHeaderText("From Date Error.");
				alert.setContentText("Please select From Date.");

				alert.showAndWait();
				return false;
			}
			if (toDate.getValue() == null && fromDate.getValue() != null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Search View Error");
				alert.setHeaderText("To Date Error.");
				alert.setContentText("Please select To Date.");

				alert.showAndWait();
				return false;
			}

		}

		if (toDate.getValue() != null && fromDate.getValue() != null) {
			go = true;
			return true;
		}
		return false;
	}

	/**
	 * Handles Create Album button, after the search and creates a new album titled
	 * "Search + current-date-time" for the user and adds all the photos from the
	 * photo list view to the album. Then saves the data using common function.
	 */
	public void handleCreateAlbumFromResults() {
		if (photoListView.getItems().isEmpty()) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Search View Error");
			alert.setHeaderText("No Photos Error.");
			alert.setContentText("Please Search Photos to Create Album.");
			alert.showAndWait();

		} else {

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			Album newAlbum = new Album("Search Result " + dateFormat.format(cal.getTime()).toString());
			user.getAlbums().add(newAlbum);

			for (Photo currphoto : photoListView.getItems()) {
				newAlbum.getPhotos().add(currphoto);
			}
			CommonFunctions.saveData(users);
		}
	}
}