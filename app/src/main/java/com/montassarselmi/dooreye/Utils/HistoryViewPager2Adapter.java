package com.montassarselmi.dooreye.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.montassarselmi.dooreye.Fragments.AllFragment;
import com.montassarselmi.dooreye.Fragments.LiveFragment;
import com.montassarselmi.dooreye.Fragments.MembersFragment;
import com.montassarselmi.dooreye.Fragments.MotionsFragment;
import com.montassarselmi.dooreye.Fragments.RequestsFragment;
import com.montassarselmi.dooreye.Fragments.RingsFragment;
import com.montassarselmi.dooreye.Model.EventHistory;
import com.montassarselmi.dooreye.Model.User;

import java.util.ArrayList;


public class HistoryViewPager2Adapter extends FragmentStateAdapter {

    private Context mContext;
    private ArrayList<EventHistory> eventsList;
    private static final String TAG = HistoryViewPager2Adapter.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRefUser=database.getReference();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String boxId;

    public HistoryViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AllFragment();
            case 1:
                return new RingsFragment();
            case 2:
                return new MotionsFragment();
            case 3:
                return new LiveFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
