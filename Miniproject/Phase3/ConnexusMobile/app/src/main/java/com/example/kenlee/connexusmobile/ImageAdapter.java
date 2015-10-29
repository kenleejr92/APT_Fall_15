package com.example.kenlee.connexusmobile;

import android.app.ActionBar;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kenlee on 10/20/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> imageURLs;
    private ArrayList<String> stream_names;
    public ImageAdapter(Context c, ArrayList<String> imageURLs, ArrayList<String> stream_names) {
        mContext = c;
        this.imageURLs = imageURLs;
        this.stream_names = stream_names;
    }

    public int getCount() {
        return imageURLs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ListView listView;
        ImageView imageView;
        TextView textView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        if(!imageURLs.isEmpty()) {
            Picasso.with(mContext).load(imageURLs.get(position)).into(imageView);
        }
        return imageView;
    }
}
