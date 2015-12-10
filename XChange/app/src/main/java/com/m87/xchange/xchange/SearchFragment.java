package com.m87.xchange.xchange;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.m87.sdk.M87NearEntry;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    public static ArrayList<Contact> searchResults;
    public static ArrayAdapter searchAdapter;
    private ListView searchListView;
    public Context context;
    public BCListener mBCListener;
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, null);
        searchAdapter = new RouteArrayAdapter(this.context,searchResults);
        searchListView = (ListView) mRootView.findViewById(R.id.search_listview);

        ImageButton searchButton = (ImageButton) mRootView.findViewById(R.id.imageButton);
        final EditText searchText = (EditText) mRootView.findViewById(R.id.search_query);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nearbyString = "";
                String queryString = searchText.getText().toString();
                for(M87NameEntry n:NearbyFragment.neighborList){
                    nearbyString = nearbyString.concat(" " + n.id() + " ");
                }
                //send request to server
                getSearchResults(nearbyString,queryString);
            }
        });
        return mRootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mBCListener = (BCListener) activity;
            this.context = getActivity().getApplicationContext();
            searchResults = new ArrayList<>();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class RouteArrayAdapter extends ArrayAdapter<Contact> {
        private Context context;
        public ArrayList contactTable;

        public RouteArrayAdapter(Context context, ArrayList cTable) {
            super(context, R.layout.section_route_table, cTable);
            this.context = context;
            this.contactTable = cTable;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            if (convertView == null) {
                rowView = inflater.inflate(R.layout.section_route_table, parent, false);
            }
            else rowView = convertView;

            Contact c = (Contact) this.contactTable.get(position);
            TextView id = (TextView) rowView.findViewById(R.id.device_id);
            if(id!=null){
                id.setText(c.getName());
            }
            Button addContacts = (Button) rowView.findViewById(R.id.add_contacts);
            addContacts.setOnClickListener(new View.OnClickListener() {
                int position;

                @Override
                public void onClick(View v) {
                    Log.d("KHL", "Selected a nearby entry");
                    //get phone number and email from server and add to contacts
                    Contact c = (Contact) contactTable.get(position);
                    addToContacts(c.getName(),c.getPhoneNumber(),c.getEmail());
                }

                private View.OnClickListener init(int pos) {
                    position = pos;
                    return this;
                }
            }.init(position));

            ImageButton viewBC = (ImageButton) rowView.findViewById(R.id.xchange);
            viewBC.setOnClickListener(new View.OnClickListener() {
                int position;
                @Override
                public void onClick(View v) {
                    Contact c = (Contact) contactTable.get(position);
                    mBCListener.onBCPressed(c.getName());
                }
                private View.OnClickListener init(int pos) {
                    position = pos;
                    return this;
                }
            }.init(position));
            return rowView;
        }
    }

    private void getSearchResults(String nearbyString, String queryString) {
        RequestParams params = new RequestParams();
        Log.d("KHL","nearby_string: "+ nearbyString);
        Log.d("KHL","nearby_string: "+queryString);
        params.put("nearby_string", nearbyString);
        params.put("query_string", queryString);
        AsyncHttpClient client = new AsyncHttpClient();
        String upload_url = "http://xchange-1132.appspot.com/search_nearby";
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray names = jObject.getJSONArray("names");
                    JSONArray phone_numbers = jObject.getJSONArray("phone_numbers");
                    JSONArray emails = jObject.getJSONArray("emails");
                    for(int i=0; i<names.length(); i++){
                        searchResults.add(new Contact(names.getString(i),phone_numbers.getString(i),emails.getString(i)));
                    }
                    searchListView.setAdapter(searchAdapter);
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                Toast.makeText(context, "No Results", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addToContacts(String displayName, String number, String email)
    {
        String strDisplayName 	=  displayName; // Name of the Person to add
        String strNumber 	=  number; //number of the person to add with the Contact

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strNumber) // Number to be added
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
        //email added to contracts
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email) // Number to be added
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME).build());
        try
        {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
            Toast.makeText(context, displayName + " added to Contacts", Toast.LENGTH_SHORT).show();
        }
        catch (RemoteException exp)
        {
            //logs;
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

}
