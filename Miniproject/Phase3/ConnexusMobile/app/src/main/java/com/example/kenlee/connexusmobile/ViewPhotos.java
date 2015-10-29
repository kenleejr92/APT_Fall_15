package com.example.kenlee.connexusmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ViewPhotos extends ActionBarActivity {
    Context context = this;
    private String TAG  = "Display Photos";
    private String stream_name;
    public String owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);
        Intent intent = getIntent();
        stream_name = intent.getExtras().getString("stream_name");
        final String request_url = "http://apt15connexus.appspot.com/view_photos_mobile/?stream_name=" + stream_name;


        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> imageURLs = new ArrayList<String>();
                final ArrayList<String> imageCaps = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    //JSONArray stream_names = jObject.getJSONArray("stream_names");
                    JSONArray image_urls = jObject.getJSONArray("image_urls");
                    JSONArray user = jObject.getJSONArray("user_email");
                    owner = user.get(0).toString();

                    //JSONArray displayCaption = jObject.getJSONArray("imageCaptionList");

                    for (int i = 0; i < image_urls.length(); i++) {

                        imageURLs.add(image_urls.getString(i));
                        imageCaps.add("Photo");
                        System.out.println(image_urls.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new KenAdapter(ViewPhotos.this, context, imageURLs, imageCaps));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            // Toast.makeText(context, imageCaps.get(position), Toast.LENGTH_SHORT).show();

                            Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);
                            Picasso.with(context).load(imageURLs.get(position)).into(image);
                            imageDialog.show();
                        }
                    });
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
//                Log.e("User", owner);
//                Log.e("Front-end User", MainActivity.email);
//                Log.e("IsEqual", String.valueOf(MainActivity.email.equals(owner)));
                if(MainActivity.email!=null){
                    if (MainActivity.email.equals(owner)) {
                        LinearLayout layout = (LinearLayout) findViewById(R.id.view_photo_layout);
                        Button uploadButton = new Button(context);
                        uploadButton.setText(R.string.uploadImage);
                        uploadButton.setLayoutParams(new ActionBar.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        layout.addView(uploadButton);
                        uploadButton.setClickable(true);
                        uploadButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ImageUpload.class);
                                        intent.putExtra("stream_name", stream_name);
                                        startActivity(intent);
                                    }
                                }
                        );
                        setContentView(layout);
                    }
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });

}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
