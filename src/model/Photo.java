package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javafx.scene.image.Image;



public class Photo implements Serializable {

	
	private static final long serialVersionUID = 6955723612371190680L;
	private ArrayList<Tag> tags;
	private String name, caption;
	private SerializableImage image;
	private Calendar date;

	/**
	 * Constructor
	 * @param name the name of the photo
	 * @param image the image to be represented by the photo
	 * @param date the last modified date
	 */
	public Photo(String name, SerializableImage image, Calendar date) {
		this.name = name;
		this.caption = "";
		this.image = image;
		this.date = date;
		this.tags = new ArrayList<Tag>();
		this.date.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * 2nd constructor
	 * @param name the name of the photo
	 * @param image the image to be represented by the photo
	 * @param date the last modified date
	 */
	public Photo(String name, Image image, Calendar date) {
		this.name = name;
		this.caption = "";
		this.image = new SerializableImage(image);
		this.date = date;
		this.tags = new ArrayList<Tag>();
		this.date.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * Returns the name of this photo
	 * @return the name of this photo
	 */
	public String getName() {
		return name;
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
	public Image getImage() {
		return image.getImage();
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
