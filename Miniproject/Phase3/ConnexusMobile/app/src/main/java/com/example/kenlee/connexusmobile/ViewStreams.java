package com.example.kenlee.connexusmobile;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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


public class ViewStreams extends ActionBarActivity {
    Context context = this;
    private String TAG  = "Display Streams";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_streams);
        final String request_url = "http://apt15connexus.appspot.com/view_streams_mobile";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> imageURLs = new ArrayList<String>();
                final ArrayList<String> imageCaps = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray stream_names = jObject.getJSONArray("stream_names");
                    JSONArray image_urls = jObject.getJSONArray("image_urls");
                    //JSONArray displayCaption = jObject.getJSONArray("imageCaptionList");

                    for (int i = 0; i < stream_names.length(); i++) {

                        imageURLs.add(image_urls.getString(i));
                        imageCaps.add(stream_names.getString(i));
                        System.out.println(image_urls.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new KenAdapter(ViewStreams.this, context, imageURLs, imageCaps));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            //start view photos activity
                            Intent intent = new Intent(context, ViewPhotos.class);
                            String stream_name = imageCaps.get(position);
                            intent.putExtra("stream_name", stream_name);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });

        if(MainActivity.email!=null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.view_streams_layout);
            Button mySubscribed = new Button(this);
            mySubscribed.setText(R.string.subscribed_streams);
            mySubscribed.setLayoutParams(new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(mySubscribed);
            mySubscribed.setClickable(true);
            mySubscribed.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SubscribedStreams.class);
                            startActivity(intent);
                        }
                    }
            );
            setContentView(layout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_streams, menu);
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

    public void searchResults(View view){
        EditText searchbox = (EditText) findViewById(R.id.search_text);
        String query_string = searchbox.getText().toString();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("query_string",query_string);
        startActivity(intent);
    }

    public void nearbyPhotos(View view){
        Intent intent = new Intent(this, NearbyPhotos.class);
        startActivity(intent);
    }

}
