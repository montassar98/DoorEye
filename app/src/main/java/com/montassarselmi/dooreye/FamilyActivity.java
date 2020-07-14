package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.montassarselmi.dooreye.Fragments.MembersFragment;
import com.montassarselmi.dooreye.Fragments.RequestsFragment;

import com.montassarselmi.dooreye.Utils.ViewPagerFragmentAdapter;
import com.montassarselmi.dooreye.Utils.ZoomOutPageTransformer;

import java.util.ArrayList;

import static com.montassarselmi.dooreye.MainActivity.changeStatusBarToWhite;


public class FamilyActivity extends AppCompatActivity  {


    ViewPagerFragmentAdapter myAdapter;
    ArrayList<Fragment> arrayList = new ArrayList<>();
    String[] tabNames = {"Members","Requests"};
    int[] tabIconsEnabled = {R.drawable.ic_members_enabled,R.drawable.ic_requests_enabled};
    int[] tabIconsDisabled = {R.drawable.ic_members_disabled,R.drawable.ic_requests_disabled};
    ViewPager2 myViewPager2;
    public static boolean isActivityRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        changeStatusBarToWhite(FamilyActivity.this);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.appbar_family);
            findViewById(R.id.img_back_arrow_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        myViewPager2 = findViewById(R.id.viewpager);
        if (myViewPager2 != null)
        {
            setUpViewPager(myViewPager2);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, myViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position ==0) {
                    tab.setIcon(tabIconsEnabled[0]);
                }
                if (position==1)
                tab.setIcon(tabIconsDisabled[position]);

            }
        }).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
              //  tab.setIcon(tabIconsEnabled[]);
                switch (tab.getPosition())
                {
                    case 0: tab.setIcon(tabIconsEnabled[0]);
                    break;
                    case 1: tab.setIcon(tabIconsEnabled[1]);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition())
                {
                    case 0: tab.setIcon(tabIconsDisabled[0]);
                        break;
                    case 1: tab.setIcon(tabIconsDisabled[1]);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }



    private void setUpViewPager(ViewPager2 viewPager) {

        arrayList.add(new MembersFragment());
        arrayList.add(new RequestsFragment());

        myAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        // set Orientation in your ViewPager2
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewPager.setAdapter(myAdapter);

        //viewPager.setPageTransformer(new MarginPageTransformer(1500));
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }
}
