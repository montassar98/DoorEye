package com.montassarselmi.dooreye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.view.Window;
import android.view.WindowManager;


public class CheckFrontDoorActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseDatabase database;
    private DatabaseReference userInfoRef,userBoxRef;
    private FirebaseAuth mAuth;
    public static boolean isActivityRunning;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_front_door);
        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userInfoRef = database.getReference("BoxList").child(mSharedPreferences.getString("BOX_ID","Null"))
                .child("users").child(mAuth.getUid());
      checkFrontDoor();
    }
    private void checkFrontDoor() {
        userInfoRef.child("checking").setValue(true);
        editor.putBoolean("CHECKING",true);
        editor.apply();
        startActivity(new Intent(CheckFrontDoorActivity.this, VideoChatActivity.class));
        finish();
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