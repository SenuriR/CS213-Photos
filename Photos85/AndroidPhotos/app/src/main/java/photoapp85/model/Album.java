package photoapp85.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.ArrayList;

public class Album implements Parcelable {

	// private static final long serialVersionUID = 1891567810783724951L;
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

	@Override
	public int describeContents() {
		return 0;
	}

	// write your object's data to the passed-in Parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(name);
		SparseArray<Photo> photoSparceArray = new SparseArray<>();
		for (int i = 0; i < photos.size(); i++) {
			photoSparceArray.put(i, photos.get(i));
		}
		out.writeSparseArray(photoSparceArray);
	}

	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
	public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
		public Album createFromParcel(Parcel in) {
			return new Album(in);
		}

		public Album[] newArray(int size) {
			return new Album[size];
		}
	};

	// example constructor that takes a Parcel and gives you an object populated with it's values
	private Album(Parcel in) {
		name = in.readString();
		SparseArray<Photo> photoSparseArray = in.readSparseArray(null);
		this.photos = new ArrayList<>();
		for (int i = 0; i < photoSparseArray.size(); i++) {
			photos.add(photoSparseArray.valueAt(i));
		}
	}

}
