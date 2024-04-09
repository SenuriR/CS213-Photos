package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;


public class Album implements Serializable {

	private static final long serialVersionUID = 1891567810783724951L;
	private String name;
	private ArrayList<Photo> photos;
	
	/**
	 * Constructor
	 * @param name
	 */
	public Album(String name) {
		this.name = name;
		photos = new ArrayList<Photo>();
	}
	
	/**
	 * Gets the name of this album
	 * @return the name of this album
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name of this album
	 * @param name the new name of this album
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the photos in this album
	 * @return an arraylist of photos
	 */
	public ArrayList<Photo> getPhotos() {
		return this.photos;
	}
	
	/**
	 * Returns the number of photos in this album
	 * @return the number of photos in this album
	 */
	public int getPhotoCount() {
		return this.photos.size();
	}
	
	/**
	 * Compares this album to another
	 * @param other the album to be compared
	 * @return true if the albums are equal, false otherwise
	 */
	public boolean equals(Album other) {
		return name.equals(other.name);
	}
	
	/**
	 * Returns a string representation of this album
	 */
	public String toString() {
		String result = "NAME: " + name + "\nPHOTO COUNT: " + photos.size(); 
		return result;
	}
}
