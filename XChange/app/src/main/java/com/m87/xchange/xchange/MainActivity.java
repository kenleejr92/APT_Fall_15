package com.m87.xchange.xchange;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
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
        PendingRequestsFragment.PendingRequestsListener, NearbyFragment.NearbyListener {
    public static M87Api mApi;
    public static int myID = 7;
    //public static int myID = 17;

    public static Set<Integer> nearbyIDs;
    public static List<M87NearMsgEntry> msgList;
    Context context;

    private class XChangeCallbacks implements M87Callbacks
    {
        @Override
        public void onSuccess(M87Action a, String message)
        {
            if (a == M87Action.INIT)
            {
                mApi.nearSubscribe(0, null, null);
                mApi.nearPublish(0, null, 0, myID);
                nearbyIDs = new HashSet<>();
                msgList = new ArrayList<>();

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
            Log.d("KHL", String.valueOf(obj.id()));
            String op = "Ignoring";

            switch (state)
            {
                case SDK_NEAR_ENTRY_ADD   : op = "Adding"  ; break;
                case SDK_NEAR_ENTRY_UPDATE: op = "Updating"; break;
                case SDK_NEAR_ENTRY_DELETE: op = "Deleting"; break;
            }
            /*SamBeacon bcn = new SamBeacon(obj);

            bcn.snr = snr;
            bcn.rxPower = rxPower;
            bcn.state = state;

            SAM_DEBUG("New Entry adding to fragment");

            mBeaconsFragment.add(bcn);
            mBeaconsFragment.display();*/
        }

        public void onNearTable(M87NearEntry[] neighbors)
        {

            for (M87NearEntry n : neighbors)
            {
                nearbyIDs.add(n.id());
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
            msgList.add(obj);
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

            // Create a new Fragment to be placed in the activity layout
            HomeScreenFragment firstFragment = new HomeScreenFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
    public void onNearbyPressed(){
        // Create fragment and give it an argument specifying the article it should show
        NearbyFragment newFragment = new NearbyFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void onPendingPressed(){
        // Create fragment and give it an argument specifying the article it should show
        PendingRequestsFragment newFragment = new PendingRequestsFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
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

