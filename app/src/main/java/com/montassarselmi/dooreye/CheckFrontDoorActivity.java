package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CheckFrontDoorActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseDatabase database;
    private DatabaseReference userInfoRef,userBoxRef;
    private FirebaseAuth mAuth;


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


  }