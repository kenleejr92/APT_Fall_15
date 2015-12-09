package com.m87.xchange.xchange;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.m87.sdk.M87Action;
import com.m87.sdk.M87Callbacks;
import com.m87.sdk.M87Event;
import com.m87.sdk.M87NearEntry;
import com.m87.sdk.M87NearEntryState;
import com.m87.sdk.M87NearMsgEntry;
import com.m87.sdk.M87StatusCode;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.FileOutputStream;


public class NearbyFragment extends Fragment {
    public static ArrayList<M87NameEntry> neighborList = new ArrayList<M87NameEntry>();
    public static ArrayAdapter neighborListAdapter;
    private ListView neighborListView;
    private Context context;

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
            this.context = getActivity().getApplicationContext();
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
        neighborListAdapter = new RouteArrayAdapter(this.context, neighborList);

        neighborListView = (ListView) mRootView.findViewById(R.id.neighbor_listview);
        neighborListView.setAdapter(neighborListAdapter);
        //neighborListView.setClickable(true);
        //neighborListView.setOnItemClickListener(new neighborClickListener());
        return mRootView;
    }

//    public class neighborClickListener implements AdapterView.OnItemClickListener {
//        public void onItemClick(AdapterView parentView, View childView, int position, long id) {
//            Log.d("KHL","Selected a nearby entry");
//            //get phone number and email from server and add to contacts
//            M87NameEntry n = neighborList.get(position);
//            getContactsAndAdd(n.id());
//        }
//    }

    public void display()
    {
        neighborListAdapter.notifyDataSetChanged();
    }

    public class RouteArrayAdapter extends ArrayAdapter<M87NearEntry> {
        private Context context;
        public ArrayList routingTable;

        public RouteArrayAdapter(Context context, ArrayList routingTable) {
            super(context, R.layout.section_route_table, routingTable);
            this.context = context;
            this.routingTable = routingTable;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            if (convertView == null) {
                rowView = inflater.inflate(R.layout.section_route_table, parent, false);
            }
            else rowView = convertView;

            M87NameEntry n = (M87NameEntry) this.routingTable.get(position);
            Log.d("KHL", String.valueOf(n.id()));
            TextView id = (TextView) rowView.findViewById(R.id.device_id);
            Button addContacts = (Button) rowView.findViewById(R.id.add_contacts);
            addContacts.setOnClickListener(new View.OnClickListener() {
                int position;

                @Override
                public void onClick(View v) {
                    Log.d("KHL", "Selected a nearby entry");
                    //get phone number and email from server and add to contacts
                    M87NameEntry n = neighborList.get(position);
                    getContactsAndAdd(n.id());
                    //Write to history to SQLlite database



                }

                private View.OnClickListener init(int pos) {
                    position = pos;
                    return this;
                }
            }.init(position));

            if(id!=null){
                id.setText(n.name);
            }
            return rowView;
        }
    }

    private void getContactsAndAdd(Integer ID) {
        RequestParams params = new RequestParams();
        params.put("user_id", String.valueOf(ID));
        AsyncHttpClient client = new AsyncHttpClient();
        String upload_url = "http://xchange-1132.appspot.com/get_contacts";
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    String phone_number = jObject.getString("phone_number");
                    String email = jObject.getString("email");
                    String name = jObject.getString("user_name");
                    addToContacts(name,phone_number,email);
                    //Add to SQLite database
                    MainActivity.databaseHandler.addContact(new Contact(name,phone_number,email));
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                //Toast.makeText(context, "Upload Unsuccessful", Toast.LENGTH_SHORT).show();
            }

            public void addToContacts(String displayName, String number, String email)
            {
                Context contetx 	= context; //Application's context or Activity's context
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
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                        .build());
                //Mobile number will be inserted in ContactsContract.Data table
                cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
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
        });
    }
}


