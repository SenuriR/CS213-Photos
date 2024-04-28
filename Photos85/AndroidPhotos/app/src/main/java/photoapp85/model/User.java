package photoapp85.model;

import java.io.Serializable;
import java.util.ArrayList;


public class User implements Serializable {
	
	private static final long serialVersionUID = 8177923271139908648L;
	private String username;
	private ArrayList<Album> albums;
	
	/**
	 * Creates a new user
	 * @param username the name of the new user
	 */
	public User(String username) {
		this.username = username;
		albums = new ArrayList<Album>();
	}
	
	/**
	 * Returns the username of this user
	 * @return the username of this user
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns the username of this user
	 * @return the username of this user
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * Returns a string representation of this user
	 * @return a string representation of this user
	 */
	public String toString() {
		return this.username;
	}
	
	/**
	 * Checks if this user is equal to another
	 * @param other the user to be compared to
	 * @return true if the users are equal, false otherwise
	 */
	public boolean equals(User other) {
		return this.username.equals(other.username);
	}
}
