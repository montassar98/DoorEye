package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Constants;

import com.montassarselmi.dooreye.Services.ForegroundCallService;

public class MainActivity extends AppCompatActivity {

    private final String TAG ="MainActivity";
    private final String CALLING_TAG ="Calling listener";


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Users");
    private FirebaseAuth mAuth;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseReference boxIdRef,ringingRef;
    private FirebaseJobDispatcher jobDispatcher;
    private Job job;
    private  DatabaseReference myRef = database.getReference("door");

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

        /*

        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        job =jobDispatcher.newJobBuilder()
                .setService(BackgroundCallingService.class)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTag(CALLING_TAG)
                .setTrigger(Trigger.executionWindow(0, 1))
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setReplaceCurrent(false)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK

                )
                .build();

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Start Service", Toast.LENGTH_SHORT).show();
                startService();
                //startService(new Intent(MainActivity.this, CallBackgroundService.class));
            }
        });
        findViewById(R.id.btnEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "end Service", Toast.LENGTH_SHORT).show();
                stopService();
                //stopService(new Intent(MainActivity.this,CallBackgroundService.class));
            }
        });*/
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
        stopService();
        //jobDispatcher.cancel(CALLING_TAG);
        //Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();

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
                if (dataSnapshot.hasChild("Ringing") && dataSnapshot.hasChild("pickup"))
                {
                        if (dataSnapshot.child("pickup").getValue().equals(false)) {
                            Log.d(TAG, "onDataChange: user have a call");
                            Toast.makeText(MainActivity.this, "user have a call", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, CallingActivity.class));
                            finish();
                        }


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
        startService();
        Log.d(TAG, "onStop: ");

    }

    public void startService() {
        //if deleteSharedPreferences()
        Intent serviceIntent = new Intent(this, ForegroundCallService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundCallService.class);
        stopService(serviceIntent);
    }
}
