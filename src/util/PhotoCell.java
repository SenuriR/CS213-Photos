package util;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Photo;

/**
 * Displays photo thumbnail in listview
 */
public class PhotoCell extends ListCell<Photo> {

	AnchorPane anchorPane = new AnchorPane();
	StackPane stackPane = new StackPane();
	ImageView imageView = new ImageView();
	Label captionLabel = new Label(), captionText = new Label(), nameLabel = new Label(), nameText = new Label();

	/**
	 * Constructor
	 */
	public PhotoCell() {
		super();

		nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
		nameText.setFont(Font.font(12));
		captionLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
		captionText.setFont(Font.font(12));

		imageView.setFitWidth(45.0);
		imageView.setFitHeight(45.0);
		imageView.setPreserveRatio(true);

		StackPane.setAlignment(imageView, Pos.CENTER);

		stackPane.getChildren().add(imageView);

		stackPane.setPrefHeight(55.0);
		stackPane.setPrefWidth(45.0);

		AnchorPane.setLeftAnchor(stackPane, 0.0);

		AnchorPane.setLeftAnchor(nameLabel, 55.0);
		AnchorPane.setTopAnchor(nameLabel, 0.0);
		AnchorPane.setLeftAnchor(nameText, 95.0);
		AnchorPane.setTopAnchor(nameText, 0.0);

		AnchorPane.setLeftAnchor(captionLabel, 55.0);
		AnchorPane.setTopAnchor(captionLabel, 24.0);
		AnchorPane.setLeftAnchor(captionText, 105.0);
		AnchorPane.setTopAnchor(captionText, 24.0);

		anchorPane.getChildren().addAll(stackPane, nameLabel, nameText, captionLabel, captionText);

		anchorPane.setPrefHeight(55.0);

		captionLabel.setMaxWidth(300.0);

		setGraphic(anchorPane);
	}

	@Override
	/**
	 * Updates item
	 */
	public void updateItem(Photo photo, boolean empty) {
		super.updateItem(photo, empty);
		setText(null);
		if (photo == null) {
			imageView.setImage(null);
			nameLabel.setText("");
			nameText.setText("");
			captionLabel.setText("");
			captionText.setText("");
		}
		if (photo != null) {
			imageView.setImage(photo.getImage());
			nameLabel.setText("Name: ");
			nameText.setText(photo.getName());
			captionLabel.setText("Caption: ");
			captionText.setText(photo.getCaption());
		}
	}

}
