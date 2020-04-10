package com.montassarselmi.dooreye.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.montassarselmi.dooreye.Model.EventHistory;
import com.montassarselmi.dooreye.Model.Live;
import com.montassarselmi.dooreye.Model.Motion;
import com.montassarselmi.dooreye.Model.Ring;
import com.montassarselmi.dooreye.R;
import com.montassarselmi.dooreye.Utils.CustomComparator;
import com.montassarselmi.dooreye.Utils.RecyclerViewAllHistoryAdapter;
import com.montassarselmi.dooreye.Utils.RecyclerViewMargin;

import java.util.ArrayList;
import java.util.Collections;


public class LiveFragment extends Fragment {

    private ArrayList<EventHistory> mDataSet;
    private RecyclerView mRecyclerView;
    private RecyclerViewAllHistoryAdapter mAdapter;
    private ProgressBar mProgressBar;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mBoxUsersRef,mBoxHistory;
    private String boxId;
    private FirebaseAuth mAuth;
    private String TAG= "LiveFragment";
    private TextView txtNoLives;



    public LiveFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        boxId = mSharedPreferences.getString("BOX_ID","NULL");
        mAuth = FirebaseAuth.getInstance();
        mBoxUsersRef = database.getReference("BoxList").child(boxId).child("users").child(mAuth.getUid());
        mBoxHistory = database.getReference("BoxList").child(boxId).child("history");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_live, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_cyclic);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_live_history);
        mRecyclerView.setVisibility(View.VISIBLE);
        txtNoLives = (TextView) view.findViewById(R.id.txt_no_lives);
        txtNoLives.setVisibility(View.GONE);
        mDataSet = new ArrayList<EventHistory>();
        loadData();
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerViewAllHistoryAdapter(getContext(), mDataSet);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData() {
            mBoxHistory.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("live")) {
                        for (DataSnapshot data : dataSnapshot.child("live").getChildren()) {
                            Log.d(TAG, "" + dataSnapshot.child("live").toString());
                            Live live;
                            live = data.getValue(Live.class);
                            live.setupIcon(live.getStatus());
                            mDataSet.add(live);
                        }
                    }
                    Collections.sort(mDataSet, new CustomComparator());
                    Collections.reverse(mDataSet);
                    mAdapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.GONE);
                    if (mDataSet.size() > 0)
                        mRecyclerView.setVisibility(View.VISIBLE);
                    else txtNoLives.setVisibility(View.VISIBLE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

}
