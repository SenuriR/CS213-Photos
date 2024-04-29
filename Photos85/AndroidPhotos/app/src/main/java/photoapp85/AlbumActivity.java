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
    private int albumPos = 0;
    private ArrayList<Album> albums;
    private Album currAlbum;
    private ListView listView;
    private Button btnChangeColor;

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

        // Initialize and set button color
        btnChangeColor = findViewById(R.id.btnChangeColor);
        changeButtonColor(btnChangeColor, R.color.my_button_color);
    }

    private void changeButtonColor(Button button, int colorResId) {
        int color = ContextCompat.getColor(this, colorResId);
        ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(this, colorResId));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openPhoto(View view) {
        if (listView.getAdapter().getCount() == 0) {
            return;
        }
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("albumPos", albumPos);
        intent.putExtra("photoPos", listView.getCheckedItemPosition());
        intent.putExtra("albums", albums);
        startActivity(intent);
    }

    public void addPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 42 && resultCode == Activity.RESULT_OK && resultData != null) {
            Uri uri = resultData.getData();
            try (ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r")) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                String caption = uri.getLastPathSegment();
                Photo photo = new Photo(caption, bitmap);
                PhotoAdapter adapter = (PhotoAdapter) listView.getAdapter();
                if (!checkPhotoCapAlreadyExists(adapter, photo, caption)) {
                    adapter.add(photo);
                    Helper.saveData(albums, path);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private boolean checkPhotoCapAlreadyExists(PhotoAdapter adapter, Photo photo, String caption) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(photo)) {
                new AlertDialog.Builder(this)
                        .setMessage("A photo with the caption \"" + caption + "\" already exists.")
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
        }
        return false;
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
                        Helper.saveData(this, albums, path);
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
    }

    public void movePhoto(View view) {

        // WE NEED TO GET THE SRC AND DST ALBUM NAMES
        ArrayList<String> namesSrcDstList = new ArrayList<>();
        for (Album album : albums) {
            if (!album.getName().equals(selectedAlbum.getName())) {
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
                Helper.saveData((DialogInterface.OnClickListener) this, albums, path);
            }
        }
    }
}
