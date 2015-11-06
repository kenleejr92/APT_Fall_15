package com.m87.xchange.xchange;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.m87.sdk.M87Action;
import com.m87.sdk.M87Callbacks;
import com.m87.sdk.M87Event;
import com.m87.sdk.M87NearEntry;
import com.m87.sdk.M87NearEntryState;
import com.m87.sdk.M87NearMsgEntry;
import com.m87.sdk.M87StatusCode;

import java.util.HashSet;
import java.util.Set;


public class NearbyFragment extends Fragment {
    private NearbyListener mListener;

    public static NearbyFragment newInstance(String param1, String param2) {
        NearbyFragment fragment = new NearbyFragment();
        return fragment;
    }

    public NearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NearbyListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_nearby, null);
        // Inflate the layout for this fragment
        return mRootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSendContacts();
        }
    }


    //Implemented by Main Activity
    public interface NearbyListener {
        public void onSendContacts();
    }

}


