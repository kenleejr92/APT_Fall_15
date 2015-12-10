package com.m87.xchange.xchange;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.net.URL;



public class ShowBusinessCard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "user_name";
    private String name;
    private ViewGroup mRootView;
    private ImageView businessCard;
    public static DownloadImageTask dit;

    public static ShowBusinessCard newInstance(String name) {
        ShowBusinessCard fragment = new ShowBusinessCard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        fragment.setArguments(args);
        return fragment;
    }

    public ShowBusinessCard() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_show_business_card, container, false);
        businessCard = (ImageView) mRootView.findViewById(R.id.business_card);
        RequestParams params = new RequestParams();
        params.put("user_name", name);
        AsyncHttpClient client = new AsyncHttpClient();
        String request_url = "http://xchange-1132.appspot.com/get_businesscard";
        client.post(request_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    String url_string = jObject.getString("bc_url");
                    dit = new DownloadImageTask();
                    dit.execute(new String[]{url_string});
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                //Toast.makeText(context, "Upload Unsuccessful", Toast.LENGTH_SHORT).show();
            }


        });
        return mRootView;
    }

    private class DownloadImageTask extends AsyncTask {
        protected Bitmap doInBackground(String url_string) {
            try{
                URL url = new URL(url_string);
                System.out.println(url_string);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                if(bmp==null)dit.cancel(true);
                return bmp;
            } catch (Exception e){
                System.out.println(e);
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            System.out.println("Setting image view");
            businessCard.setImageBitmap(result);
            if(result==null) {
                dit.cancel(true);
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try{
                URL url = new URL(params[0].toString());
                System.out.println(params[0].toString());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                this.onPostExecute(bmp);
                return bmp;
            } catch (Exception e){
                System.out.println(e);
                return null;
            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dit.cancel(true);
        //businessCard.setImageBitmap(null);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(dit!=null){
            System.out.println("cancelling async");
            dit.cancel(true);
        }
    }



}
