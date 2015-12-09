package com.m87.xchange.xchange;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchFragment extends Fragment {
    public static ArrayList<Contact> searchResults;
    public Context context;
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, null);
        ImageButton searchButton = (ImageButton) mRootView.findViewById(R.id.imageButton);
        final EditText searchText = (EditText) mRootView.findViewById(R.id.search_query);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nearbyString = "";
                String queryString = searchText.getText().toString();
                for(M87NameEntry n:NearbyFragment.neighborList){
                    nearbyString.concat(" " + n.id());
                }
                //send request to server
                getSearchResults(nearbyString,queryString);
            }
        });
        return mRootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.context = getActivity().getApplicationContext();
            searchResults = new ArrayList<>();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void getSearchResults(String nearbyString, String queryString) {
        RequestParams params = new RequestParams();
        params.put("nearby_string", nearbyString);
        params.put("query_string", queryString);
        AsyncHttpClient client = new AsyncHttpClient();
        String upload_url = "http://xchange-1132.appspot.com/search_nearby";
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            final ArrayList<String> imageURLs = new ArrayList<String>();
            final ArrayList<String> imageCaps = new ArrayList<String>();
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray names = jObject.getJSONArray("names");
                    JSONArray phone_numbers = jObject.getJSONArray("phone_numbers");
                    JSONArray emails = jObject.getJSONArray("emails");
                    for(int i=0; i<names.length(); i++){
                        searchResults.add(new Contact(names.getString(i),phone_numbers.getString(i),emails.getString(i)));
                    }
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                //Toast.makeText(context, "Upload Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
