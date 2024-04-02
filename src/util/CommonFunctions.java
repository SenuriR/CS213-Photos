package util;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import model.User;

/**
 * Common functions class - we tried to use a controller superclass but method
 * calls did not work for some odd reason
 */
public class CommonFunctions {
	/**
	 * Saves the data to data.dat
	 * 
	 * @param users
	 *            the user list
	 */
	public static void saveData(ArrayList<User> users) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("data/data.dat");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

			objectOutputStream.writeObject(users);

			objectOutputStream.close();
			fileOutputStream.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
