package com.m87.xchange.xchange;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.m87.sdk.M87Action;
import com.m87.sdk.M87Callbacks;
import com.m87.sdk.M87Event;
import com.m87.sdk.M87NearEntry;
import com.m87.sdk.M87NearEntryState;
import com.m87.sdk.M87NearMsgEntry;
import com.m87.sdk.M87StatusCode;

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

    public static M87Api mApi;
    public static int myID = 7;
    //public static int myID = 17;

    public static String USER_NAME;
    public static String USER_PHONE_NUMBER;
    public static String USER_EMAIL;

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
                mNearbyFragment.neighborList.add(n);
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
        Log.d("KHL","Deleted user data");
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

}

