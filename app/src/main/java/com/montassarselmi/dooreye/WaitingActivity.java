package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.montassarselmi.dooreye.MainActivity.changeStatusBarToWhite;

public class WaitingActivity extends AppCompatActivity {

    private static final String TAG = WaitingActivity.class.getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    public static boolean isActivityRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        changeStatusBarToWhite(WaitingActivity.this);

        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        usersRef = database.getReference("BoxList/"+findBoxId()+"/users");


        checkIfUserAvailable();
    }
    private String findBoxId(){
        String boxId="";
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        return boxId;
    }

    private void checkIfUserAvailable() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: uid="+mAuth.getCurrentUser().getUid());
                Log.d(TAG, "waiting...");

                if (!dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("status").getValue().equals("waiting"))
                {
                    Log.d(TAG, dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("status").getValue().toString());
                    startActivity(new Intent(WaitingActivity.this,MainActivity.class));
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
        isActivityRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }
}
