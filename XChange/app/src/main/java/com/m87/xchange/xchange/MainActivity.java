package com.m87.xchange.xchange;

import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.m87.sdk.M87Action;
import com.m87.sdk.M87Callbacks;
import com.m87.sdk.M87Event;
import com.m87.sdk.M87NearEntry;
import com.m87.sdk.M87NearEntryState;
import com.m87.sdk.M87NearMsgEntry;
import com.m87.sdk.M87StatusCode;

import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends Activity {
    public M87Api mApi;
    public EditText e1;
    public Button sb;
    public TextView tv;
    public String message;


    private class XChangeCallbacks implements M87Callbacks
    {
        @Override
        public void onSuccess(M87Action a, String message)
        {
            if (a == M87Action.INIT)
            {
                mApi.nearSubscribe(0, null, null);
                mApi.nearPublish(0, null, 0, 17);

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

            }
        }

        public void onNearMsgEntry(M87NearMsgEntry obj, M87NearEntryState state)
        {
            if (obj == null || obj.srcId() == obj.dstId())
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
            Log.d("KHL","obj.dstID: " + obj.dstId() + "obj.srcID:" + obj.srcId() + "obj.msg:" + obj.msg());
            tv.setText("obj.dstID: " + obj.dstId() + "obj.srcID:" + obj.srcId() + "obj.msg:" + obj.msg());
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApi = new M87Api(this, new XChangeCallbacks());
        mApi.initialize(this);

        e1 = (EditText)findViewById(R.id.text_id);
        sb = (Button)findViewById(R.id.send_id);
        tv = (TextView)findViewById(R.id.response_id);
        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("KHL","Message Sent");
                message = e1.getText().toString();
                mApi.nearMsgSend(7,message);
                //mApi.nearMsgBroadcast(message);
            }
        });


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
}

