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
    }

    @Override
    protected void updateItem(Photo photo, boolean empty) {
        super.updateItem(photo, empty);

        if (empty || photo == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Set thumbnail image
            imageView.setImage(photo.getImage()); // Assuming photo.getImage() returns an Image
            setText(photo.getCaption());
            setGraphic(imageView);
        }
    }
}
