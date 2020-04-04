package com.montassarselmi.dooreye.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.montassarselmi.dooreye.Model.EventHistory;
import com.montassarselmi.dooreye.Model.Live;
import com.montassarselmi.dooreye.Model.Motion;
import com.montassarselmi.dooreye.Model.Ring;
import com.montassarselmi.dooreye.R;
import com.montassarselmi.dooreye.Utils.RecyclerViewAllHistoryAdapter;
import com.montassarselmi.dooreye.Utils.RecyclerViewMargin;

import java.util.ArrayList;


public class LiveFragment extends Fragment {

    private ArrayList<EventHistory> mDataSet;
    private RecyclerView mRecyclerView;
    private RecyclerViewAllHistoryAdapter mAdapter;
    private ProgressBar mProgressBar;


    public LiveFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_live, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_cyclic);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_live_history);
        mRecyclerView.setVisibility(View.VISIBLE);
        mDataSet = new ArrayList<>();
        loadData();
        initRecyclerView();

        return view;
    }

    private void loadData() {
        for (int i=0; i<10; i++)
        {
            mDataSet.add(new Live(0,"4/4/2020 - 9:02 PM","Hsin"));

        }
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerViewAllHistoryAdapter(getContext(), mDataSet);

        mRecyclerView.setAdapter(mAdapter);
    }
}
