package photoapp85.util; // Replace with your actual package name

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import photoapp85.model.Photo;
import photoapp85.model.Album;
import photoapp85.model.User;
import photoapp85.model.Tag;
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
        for (Album a : albums) {
            for (Photo p : a.getPhotos()) {
                for (Tag t : p.getTags()) {
                    System.out.println(t);
                }
            }
        }
    }

    public static void saveData(ArrayAdapter<Album> arrayAdapter, String path) {
        // PROCESS: BASICALLY HAVE THE CHANGES DONE TO ADAPTER, THEN REWRITE OVER THE OLD ALBUMS ARRAYLIST
        ArrayList<Album> albums = new ArrayList<Album>();
        for (int index = 0; index < arrayAdapter.getCount(); index++)
            albums.add(arrayAdapter.getItem(index));

        saveData(albums, path);
    }
}
