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

import com.daimajia.swipe.util.Attributes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.montassarselmi.dooreye.Model.User;
import com.montassarselmi.dooreye.R;
import com.montassarselmi.dooreye.Utils.RecyclerViewMargin;
import com.montassarselmi.dooreye.Utils.RequestRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "RequestsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ArrayList<User> mDataSet;
    private RecyclerView mRecyclerView;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    private String pNumber;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mBoxUsersRef;
    private String boxId;
    private RequestRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView txtNoRequests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_requests, container, false);
        mSharedPreferences = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        boxId = mSharedPreferences.getString("BOX_ID","NULL");
        Log.d(TAG, "BOX_ID: "+boxId);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_cyclic);
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_requests);
        mRecyclerView.setVisibility(View.GONE);
        txtNoRequests = (TextView) view.findViewById(R.id.txt_no_requests);
        txtNoRequests.setVisibility(View.GONE);
        mDataSet = new ArrayList<User>();
        loadData();
        initRecyclerView();



        return view;
    }

    public int getItemCount() {
        return mDataSet.size(); // Where mDataSet is the list of your items
    }
    //initiate Recycler View
    private  void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Item Decorator:
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(ResourcesCompat.getDrawable(getResources(), R.drawable.divider, null)));
        //mRecyclerView.setItemAnimator(new FadeInLeftAnimator());
        //creating adapter object
        mAdapter = new RequestRecyclerViewAdapter(getContext(), mDataSet);
        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        ((RequestRecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);

        RecyclerViewMargin decoration = new RecyclerViewMargin(16, 10);

        mRecyclerView.addItemDecoration(decoration);

        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("RecyclerView", "onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    // load initial data
    private void loadData() {
        mBoxUsersRef = database.getReference("BoxList").child(boxId).child("users");
        mBoxUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d(TAG, "" + dataSnapshot.toString());
                    User user;
                    user = data.getValue(User.class);
                        if (user.getStatus() != null && user.getStatus().equals("waiting")) {
                                mDataSet.add(user);

                    }


                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
                if (mDataSet.size() > 0)
                mRecyclerView.setVisibility(View.VISIBLE);
                else txtNoRequests.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
