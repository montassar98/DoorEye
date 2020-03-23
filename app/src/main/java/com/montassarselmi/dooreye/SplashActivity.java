package com.montassarselmi.dooreye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;

public class SplashActivity extends AppCompatActivity {

    private ImageView imgSplashLogo;
    private Animation animFadeIn, animTranslate, topAnim, bottomAnim;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //===========/Hide Status Bar/===========
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //=======================================
        setContentView(R.layout.activity_splash);

        imgSplashLogo = (ImageView) findViewById(R.id.splash_logo);
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        animTranslate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.transalte);
        topAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_anim);
       /* imgSplashLogo.setAnimation(animTranslate);
        imgSplashLogo.startAnimation(animTranslate);
        findViewById(R.id.txt_splash).setAnimation(animFadeIn);
        findViewById(R.id.txt_splash).startAnimation(animFadeIn);
        */
        imgSplashLogo.setAnimation(topAnim);
        imgSplashLogo.startAnimation(topAnim);
        findViewById(R.id.linear_title).setAnimation(bottomAnim);
        findViewById(R.id.linear_title).startAnimation(bottomAnim);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.door_eye);
        mediaPlayer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent regIntent = new Intent(SplashActivity.this,RegistrationActivity.class);
                SplashActivity.this.startActivity(regIntent);
                overridePendingTransition(0, 0);
                SplashActivity.this.finish();
            }
        },5000);
    }
}
