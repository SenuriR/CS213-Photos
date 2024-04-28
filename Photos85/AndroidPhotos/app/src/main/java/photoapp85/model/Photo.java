package photoapp85.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;




public class Photo implements Serializable {


	private static final long serialVersionUID = 6955723612371190680L;
	private ArrayList<Tag> tags;
	private String name, caption;
	private SerializableBitmap bitmap;
	private Calendar date;

	public Photo(String caption, Bitmap bitmap) {
		this.caption = caption;
		this.tags = new ArrayList<Tag>();
		this.bitmap = new SerializableBitmap(bitmap);
	}
	/**
	 * Returns the name of this photo
	 * @return the name of this photo
	 */
	public String getName() {
		return name;
	}

	public ArrayList<Tag> setStarterTags() {
		ArrayList<Tag> tagsStarter = new ArrayList<>();
		Tag locationNameTag = new Tag("Location", "");
		Tag personNameTag = new Tag("Person", "");
		Tag activityNameTag = new Tag("Activity", "");
		tagsStarter.add(locationNameTag);
		tagsStarter.add(personNameTag);
		tagsStarter.add(activityNameTag);
		return tagsStarter;
	}

	/**
	 * Returns the caption of this photo
	 * @return the caption of this photo
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Returns the image represented by this photo
	 * @return the image represented by this photo
	 */
	public Bitmap getBitmap() {
		return bitmap.getBitmap();
	}


	/**
	 * Returns the tags for this photo
	 * @return an arraylist of tags
	 */
	public ArrayList<Tag> getTags() {
		return tags;
	}

	/**
	 * Returns the last modified date of this photo
	 * @return the last modified date of this photo
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * Set the caption of this photo
	 * @param caption the new caption of this photo
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Compares this photo to another
	 * @param other the photo to be compared to
	 * @return true if the photos are equal, false otherwise
	 */
	public boolean equals(Photo other) {
		return this.name.equals(other.name);
	}
}
