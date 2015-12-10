package com.m87.xchange.xchange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class SigninFragent extends Fragment {

    private static final int PICK_IMAGE = 1;
    private SigninListener mListener;
    private EditText edit_username;
    private EditText edit_phoneNumber;
    private EditText edit_email;
    private Button gallery_button;
    private Context context;
    private ViewGroup mRootView;
    Bitmap bitmapImage;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private Uri fileUri;
    byte[] encodedImage;


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
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_signin_fragent, container, false);
        TextView title = (TextView) mRootView.findViewById(R.id.title);
        Typeface custom_font = Typeface.createFromAsset(this.context.getAssets(), "JLSDataGothicR_NC.otf");
        title.setTypeface(custom_font);

        edit_username = (EditText)mRootView.findViewById(R.id.signin_text);
        edit_phoneNumber = (EditText)mRootView.findViewById(R.id.phone_number_enter);
        edit_email = (EditText) mRootView.findViewById(R.id.email_enter);
        gallery_button = (Button) mRootView.findViewById(R.id.upload_bc);
        gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To do this, go to AndroidManifest.xml to add permission
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_IMAGE);

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
        public void onSignedIn(String username, String phone_number, String email, byte[] encodedImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            // User had pick an image.

            String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            // Link to the image

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imageFilePath = cursor.getString(columnIndex);
            cursor.close();
            System.out.println(imageFilePath);
            // Bitmap imaged created and show thumbnail

            ImageView imgView = (ImageView) mRootView.findViewById(R.id.thumbnail);
            bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            this.encodedImage = b;
            // Enable the upload button once image has been uploaded




        }
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(context, "Image Uploaded", Toast.LENGTH_LONG).show();
                Log.e("File URI", fileUri.getPath());
                String imageFilePath = fileUri.getPath();
                bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(context, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }

        Button submit_buton = (Button)mRootView.findViewById(R.id.submit_signin);
        submit_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String username = edit_username.getText().toString();
                    String phone_number = edit_phoneNumber.getText().toString();
                    String email = edit_email.getText().toString();
                    if (username.equals(null) || username.equals("") ||
                            phone_number.equals(null) || phone_number.equals("") ||
                            email.equals(null) || email.equals("")) {
                        throw new IllegalArgumentException();
                    } else {
                        mListener.onSignedIn(username, phone_number, email, encodedImage);
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

    }

}
