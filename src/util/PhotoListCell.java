package util;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Photo;

public class PhotoListCell extends ListCell<Photo> {

    private static final int THUMBNAIL_WIDTH = 100;  // Set desired width
    private static final int THUMBNAIL_HEIGHT = 100; // Set desired height

    private ImageView imageView;

    public PhotoListCell() {
        this.imageView = new ImageView();
        this.imageView.setFitWidth(THUMBNAIL_WIDTH);
        this.imageView.setFitHeight(THUMBNAIL_HEIGHT);
        this.imageView.setPreserveRatio(true);
        this.imageView.setSmooth(true); // Optional: Improve image quality
    }

    @Override
    protected void updateItem(Photo photo, boolean empty) {
        super.updateItem(photo, empty);

        if (empty || photo == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Set thumbnail image with error handling
            Image image = photo.getImage();
            if (image.isError()) {
                // Handle image loading error
                imageView.setImage(null); // Clear existing image
                setText("Error loading image");
            } else {
                // Resize image to fit thumbnail dimensions
                imageView.setImage(image);
                imageView.setFitWidth(THUMBNAIL_WIDTH);
                imageView.setFitHeight(THUMBNAIL_HEIGHT);
                String text = "Caption: " + photo.getCaption() + " Tag(s): " + photo.getTags();
                setText(text);
            }
            setGraphic(imageView);
        }
    }
}
