package com.montassarselmi.dooreye.Utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.montassarselmi.dooreye.Fragments.AllFragment;
import com.montassarselmi.dooreye.Fragments.LiveFragment;
import com.montassarselmi.dooreye.Fragments.MembersFragment;
import com.montassarselmi.dooreye.Fragments.MotionsFragment;
import com.montassarselmi.dooreye.Fragments.RequestsFragment;
import com.montassarselmi.dooreye.Fragments.RingsFragment;


public class HistoryViewPager2Adapter extends FragmentStateAdapter {



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
