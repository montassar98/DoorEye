package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.MarginPageTransformer;
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



public class FamilyActivity extends AppCompatActivity {


    ViewPagerFragmentAdapter myAdapter;
    ArrayList<Fragment> arrayList = new ArrayList<>();
    String[] tabNames = {"Members","Requests"};
    int[] tabIcons = {R.drawable.ic_requests1,R.drawable.ic_requests1};
    ViewPager2 myViewPager2;

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
            actionBar.setTitle("Family & Partners");
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
                tab.setText(null);
                tab.setIcon(tabIcons[position]);
                if (tab.isSelected())
                {
                    tab.setIcon(null);
                    tab.setText(tabNames[position]);
                }
            }
        }).attach();



    }

    public static void changeStatusBarToWhite(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            activity.getWindow().setStatusBarColor(Color.rgb(255,255,255));

        }
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


}
