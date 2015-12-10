package com.m87.xchange.xchange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.m87.sdk.M87NearEntry;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    public ArrayList<Contact> contactHistory;
    public static ArrayAdapter historyListAdapter;
    private ListView historyListView;
    private Context context;
    public HistoryListener mHistoryListener;
    public BCListener mBCListener;

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /// Inflate the layout for this fragment
        ViewGroup mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_history, null);
        historyListAdapter = new RouteArrayAdapter(this.context, contactHistory);
        historyListView = (ListView) mRootView.findViewById(R.id.history_listview);
        historyListView.setAdapter(historyListAdapter);

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.context = getActivity().getApplicationContext();
            mBCListener = (BCListener) activity;
            mHistoryListener = (HistoryListener) activity;
            contactHistory = (ArrayList) MainActivity.databaseHandler.getAllContacts();
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
        public ArrayList historyTable;

        public RouteArrayAdapter(Context context, ArrayList historyTable) {
            super(context, R.layout.history_table, historyTable);
            this.context = context;
            this.historyTable = historyTable;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            if (convertView == null) {
                rowView = inflater.inflate(R.layout.history_table, parent, false);
            }
            else rowView = convertView;

            Contact contact = (Contact) this.historyTable.get(position);
            TextView id = (TextView) rowView.findViewById(R.id.device_id);
            if(id!=null){
                id.setText(contact.getName());
            }
            ImageButton callButton = (ImageButton) rowView.findViewById(R.id.call);
            ImageButton textButton = (ImageButton) rowView.findViewById(R.id.text);
            ImageButton emailButton = (ImageButton) rowView.findViewById(R.id.email);
            ImageButton businessCard = (ImageButton) rowView.findViewById(R.id.view_bc);
            callButton.setOnClickListener(new View.OnClickListener() {
                private Contact contact;
                @Override
                public void onClick(View v) {
                    mHistoryListener.onCallPressed(contact);
                }
                private View.OnClickListener init(Contact c){
                    this.contact = c;
                    return this;
                }
            }.init(contact) );

            textButton.setOnClickListener(new View.OnClickListener() {
                private Contact contact;
                @Override
                public void onClick(View v) {
                    mHistoryListener.onTextPressed(contact);
                }
                private View.OnClickListener init(Contact c){
                    this.contact = c;
                    return this;
                }
            }.init(contact) );

            emailButton.setOnClickListener(new View.OnClickListener() {
                private Contact contact;
                @Override
                public void onClick(View v) {
                    mHistoryListener.onEmailPressed(contact);
                }
                private View.OnClickListener init(Contact c){
                    this.contact = c;
                    return this;
                }
            }.init(contact) );

            businessCard.setOnClickListener(new View.OnClickListener() {
                private Contact contact;
                @Override
                public void onClick(View v) {
                    mBCListener.onBCPressed(contact.getName());
                }
                private View.OnClickListener init(Contact c){
                    this.contact = c;
                    return this;
                }
            }.init(contact) );


            return rowView;
        }
    }

    //Implemented by MainActivity
    public interface HistoryListener {
        public void onCallPressed(Contact c);
        public void onTextPressed(Contact c);
        public void onEmailPressed(Contact c);
    }
}
