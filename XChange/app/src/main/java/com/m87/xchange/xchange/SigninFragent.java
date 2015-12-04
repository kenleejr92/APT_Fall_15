package com.m87.xchange.xchange;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SigninFragent extends Fragment {

    private SigninListener mListener;
    private EditText edit_username;
    private EditText edit_phoneNumber;
    private EditText edit_email;
    private Context context;

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
        TextView title = (TextView) mRootView.findViewById(R.id.title);
        Typeface custom_font = Typeface.createFromAsset(this.context.getAssets(), "JLSDataGothicR_NC.otf");
        title.setTypeface(custom_font);

        edit_username = (EditText)mRootView.findViewById(R.id.signin_text);
        edit_phoneNumber = (EditText)mRootView.findViewById(R.id.phone_number_enter);
        edit_email = (EditText) mRootView.findViewById(R.id.email_enter);

        Button submit_buton = (Button)mRootView.findViewById(R.id.submit_signin);
        submit_buton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    String username = edit_username.getText().toString();
                    String phone_number = edit_phoneNumber.getText().toString();
                    String email = edit_email.getText().toString();
                    if(username.equals(null)|| username.equals("") ||
                            phone_number.equals(null) || phone_number.equals("") ||
                            email.equals(null) || email.equals("")){
                        throw new IllegalArgumentException();
                    }else{
                        mListener.onSignedIn(username,phone_number,email);
                    }
                } catch (IllegalArgumentException e) {
                    Context context = getActivity();
                    CharSequence text = "Incomplete Form";
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
            this.context = getActivity().getApplicationContext();
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
        public void onSignedIn(String username, String phone_number, String email);
    }

}
