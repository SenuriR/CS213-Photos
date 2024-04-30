package photoapp85;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.io.File;

import com.example.androidphotos.R;
import photoapp85.adapter.PhotoAdapter;
import photoapp85.model.Album;
import photoapp85.model.Photo;
import photoapp85.model.Tag;
import photoapp85.util.Helper;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Album> albums;
    private ListView listView;
    private String path;
    private static final int REQUEST_CODE_ALBUM_ACTIVITY = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // basic initialization from photos proj
        path = this.getApplicationInfo().dataDir + "/data.dat";
        File data = new File(path);
        data.delete(); // KEEP THIS HERE FOR NOW, FOR SOME REASON, WHEN WE'RE NOT STARTING FRESH WE GET AN ERROR
        if (!data.exists() || !data.isFile()) {
            try {
                data.createNewFile();
                albums = new ArrayList<Album>();
                albums.add(new Album("stock"));
                Helper.saveData(albums, path);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            albums = (ArrayList<Album>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<Album> adapter = new ArrayAdapter<Album>(this, R.layout.album_view, albums);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        listView.setItemChecked(0, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setItemChecked(position, true);
            }
        });
    }

    public void removeAlbum(View view) {

        // GET ADAPTER TO VIEW ALBUMS
        final ArrayAdapter<Album> adapter = (ArrayAdapter<Album>) listView.getAdapter();

        // IF NO DATA ON DISK
        if (adapter.getCount() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("No albums to display.")
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        // USER HAS SELECTED AN ITEM TO DELETE
        if (listView.getCheckedItemPosition() == -1) {
            new AlertDialog.Builder(this)
                    .setMessage("Please select an album to remove.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        final Album albumToDelete = adapter.getItem(listView.getCheckedItemPosition());

        // TO VERIFY REMOVE ALBUM
        AlertDialog.Builder builder = getBuilder(albumToDelete, adapter, listView.getCheckedItemPosition());
        builder.show();
        for (int i = 0; i < adapter.getCount(); i ++) {
            listView.setItemChecked(i, false);
        }
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private AlertDialog.Builder getBuilder(Album albumToDelete, ArrayAdapter<Album> adapter, int selectedItemPos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove " + albumToDelete.getName() + "?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        adapter.remove(albumToDelete);
                        Helper.saveData(adapter, path);
                        listView.setItemChecked(selectedItemPos, true);
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder;
    }

    public void addAlbum(View view) {

        // GET ADAPTER TO VIEW ALBUMS
        final ArrayAdapter<Album> adapter = (ArrayAdapter<Album>) listView.getAdapter();

        // FOR USER TO INPUT NEW ALBUM NAME
        final EditText requestedAlbumName = new EditText(this);
        requestedAlbumName.setInputType(InputType.TYPE_CLASS_TEXT);

        // TO VERIFY ADD ALBUM
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(requestedAlbumName);

        // USER WANTS TO ADD ALBUM
        builder.setPositiveButton("Add Album", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newAlbumName = requestedAlbumName.getText().toString();
                Album newAlbum = new Album(newAlbumName);

                // VERIFY THAT ALBUM NAME IS NOT TAKEN
                if (!albumNameTaken(adapter, builder, newAlbumName)) {
                    // ADD NEWLY CREATED ALBUM TO THE DISK
                    adapter.add(newAlbum);
                    Helper.saveData(adapter, path);
                } else {
                    return;
                }
            }
        });

        // USER CANCELS ADD ALBUM BUTTON
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void renameAlbum(View view) {
        // TO VIEW ALBUMS FROM DISK
        final ArrayAdapter<Album> adapter = (ArrayAdapter<Album>) listView.getAdapter();

        // GET REQUESTED NEW ALBUM NAME FROM USER INPUT
        final EditText requestedNewAlbumName = new EditText(this);
        requestedNewAlbumName.setInputType(InputType.TYPE_CLASS_TEXT);
        if (listView.getCheckedItemPosition() == -1) {
            // user has not selected a album to delete
            new AlertDialog.Builder(this)
                    .setMessage("Please select an album to rename.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        requestedNewAlbumName.setText(adapter.getItem(listView.getCheckedItemPosition()).getName());
        requestedNewAlbumName.setSelection(requestedNewAlbumName.getText().length());

        // TO VERIFY ALBUM RENAME
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(requestedNewAlbumName);

        // CONFIRM RENAME OF ALBUM
        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumName = requestedNewAlbumName.getText().toString();

                // VERIFY THAT ALBUM NAME NOT TAKEN
                if (!albumNameTaken(adapter, builder, albumName)) {
                    // UPDATE ADAPTER - SAVE CHANGES TO DISK
                    adapter.getItem(listView.getCheckedItemPosition()).setName(albumName);
                    adapter.notifyDataSetChanged();
                    Helper.saveData(adapter, path);
                } else {
                    return;
                }
            }
        });

        // CANCEL RENAME ALBUM ACTION
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void openAlbum(View view) {

        // IF NO ALBUMS ON DISK
        if (listView.getAdapter().getCount() == 0) {
            return;
        }

        Intent intent = new Intent(this, AlbumActivity.class);

        try {
            int albumPos = listView.getCheckedItemPosition();
            albums.get(albumPos);
            intent.putExtra("albumPos", listView.getCheckedItemPosition());
            intent.putExtra("albums", albums);
            startActivityForResult(intent, REQUEST_CODE_ALBUM_ACTIVITY);
        } catch(Exception e) {
            intent.putExtra("albumPos", 0);
            intent.putExtra("albums", albums);
            startActivityForResult(intent, REQUEST_CODE_ALBUM_ACTIVITY);
        }
    }

    public void searchAlbums(View view) {
        // SET LAYOUT
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // INITIALIZE OPTIONS FOR LOCATION OR PERSON SEARCH
        final EditText locationSearchInput = new EditText(this);
        locationSearchInput.setHint("Location");
        layout.addView(locationSearchInput);
        final EditText personSearchInput = new EditText(this);
        personSearchInput.setHint("Person");
        layout.addView(personSearchInput);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        // INITIALIZE SEARCH OBJECTS
                        ArrayList<Photo> searchRes;
                        String locationSearchString = locationSearchInput.getText().toString();
                        String personSearchString = personSearchInput.getText().toString();
                        boolean alreadyAddedToSearch = false;

                        // SEARCH
                        searchRes = getSearchRes(locationSearchString, personSearchString, alreadyAddedToSearch, albums);

                        // IF NO MATCHES FOR REQUESTED SEARCH
                        if (searchRes.isEmpty()) {
                            new AlertDialog.Builder(builder.getContext())
                                    .setMessage("No search results.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                            return;
                        } else {
                            PhotoAdapter photoAdapter = new PhotoAdapter(builder.getContext(), R.layout.photo_view, searchRes);
                            ListView searchView = new ListView(builder.getContext());
                            searchView.setAdapter((ListAdapter) photoAdapter);

                            AlertDialog.Builder searchBuilder = new AlertDialog.Builder(builder.getContext());
                            searchBuilder.setView(searchView)
                                    .setPositiveButton("Search Again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            searchAlbums(null);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialog.cancel();
                                        }
                                    });
                            searchBuilder.show();
                        }
                    }
                })
                // CANCEL SEARCH
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // SOFT KEYBOARD
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public ArrayList<Photo> getSearchRes(String locationSearchString, String personSearchString, boolean alreadyAddedToSearch, ArrayList<Album> albums) {
        ArrayList<Photo> searchRes = new ArrayList<>();
        for (Album currAlbum : albums) {
            for (Photo currPhoto : currAlbum.getPhotos()) {
                for (Tag currTag : currPhoto.getTags()) {
                    String tagVal = currTag.getValue();

                    if (!tagVal.isEmpty()) {
                        if (!personSearchString.isEmpty() && tagVal.contains(personSearchString) ||
                                !locationSearchString.isEmpty() && tagVal.contains(locationSearchString)) {
                            for (Photo currAddedPhoto : searchRes) {
                                if (currAddedPhoto.equals(currPhoto)) {
                                    // SO WE DON'T ADD DUPLICATES
                                    alreadyAddedToSearch = true;
                                    break;
                                }
                            }
                            // VALID ADDITION TO SEARCH (NOT A DUPLICATE)
                            if (!alreadyAddedToSearch) {
                                searchRes.add(currPhoto);
                                alreadyAddedToSearch = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return searchRes;
    }

    // HELPER METHOD
    public boolean albumNameTaken(ArrayAdapter<Album> adapter, AlertDialog.Builder builder, String albumName) {
        for (int index = 0; index < adapter.getCount(); index++)
            if (albumName.equals(adapter.getItem(index).getName())) {
                new AlertDialog.Builder(builder.getContext())
                        .setMessage("Album " + albumName + " already exists.")
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ALBUM_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                // Retrieve the updated albums list from the result data
                ArrayList<Album> updatedAlbums = (ArrayList<Album>) data.getSerializableExtra("albums");
                if (updatedAlbums != null) {
                    // Update the albums list in the MainActivity
                    albums.clear();
                    albums.addAll(updatedAlbums);
                    // Refresh the ListView or any other UI component to reflect the changes
                    ((ArrayAdapter<Album>) listView.getAdapter()).notifyDataSetChanged();
                }
            }
        }
    }


}