package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EventHistoryActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.tab_all:
                Toast.makeText(this, "ALL", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.tab_live:
                Toast.makeText(this, "LIVE", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.tab_motions:
                Toast.makeText(this, "Motions", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.tab_rings:
                Toast.makeText(this, "Rings", Toast.LENGTH_SHORT).show();
                return true;
            default: return false;
        }
    }
}
