package com.montassarselmi.dooreye;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class CheckFrontDoorActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String boxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_front_door);
        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        usersRef = database.getReference("BoxList/"+boxId+"/users");
        CheckFrontDoor();

    }

    private void CheckFrontDoor() {
      /*  usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               usersRef.child(mAuth.getUid()).child("checking").setValue(true);
               startActivity(new Intent(CheckFrontDoorActivity.this,VideoChatActivity.class));
               finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       */
    }


}

