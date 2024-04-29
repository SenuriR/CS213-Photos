package photoapp85;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileDescriptor;
import java.util.ArrayList;
import com.example.androidphotos.R;
import photoapp85.adapter.PhotoAdapter;
import photoapp85.model.Album;
import photoapp85.model.Photo;
import photoapp85.util.Helper;


public class AlbumActivity extends AppCompatActivity {
    private String path;
    private int albumPos = 0;
    private ArrayList<Album> albums;
    private Album currAlbum;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        path = this.getApplicationInfo().dataDir + "/data.dat";
        Intent intent = getIntent();
        albums = (ArrayList<Album>) intent.getSerializableExtra("albums");
        albumPos = intent.getIntExtra("albumPos", 0);
        currAlbum = albums.get(albumPos);
        PhotoAdapter adapter = new PhotoAdapter(this, R.layout.photo_view, currAlbum.getPhotos());
        adapter.setNotifyOnChange(true);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setItemChecked(0, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setItemChecked(position, true);
            }
        });
    }

    // HOME BUTTON
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openPhoto(View view) {

        // IF NO PHOTOS
        if (listView.getAdapter().getCount() == 0) {
            return;
        }

        // SET UP INTENT TO CALL PHOTOACTIVITY CLASS
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("albumPos", albumPos);
        intent.putExtra("photoPos", listView.getCheckedItemPosition());
        intent.putExtra("albums", albums);
        startActivity(intent);
    }

    public void addPhoto(View view) {

        // INTENT TO OPEN IMAGE FILE
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // WE NEED THIS TO ACCOUNT FOR THE ERROR OF NON-OPENABLE IMAGES
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, 42);
        Helper.saveData(albums, path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == 42) {
            if (resultCode == Activity.RESULT_OK) {
                if (resultData != null) {
                    Uri uri = resultData.getData();
                    Bitmap bitmap = null;
                    try {
                        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    String caption = uri.getLastPathSegment();
                    Photo photo = new Photo(caption, bitmap);
                    PhotoAdapter adapter = (PhotoAdapter) listView.getAdapter();
                    adapter.add(photo);
                    Helper.saveData(albums, path);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }


    public void removePhoto(View view) {
        final PhotoAdapter adapter = (PhotoAdapter) listView.getAdapter();

        // IF NO PHOTOS
        if (adapter.getCount() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("This selected album does not have any photos.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        if (listView.getCheckedItemPosition() == -1) {
            new AlertDialog.Builder(this)
                    .setMessage("Please select an photo to remove.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        final int selectedPhotoPos = listView.getCheckedItemPosition();
        final Photo selectedPhoto = adapter.getItem(selectedPhotoPos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove " + selectedPhoto.getCaption() + " ?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        adapter.remove(selectedPhoto);
                        Helper.saveData(albums, path);
                        listView.setItemChecked(selectedPhotoPos, true);
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

    public void movePhoto(View view) {

        // WE NEED TO GET THE SRC AND DST ALBUM NAMES
        ArrayList<String> namesSrcDstList = new ArrayList<>();
        namesSrcDstList.add(currAlbum.getName());  // so now, namesSrcDst[0] -> src album
        for (Album album : albums) {
            if (!album.getName().equals(currAlbum.getName())) {
                namesSrcDstList.add(album.getName());
            }
        }

        // convert the namesSrcDst ARRAYLIST into an array, specifically, CharSequence[] of size namesSrcDst.size()
        final CharSequence[] namesSrcDstArray = namesSrcDstList.toArray(new CharSequence[namesSrcDstList.size()]);

        final PhotoAdapter adapter = (PhotoAdapter) listView.getAdapter();

        // designate a spot in the new CharSequence array for the dst album
        final Album dstAlbum = new Album(namesSrcDstArray[0].toString());

        if (listView.getCheckedItemPosition() == -1) {
            new AlertDialog.Builder(this)
                    .setMessage("Please select an photo to move.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(namesSrcDstArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // update selected dst name
                        dstAlbum.setName(namesSrcDstArray[which].toString());
                    }
                })
                .setPositiveButton("Move", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listView.deferNotifyDataSetChanged();
                        Photo photoToMove = adapter.getItem(listView.getCheckedItemPosition());
                        // remove photo from curr album
                        adapter.remove(photoToMove);
                        checksAndMove(albums, dstAlbum, photoToMove, builder);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
        Helper.saveData(albums, path);
        listView.deferNotifyDataSetChanged();
    }

    public void checksAndMove(ArrayList<Album> albums, Album dstAlbum, Photo photoToMove, AlertDialog.Builder builder) {
        for (Album album : albums) {
            if (album.getName().equals(dstAlbum.getName())) {
                for (Photo photo : album.getPhotos()) {
                    if (photo.equals(photoToMove)) {
                        new AlertDialog.Builder(builder.getContext())
                                .setMessage("Caption " + photoToMove.getCaption() + " already exists in " + album.getName() + " .")
                                .setPositiveButton("OK", null)
                                .show();

                        return;
                    }

                }
                album.getPhotos().add(photoToMove);
                Helper.saveData(albums, path);
            }
        }
    }
}