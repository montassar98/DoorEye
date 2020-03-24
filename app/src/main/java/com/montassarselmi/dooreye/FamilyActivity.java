package com.montassarselmi.dooreye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.daimajia.swipe.util.Attributes;
import com.montassarselmi.dooreye.Model.User;
import com.montassarselmi.dooreye.Utils.FamilyRecyclerViewAdapter;

import java.util.ArrayList;

public class FamilyActivity extends AppCompatActivity {

    private ArrayList<User> mDataSet;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_family);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Item Decorator:
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        // mRecyclerView.setItemAnimator(new FadeInLeftAnimator());
        mDataSet = new ArrayList<User>();
        loadData();
        //creating adapter object
        FamilyRecyclerViewAdapter mAdapter = new FamilyRecyclerViewAdapter(this, mDataSet);
        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        ((FamilyRecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);

        mRecyclerView.setAdapter(mAdapter);

        /* Scroll Listeners */
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
            for (int i = 0; i <= 20; i++) {
                mDataSet.add(new User("Hsin","+216 96 85 74 12","hsin@gmail.com","tatata"));
            }
    }
}
