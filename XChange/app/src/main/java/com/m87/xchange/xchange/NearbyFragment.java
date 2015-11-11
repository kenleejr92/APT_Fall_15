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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class NearbyFragment extends Fragment {

    private NearbyListener mListener;
    public static ArrayList<M87NearEntry> neighborList = new ArrayList<M87NearEntry>();
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
            mListener = (NearbyListener) activity;
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
        neighborListView.setClickable(true);
        neighborListView.setOnItemClickListener(new neighborClickListener());
        return mRootView;
    }

    public class neighborClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parentView, View childView, int position, long id) {
            Log.d("KHL","Selected a nearby entry");
        }
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

    public void display()
    {
        neighborListAdapter.notifyDataSetChanged();
    }

    public class RouteArrayAdapter extends ArrayAdapter<M87NearEntry> {
        private Context context;
        private ArrayList routingTable;

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

            M87NearEntry n = (M87NearEntry) this.routingTable.get(position);
            Log.d("KHL", String.valueOf(n.id()));
            TextView id = (TextView) rowView.findViewById(R.id.device_id);
            if(id!=null){
                id.setText(String.valueOf(n.id()));
            }


            return rowView;
        }
    }
}


