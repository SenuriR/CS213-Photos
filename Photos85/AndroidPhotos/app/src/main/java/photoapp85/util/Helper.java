package photoapp85.util;

import android.widget.ArrayAdapter;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import photoapp85.model.Album;

public class Helper {

    public static void saveData(ArrayList<Album> albums, String path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(albums);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        /*
        for (Album a : albums) {
            for (Photo p : a.getPhotos()) {
                for (Tag t : p.getTags()) {
                    System.out.println(t);
                }
            }
        }
        */
    }

    public static void saveData(ArrayAdapter<Album> arrayAdapter, String path) {
        // PROCESS: BASICALLY HAVE THE CHANGES DONE TO ADAPTER, THEN REWRITE OVER THE OLD ALBUMS ARRAYLIST
        ArrayList<Album> albums = new ArrayList<Album>();
        for (int index = 0; index < arrayAdapter.getCount(); index++)
            albums.add(arrayAdapter.getItem(index));

        saveData(albums, path);
    }
}
