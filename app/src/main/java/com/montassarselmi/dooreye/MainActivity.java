package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Constants;

public class MainActivity extends AppCompatActivity {

    private final String TAG ="MainActivity";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Users");
    private FirebaseAuth mAuth;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseReference boxIdRef,ringingRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        boxIdRef = database.getReference("Users/"+mAuth.getUid()+"/");
        checkIfUserAvailable();
        checkForCalls();
    }

    private void checkIfUserAvailable() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: uid="+mAuth.getCurrentUser().getUid());

                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                {
//                    Toast.makeText(MainActivity.this, "find it", Toast.LENGTH_SHORT).show();
                   // Toast.makeText(MainActivity.this, "children ="+dataSnapshot.getValue(), Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());

                }else {
                    Toast.makeText(MainActivity.this, "nope", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("IS_SAVED",false);
                    editor.apply();
                    startActivity(new Intent(MainActivity.this,ConfigureActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    private String findBoxId(){
        String boxId="";
        boxIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String boxId = dataSnapshot.child("boxId").getValue().toString();
                Log.d(TAG, "onDataChange: "+boxId);
                editor.putString("BOX_ID",boxId);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        return boxId;
    }

    private void checkForCalls() {
        ringingRef = database.getReference("BoxList/"+findBoxId()+"/users/"+mAuth.getUid());
        ringingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: data "+ dataSnapshot.getValue());
                if (dataSnapshot.hasChild("Ringing"))
                {
                    Log.d(TAG, "onDataChange: user have a call");
                    Toast.makeText(MainActivity.this, "user have a call", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this,CallingActivity.class));
                   // finish();
                }else Log.d(TAG, "onDataChange: there is no call yet.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }
}
