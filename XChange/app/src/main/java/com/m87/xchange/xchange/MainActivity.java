package com.m87.xchange.xchange;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ContentResolver;
import android.database.Cursor;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.m87.sdk.M87Action;
import com.m87.sdk.M87Callbacks;
import com.m87.sdk.M87Event;
import com.m87.sdk.M87NearEntry;
import com.m87.sdk.M87NearEntryState;
import com.m87.sdk.M87NearMsgEntry;
import com.m87.sdk.M87StatusCode;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends Activity implements HomeScreenFragment.HomeScreenListener,
        PendingRequestsFragment.PendingRequestsListener, NearbyFragment.NearbyListener, SigninFragent.SigninListener{

    private HomeScreenFragment mHomeScreenFragment;
    private NearbyFragment mNearbyFragment;
    private PendingRequestsFragment mPendingRequestsFragment;
    private SigninFragent mSigninFragment;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Context context;

    public static M87Api mApi;
    //public static int myID = 7;
    //public static int myID = 17;
    //public static int myID = 35;
    public static int myID = 137;

    public static String USER_NAME;
    public static String USER_PHONE_NUMBER;
    public static String USER_EMAIL;
    public String neighbor_name;

    private class XChangeCallbacks implements M87Callbacks
    {
        @Override
        public void onSuccess(M87Action a, String message)
        {
            if (a == M87Action.INIT)
            {
                mApi.nearSubscribe(0, null, null);
                mApi.nearPublish(0, null, 0, myID);
            }
        }

        public void onFailure(M87Action a, String message, M87StatusCode code, String v)
        {
        }

        @Override
        public void onEvent(M87Event m87Event) {

        }

        public void onNearEntry(M87NearEntry obj, M87NearEntryState state)
        {
            if (obj == null)
            {
                return;
            }
            String op = "Ignoring";

            switch (state)
            {
                case SDK_NEAR_ENTRY_ADD   : op = "Adding"  ; break;
                case SDK_NEAR_ENTRY_UPDATE: op = "Updating"; break;
                case SDK_NEAR_ENTRY_DELETE: op = "Deleting"; break;
            }

            //send messsage Who? back to them
            //they send back name,
            //store in hash table and notify mNearbyFragment
        }

        public void onNearTable(M87NearEntry[] neighbors)
        {

            mNearbyFragment.neighborList.clear();
            for (M87NearEntry n : neighbors)
            {
                getNameFromID(n.id(),n);
            }
            if(mNearbyFragment!=null){
                mNearbyFragment.display();
            }

        }

        public void onNearMsgEntry(M87NearMsgEntry obj, M87NearEntryState state)
        {
            if (obj == null || obj.srcId() == myID)
            {
                return;
            }
            String op = "Ignoring";

            switch (state)
            {
                case SDK_NEAR_ENTRY_ADD   : op = "Adding"  ; break;
                case SDK_NEAR_ENTRY_UPDATE: op = "Updating"; break;
                case SDK_NEAR_ENTRY_DELETE: op = "Deleting"; break;
            }
            Log.d("KHL", "obj.dstID: " + obj.dstId() + "obj.srcID:" + obj.srcId() + "obj.msg:" + obj.msg());
        }

        public void onNearMsgTable(M87NearMsgEntry[] msgs)
        {
            for (M87NearMsgEntry n : msgs)
            {
            }
        }

        public void onNearMsgTxStatus(byte[] status)
        {

        }

        public void onNearPublishStatus(int status)
        {
        }

        public void onNearSubscribeStatus(int status)
        {
        }

        public void onNearPublishCancelStatus(int status)
        {
        }

        public void onNearSubscribeCancelStatus(int status)
        {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mApi = new M87Api(this, new XChangeCallbacks());
        mApi.initialize(this);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Restore preferences
            settings = getPreferences(MODE_PRIVATE);
            USER_NAME = settings.getString("Username","false");
            if(USER_NAME.equals("false")){
                //No username specified, render signin fragment
                mSigninFragment = new SigninFragent();
                mSigninFragment.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().add(R.id.fragment_container,mSigninFragment).commit();
            } else {
                mHomeScreenFragment = new HomeScreenFragment();
                mHomeScreenFragment.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mHomeScreenFragment).commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause(){
        super.onPause();
        /****************************For Testing************************/
        settings = getPreferences(MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
        Log.d("KHL", "Deleted user data");
        /**************************************************************/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onSignedIn(String username, String phone_number, String email){
        //Store the username
        USER_NAME = username;
        USER_PHONE_NUMBER = phone_number;
        USER_EMAIL = email;
        settings = getPreferences(MODE_PRIVATE);
        editor = settings.edit();
        editor.putString("Username", USER_NAME);
        editor.putString("Phonenumber",USER_PHONE_NUMBER);
        editor.putString("Useremail",USER_EMAIL);
        editor.commit();

        //Post data to server
        String register_url="http://xchange-1132.appspot.com/register_user";
        postToServer(register_url);
        // Create a new Fragment to be placed in the activity layout
        mHomeScreenFragment = new HomeScreenFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        mHomeScreenFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, mHomeScreenFragment).commit();
    }


    @Override
    public void onNearbyPressed(){
        // Create fragment and give it an argument specifying the article it should show
        mNearbyFragment = new NearbyFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, mNearbyFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void onPendingPressed(){
        // Create fragment and give it an argument specifying the article it should show


        mPendingRequestsFragment = new PendingRequestsFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, mPendingRequestsFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onAddContact(){

    }

    @Override
    public void onSendContacts(){

    }

    private void postToServer(String upload_url){
        RequestParams params = new RequestParams();
        params.put("user_name",USER_NAME);
        params.put("user_id",String.valueOf(myID));
        params.put("phone_number",USER_PHONE_NUMBER);
        params.put("email",USER_EMAIL);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                //Toast.makeText(context, "Upload Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNameFromID(Integer ID, M87NearEntry m87ne){
        RequestParams params = new RequestParams();
        params.put("user_id",String.valueOf(ID));
        AsyncHttpClient client = new AsyncHttpClient();
        String upload_url = "http://xchange-1132.appspot.com/request_name";
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            private M87NearEntry n;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    String name = jObject.getString("user_name");
                    M87NameEntry nameEntry = new M87NameEntry(name, n);
                    mNearbyFragment.neighborList.add(nameEntry);
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                //Toast.makeText(context, "Upload Unsuccessful", Toast.LENGTH_SHORT).show();
            }

            private AsyncHttpResponseHandler init(M87NearEntry m87ne){
                n = m87ne;
                return this;
            }
        }.init(m87ne) );

    }


}

