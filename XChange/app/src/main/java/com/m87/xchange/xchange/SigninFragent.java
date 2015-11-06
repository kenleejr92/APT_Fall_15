package com.m87.xchange.xchange;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SigninFragent extends Fragment {

    private SigninListener mListener;
    private EditText edit_username;

    public static SigninFragent newInstance(String param1, String param2) {
        SigninFragent fragment = new SigninFragent();
        return fragment;
    }

    public SigninFragent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_signin_fragent, container, false);
        edit_username = (EditText)mRootView.findViewById(R.id.signin_text);
        Button submit_buton = (Button)mRootView.findViewById(R.id.submit_signin);
        submit_buton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    String username = edit_username.getText().toString();
                    if(username.equals(null)||username.equals("")){
                        throw new IllegalArgumentException();
                    }else{
                        mListener.onSignedIn(username);
                    }
                } catch (IllegalArgumentException e) {
                    Context context = getActivity();
                    CharSequence text = "Must Specify a Username";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
        return mRootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SigninListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface SigninListener {
        public void onSignedIn(String username);
    }

}
