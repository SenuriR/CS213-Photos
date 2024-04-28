package photoapp85;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        path = this.getApplicationInfo().dataDir + "/data.dat";
        Intent intent = getIntent();
        albums = (ArrayList<Album>) intent.getSerializableExtra("albums");
        // GET PHOTO INFORMATION
        photoPos = intent.getIntExtra("photoPos", 0);
        photo = album.getPhotos().get(photoPos);
        // GET ALBUM INFORMATION
        albumPos = intent.getIntExtra("albumPos", 0);
        album = albums.get(albumPos);

        // INITIALIZE STUFF FOR LISTVIEW OF PHOTO TAGS
        ArrayAdapter<Tag> adapter = new ArrayAdapter<>(this, R.layout.album_view, photo.getTags());
        adapter.setNotifyOnChange(true);

        listView = findViewById(R.id.listView);
        imageView = findViewById(R.id.photo);

        listView.setItemChecked(0, true);
        listView.setAdapter(adapter);
        imageView.setImageBitmap(photo.getBitmap());
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
        final ArrayAdapter<Tag> adapter = (ArrayAdapter<Tag>) listView.getAdapter();

        final EditText tagValReq = new EditText(this);
        tagValReq.setInputType(InputType.TYPE_CLASS_TEXT);
        tagValReq.setFocusableInTouchMode(true);
        tagValReq.requestFocus();

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
                        tagNew.setValue(tagNewValStr);
                        checkAdd(tagNew, adapter, albums, builder);
                        adapter.add(tagNew);
                        Helper.saveData(albums, path);
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
        Helper.saveData(albums, path);
    }
    public void checkAdd(Tag tagNew, Adapter adapter, ArrayList<Album> albums, AlertDialog.Builder builder) {
        for (int index = 0; index < adapter.getCount(); index++){
            if (tagNew.equals(adapter.getItem(index))) {
                new AlertDialog.Builder(builder.getContext())
                        .setMessage("Tag already exists.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
        }
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