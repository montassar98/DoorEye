package com.montassarselmi.dooreye;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.montassarselmi.dooreye.MainActivity.changeStatusBarToWhite;

public class SettingsActivity extends AppCompatActivity implements OnSpinnerItemSelectedListener {

    private String itemSelected = "en";

    private static int languagePosSelected = 0;
    private static boolean nightModeEnabled = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
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
        List<String> dataset = new LinkedList<>(Arrays.asList("English", "Français", "عربى"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setSelectedIndex(languagePosSelected);
        niceSpinner.setOnSpinnerItemSelectedListener(this);
        SwitchCompat switchNotification, switchCalls;
        switchCalls = findViewById(R.id.switch_calls);
        switchNotification = findViewById(R.id.switch_notification);
        switchCalls.setChecked(true);
        switchNotification.setChecked(true);

        SwitchCompat switchNightMode = findViewById(R.id.switch_night_mode);
        boolean isNightMode = (this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) ==Configuration.UI_MODE_NIGHT_YES;
        if (isNightMode) {
            switchNightMode.setChecked(true);
        }else {
            switchNightMode.setChecked(false);
        }

        switchNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                nightModeEnabled = isChecked;
            }
        });


    }

    @Override
    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
        itemSelected = parent.getItemAtPosition(position).toString();

    }

    private void setAppLanguage(String languageSelected){
        switch (languageSelected){
            case "English":
                languageSelected = "en";
                languagePosSelected = 0;
                break;
            case "Français":
                languageSelected = "fr";
                languagePosSelected = 1;
                break;
            case "عربى":
                languageSelected = "ar";
                languagePosSelected = 2;
                break;
        }
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.setLocale(new Locale(languageSelected.toLowerCase()));
        res.updateConfiguration(config, dm);
    }

    public void onSaveChangesClicked(View view) {
        setAppLanguage(itemSelected);

        if (nightModeEnabled){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }


        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}