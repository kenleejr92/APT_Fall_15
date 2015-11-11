package com.m87.xchange.xchange;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class HomeScreenFragment extends Fragment {

    private HomeScreenListener mListener;

    public static HomeScreenFragment newInstance(String param1, String param2) {
        HomeScreenFragment fragment = new HomeScreenFragment();
        return fragment;
    }

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HomeScreenListener) activity;
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

        // Inflate the layout for this fragment
        ViewGroup mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_screen, null);

        Button nearby_button = (Button) mRootView.findViewById(R.id.nearby_button);
        Button pending_button = (Button) mRootView.findViewById(R.id.pending_button);

        nearby_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onNearbyPressed();
            }
        });

        pending_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mListener.onPendingPressed();
            }
        });

        return mRootView;
    }



    //Implemented by MainActivity
    public interface HomeScreenListener {
        public void onPendingPressed();
        public void onNearbyPressed();
    }

}

