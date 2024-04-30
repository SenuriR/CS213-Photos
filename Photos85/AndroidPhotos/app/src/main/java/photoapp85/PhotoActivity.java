package photoapp85;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import photoapp85.model.Album;
import photoapp85.model.Photo;
import photoapp85.model.Tag;
import photoapp85.util.Helper;
import com.example.androidphotos.R;

public class PhotoActivity extends AppCompatActivity {
    private ArrayList<Album> albums;
    private Album album;
    private Photo photo;
    private ListView listView;
    private ImageView imageView;
    private String path;
    private int albumPos, photoPos;

    private static final int REQUEST_CODE_ALBUM_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        path = this.getApplicationInfo().dataDir + "/data.dat";
        Intent intent = getIntent();
        albums = (ArrayList<Album>) intent.getSerializableExtra("albums");
        // GET ALBUM INFORMATION
        albumPos = intent.getIntExtra("albumPos", 0);
        album = albums.get(albumPos);
        photoPos = intent.getIntExtra("photoPos", 0);
        photo = album.getPhotos().get(photoPos);

        // INITIALIZE STUFF FOR LISTVIEW OF PHOTO TAGS
        ArrayAdapter<Tag> adapter = new ArrayAdapter<>(this, R.layout.album_view, photo.getTags());
        adapter.setNotifyOnChange(true);

        listView = findViewById(R.id.listView);
        imageView = findViewById(R.id.photo);

        listView.setItemChecked(0, true);
        listView.setAdapter(adapter);
        imageView.setImageBitmap(photo.getBitmap());
    }

    @Override
    public void onBackPressed() {
        // Update the tags of the current photo in the album
        album.getPhotos().get(photoPos).getTags().clear();
        album.getPhotos().get(photoPos).getTags().addAll(photo.getTags());

        Intent resultIntent = new Intent();
        resultIntent.putExtra("albums", albums);
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
    }
    // HOME BUTTON
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, AlbumActivity.class);
                intent.putExtra("albums", albums);
                intent.putExtra("albumPosition", albumPos);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addTag(View view) {
        final ArrayAdapter<Tag> adapter = (ArrayAdapter<Tag>) listView.getAdapter(); // adapter for all tags

        final EditText tagValReq = new EditText(this);
        tagValReq.setInputType(InputType.TYPE_CLASS_TEXT);
        tagValReq.setFocusableInTouchMode(true);
        tagValReq.requestFocus();
        // initialize for now
        final Tag tagNew = new Tag("person", "");

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(tagValReq);
        builder.setSingleChoiceItems(R.array.tag_types, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tagNew.setName(which == 0 ? "person" : "location");
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tagNewValStr = tagValReq.getText().toString();
                        if (tagNewValStr.isEmpty()) {
                            // Show an error message if the tag value is empty
                            new AlertDialog.Builder(builder.getContext())
                                    .setMessage("Tag value empty. Please add tag value before proceeding.")
                                    .setPositiveButton("OK", null)
                                    .show();
                            return;
                        }

                        tagNew.setValue(tagNewValStr);
                        if (!tagExists(tagNew, adapter, albums, builder)) {
                            // Notify the adapter of the data change
                            photo.getTags().add(tagNew);
                            adapter.notifyDataSetChanged();
                            Helper.saveData(albums, path);
                            listView.deferNotifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Tag successfully added. Please quit and re-open app to view changes.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }


    public boolean tagExists(Tag tagNew, Adapter adapter, ArrayList<Album> albums, AlertDialog.Builder builder) {
        for (int index = 0; index < adapter.getCount(); index++){
            if (tagNew.equals(adapter.getItem(index))) {
                new AlertDialog.Builder(builder.getContext())
                        .setMessage("Tag already exists.")
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
        }
        return false;
    }

    public void removeTag(View view) {
        final ArrayAdapter<Tag> adapter = (ArrayAdapter<Tag>) listView.getAdapter();
        // CASE PHOTO HAS NO TAGS
        if (adapter.getCount() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("This photo does not have any tags.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        final int selectedTagPos = listView.getCheckedItemPosition();
        if (selectedTagPos == -1) {
            new AlertDialog.Builder(this)
                    .setMessage("Please select tag to delete")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        final Tag selectedTag = adapter.getItem(selectedTagPos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove " + selectedTag.toString() + " ?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        adapter.remove(selectedTag);
                        Helper.saveData(albums, path);
                        listView.setItemChecked(selectedTagPos, true);
                        Toast.makeText(getApplicationContext(), "Tag successfully removed. Please quit and re-open app to view changes.", Toast.LENGTH_LONG).show();
                    }
                });

        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
        Helper.saveData(albums, path);
    }

    public void nextPhoto(View view) {
        int lastIndex = album.getPhotoCount() - 1;

        if (photoPos < lastIndex) {
            photoPos = photoPos + 1;
        } else {
            photoPos = 0;
        }

        photo = album.getPhotos().get(photoPos);
        imageView.setImageBitmap(photo.getBitmap());
        ArrayAdapter<Tag> adapter = new ArrayAdapter<>(this, R.layout.album_view, photo.getTags());
        listView.setAdapter(adapter);
        listView.setItemChecked(0, true);
    }
    public void previousPhoto(View view) {

        if (photoPos > 0) {
            photoPos = photoPos-1;
        } else {
            photoPos = album.getPhotoCount()-1;
        }

        // NOW, GET THE PREV PHOTO
        photo = album.getPhotos().get(photoPos);
        imageView.setImageBitmap(photo.getBitmap());
        ArrayAdapter<Tag> adapter = new ArrayAdapter<>(this, R.layout.album_view, photo.getTags());
        listView.setAdapter(adapter);
        listView.setItemChecked(0, true);
    }

}