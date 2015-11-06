package com.m87.xchange.xchange;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PendingRequestsFragment extends Fragment {

    private PendingRequestsListener mListener;

    public static PendingRequestsFragment newInstance(String param1, String param2) {
        PendingRequestsFragment fragment = new PendingRequestsFragment();
        return fragment;
    }

    public PendingRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PendingRequestsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_requests, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAddContact();
        }
    }


    //Implemented by Main Activity
    public interface PendingRequestsListener {
        public void onAddContact();
    }

}
