package com.montassarselmi.dooreye.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.montassarselmi.dooreye.Model.User;
import com.montassarselmi.dooreye.R;
import com.montassarselmi.dooreye.Utils.DividerItemDecoration;
import com.montassarselmi.dooreye.Utils.FamilyRecyclerViewAdapter;
import com.montassarselmi.dooreye.Utils.RecyclerViewMargin;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MembersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MembersFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public MembersFragment() {
        // Required empty public constructor
    }
    public static MembersFragment newInstance(String param1, String param2) {
        MembersFragment fragment = new MembersFragment();
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
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mBoxUsersRef;
    private String boxId;
    private FirebaseAuth mAuth;

    private FamilyRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_members, container, false);
        mSharedPreferences = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        boxId = mSharedPreferences.getString("BOX_ID","NULL");
        mAuth = FirebaseAuth.getInstance();
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_cyclic);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_family);
        mRecyclerView.setVisibility(View.GONE);
        mDataSet = new ArrayList<User>();
        loadData();
        initRecyclerView();



        return view;
    }

    //initiate Recycler View
    private  void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Item Decorator:
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(ResourcesCompat.getDrawable(getResources(), R.drawable.divider, null)));
        //mRecyclerView.setItemAnimator(new FadeInLeftAnimator());
        //creating adapter object
        mAdapter = new FamilyRecyclerViewAdapter(getContext(), mDataSet);
        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        ((FamilyRecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);

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

                    if (user.getStatus() != null) {
                        if (user.getStatus().equals("admin"))
                        {mDataSet.add(0, user);}
                        if (user.getStatus().equals("user"))
                        {       mDataSet.add(user);}
                    }
                }
              //  Log.d(TAG, ""+mDataSet.get(0).getProfileImage());
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }
}
