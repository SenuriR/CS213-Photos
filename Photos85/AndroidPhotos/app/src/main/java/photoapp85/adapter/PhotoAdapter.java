package photoapp85.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import com.example.androidphotos.R;
import photoapp85.model.Photo;
public class PhotoAdapter extends ArrayAdapter<Photo> {

    private Context context;

    public PhotoAdapter(Context context, int resourceId, List<Photo> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView photo;
        TextView caption;
    }

    // STILL BUGGY FOR SOME REASON
public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    Photo photo = getItem(position);

    if (convertView == null) {
        // Only inflate and find views when convertView is null
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.photo_view, parent, false);
        holder = new ViewHolder();
        holder.caption = (TextView) convertView.findViewById(R.id.caption);
        holder.photo = (ImageView) convertView.findViewById(R.id.photo);
        convertView.setTag(holder);
    } else {
        holder = (ViewHolder) convertView.getTag();
    }

    // Set the caption and image only if they are not null
    if (photo != null) {
        if (photo.getCaption() != null) {
            holder.caption.setText(photo.getCaption());
        }
        if (photo.getBitmap() != null) {
            holder.photo.setImageBitmap(photo.getBitmap());
        }
    }

    return convertView;
}



}
