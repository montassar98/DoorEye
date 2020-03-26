package com.montassarselmi.dooreye.Fragments;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.util.Attributes;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MembersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MembersFragment.
     */
    // TODO: Rename and change types and number of parameters
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_members, container, false);
     mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_family);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Item Decorator:
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(ResourcesCompat.getDrawable(getResources(), R.drawable.divider, null)));
        //mRecyclerView.setItemAnimator(new FadeInLeftAnimator());
        mDataSet = new ArrayList<User>();
        loadData();
        //creating adapter object
        FamilyRecyclerViewAdapter mAdapter = new FamilyRecyclerViewAdapter(getContext(), mDataSet);
        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        ((FamilyRecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);

        RecyclerViewMargin decoration = new RecyclerViewMargin(8, 10);

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

        return view;
    }

    // load initial data
    private void loadData() {
        for (int i = 0; i <= 10; i++) {
            mDataSet.add(new User("Hsin","+216 96 85 74 12","hsin@gmail.com","tatata",null));
        }
    }
}
