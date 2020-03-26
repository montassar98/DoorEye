package com.montassarselmi.dooreye.Utils;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.montassarselmi.dooreye.Fragments.MembersFragment;
import com.montassarselmi.dooreye.Fragments.RequestsFragment;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();


    public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MembersFragment();
            case 1:
                return new RequestsFragment();

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}