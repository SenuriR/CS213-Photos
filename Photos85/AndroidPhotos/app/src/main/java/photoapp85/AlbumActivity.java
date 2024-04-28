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
    private ArrayList<Album> albums;
    private Album album;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        path = this.getApplicationInfo().dataDir + "/data.dat";

        // unpack intent from MainActivty
        Intent intent = getIntent();
        album = (Album) intent.getParcelableExtra("album");
        albums = (ArrayList<Album>) intent.getParcelableExtra("albums");

        // initialize adapter and listview
        PhotoAdapter adapter = new PhotoAdapter(this, R.layout.photo_view, album.getPhotos());
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
        intent.putExtra("photo", album.getPhotos().get(listView.getCheckedItemPosition()));
        intent.putExtra("album", album);
        intent.putExtra("albums", albums);
        intent.putExtra("photoPos", listView.getCheckedItemPosition());
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

                    checkPhotoCapAlreadyExists(adapter, photo, caption);

                    // SAVE THE PHOTO TO ADAPTER
                    adapter.add(photo);

                    // UPDATE DISK
                    Helper.saveData(albums, path);
                }
            }
        }
    }

    public void checkPhotoCapAlreadyExists(PhotoAdapter adapter, Photo photo, String caption) {
        for (int index = 0; index < adapter.getCount(); index++) {
            if (photo.equals(adapter.getItem(index))) {
                new AlertDialog.Builder(this)
                        .setMessage("A photo with the caption \"" + caption + "\" already exists in this album.")
                        .setPositiveButton("OK", null)
                        .show();

                return;
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

        final int selectedPhotoPos = listView.getCheckedItemPosition();
        final Photo selectedPhoto = adapter.getItem(selectedPhotoPos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove \"" + selectedPhoto.getCaption() + "\"?");
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
        for (Album album : albums) {
            if (!album.getName().equals(album.getName())) {
                namesSrcDstList.add(album.getName()); // so now, namesSrcDst[0] -> src album
            }                
        }

        // convert the namesSrcDst ARRAYLIST into an array, specifically, CharSequence[] of size namesSrcDst.size()
        final CharSequence[] namesSrcDstArray = namesSrcDstList.toArray(new CharSequence[namesSrcDstList.size()]);

        final PhotoAdapter adapter = (PhotoAdapter) listView.getAdapter();

        // designate a spot in the new CharSequence array for the dst album
        final Album dstAlbum = new Album(namesSrcDstArray[0].toString());

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