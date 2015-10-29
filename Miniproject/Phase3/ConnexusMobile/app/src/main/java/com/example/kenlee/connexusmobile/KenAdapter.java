package com.example.kenlee.connexusmobile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class KenAdapter extends BaseAdapter {
    private Activity mActivity;
    private Context mContext;
    private ArrayList<String> imageURLs;
    private ArrayList<String> stream_names;

    public KenAdapter(Activity a, Context c, ArrayList<String> imageURLs, ArrayList<String> stream_names) {
        mActivity = a;
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

    public static class ViewHolder
    {
        public ImageView imgViewPic;
        public TextView txtViewTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        LayoutInflater inflator = mActivity.getLayoutInflater();

        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.layout_ken, null);

            view.txtViewTitle = (TextView) convertView.findViewById(R.id.text_ken);
            view.imgViewPic = (ImageView) convertView.findViewById(R.id.image_ken);

            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }

        view.txtViewTitle.setText(stream_names.get(position));
        //view.imgViewPic.setImageResource(imageURLs.get(position));
        Picasso.with(mContext).load(imageURLs.get(position)).into(view.imgViewPic);

        return convertView;
    }
}