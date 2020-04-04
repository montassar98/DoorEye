package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.montassarselmi.dooreye.Fragments.AllFragment;
import com.montassarselmi.dooreye.Fragments.LiveFragment;
import com.montassarselmi.dooreye.Fragments.MembersFragment;
import com.montassarselmi.dooreye.Fragments.MotionsFragment;
import com.montassarselmi.dooreye.Fragments.RequestsFragment;
import com.montassarselmi.dooreye.Fragments.RingsFragment;
import com.montassarselmi.dooreye.Utils.HistoryViewPager2Adapter;
import com.montassarselmi.dooreye.Utils.ViewPagerFragmentAdapter;
import com.montassarselmi.dooreye.Utils.ZoomOutPageTransformer;

import java.util.ArrayList;

import static com.montassarselmi.dooreye.MainActivity.changeStatusBarToWhite;

public class EventHistoryActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = EventHistoryActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 mViewPager2;
    private ArrayList<Fragment> arrayList = new ArrayList<>();
    private HistoryViewPager2Adapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);
        changeStatusBarToWhite(EventHistoryActivity.this);
        initUi();


    }

    private void initUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Log.d(TAG, "init actionBar");
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.appbar_event_history_layout);
            findViewById(R.id.img_back_arrow_history).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        mViewPager2 = (ViewPager2) findViewById(R.id.viewpager2_history);
        if (mViewPager2 != null)
        {
            setUpViewPager(mViewPager2);
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);



    }

    private void setUpViewPager(ViewPager2 viewPager) {

        arrayList.add(new AllFragment());
        arrayList.add(new RingsFragment());
        arrayList.add(new MotionsFragment());
        arrayList.add(new LiveFragment());

        mAdapter = new HistoryViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        // set Orientation in your ViewPager2
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(mAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.tab_all).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.tab_rings).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.tab_motions).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.tab_live).setChecked(true);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.tab_all:
                Toast.makeText(this, "ALL", Toast.LENGTH_SHORT).show();
                mViewPager2.setCurrentItem(0);
                return true;
            case R.id.tab_live:
                Toast.makeText(this, "LIVE", Toast.LENGTH_SHORT).show();
                mViewPager2.setCurrentItem(3);
                return true;
            case R.id.tab_motions:
                Toast.makeText(this, "Motions", Toast.LENGTH_SHORT).show();
                mViewPager2.setCurrentItem(2);
                return true;
            case R.id.tab_rings:
                Toast.makeText(this, "Rings", Toast.LENGTH_SHORT).show();
                mViewPager2.setCurrentItem(1);
                return true;
            default: return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
