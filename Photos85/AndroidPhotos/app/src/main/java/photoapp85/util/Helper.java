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
    }

    public static void saveData(ArrayAdapter<Album> arrayAdapter, String path) {
        ArrayList<Album> albums = new ArrayList<Album>();

        for (int index = 0; index < arrayAdapter.getCount(); index++)
            albums.add(arrayAdapter.getItem(index));

        saveData(albums, path);
    }

    public ArrayList<Photo> removeDuplicatePhotos(ArrayList<Photo> photos) {
        HashSet<Photo> photosNoDuplicates = new HashSet<>(photos);
        return new ArrayList<>(photosNoDuplicates);
    }
}
