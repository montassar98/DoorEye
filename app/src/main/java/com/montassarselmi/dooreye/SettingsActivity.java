package com.montassarselmi.dooreye;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.montassarselmi.dooreye.FamilyActivity.changeStatusBarToWhite;

public class SettingsActivity extends AppCompatActivity implements OnSpinnerItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incomming_message);
        changeStatusBarToWhite(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle("Settings");

            getSupportActionBar().setCustomView(R.layout.appbar_settings_layout);
            findViewById(R.id.img_back_arrow_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().subscribeToTopic("mTopic");
                findViewById(R.id.button).setEnabled(false);
                Toast.makeText(SettingsActivity.this, "subscribed", Toast.LENGTH_SHORT).show();
            }
        });

        NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("En", "Fr", "Ar"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnSpinnerItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
        String itemSelected = parent.getItemAtPosition(position).toString();
        setAppLanguage(itemSelected);

    }

    private void setAppLanguage(String languageSelected){
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.setLocale(new Locale(languageSelected.toLowerCase()));
        res.updateConfiguration(config, dm);
    }
}