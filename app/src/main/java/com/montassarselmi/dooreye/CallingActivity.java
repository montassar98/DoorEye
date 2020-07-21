package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class CallingActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG="CallingActivity";

    private MediaPlayer media;
    private ImageView imgEndCall, imgPickUpCall;
    private ImageView imgVisitor;
    private Animation animCall;
    private FirebaseDatabase database;
    private DatabaseReference userInfoRef,userBoxRef,instantImagePathRef;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    public static boolean isActivityRunning;
    public static boolean noPickUp = true;
    private Ringtone r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Log.d(TAG, "onCreate: ");
        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        imgPickUpCall = (ImageView) findViewById(R.id.img_pickup);
        imgEndCall = (ImageView) findViewById(R.id.img_end_call);
        imgVisitor = (ImageView) findViewById(R.id.img_visit);
        imgPickUpCall.setOnClickListener(this);
        imgEndCall.setOnClickListener(this);
        animCall = AnimationUtils.loadAnimation(this,R.anim.anim_ringing);
        imgEndCall.setAnimation(animCall);
        imgPickUpCall.setAnimation(animCall);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        instantImagePathRef = database.getReference().child("BoxList")
                .child(mSharedPreferences.getString("BOX_ID","Null")).child("history");
        userInfoRef = database.getReference("BoxList").child(mSharedPreferences.getString("BOX_ID","Null"))
                .child("users").child(mAuth.getUid());
        userBoxRef=database.getReference("BoxList").child(mSharedPreferences.getString("BOX_ID","Null"));
        checkVisitorImage();
        checkIfSomeonePickedUp();

        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), ringtone);
        r.play();

        noPickUp = true;


    }

    private void checkVisitorImage() {
        instantImagePathRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("instantImagePath"))
                {
                    imgVisitor.setVisibility(View.VISIBLE);
                    Picasso.get().load(dataSnapshot.child("instantImagePath").getValue().toString()).into(imgVisitor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfSomeonePickedUp()
    {

        Log.d(TAG, "checkIfSomeonePickedUp: ");
        // close the calling activity when someone else has picked up the call.
        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "checkIfSomeonePickedUp data: "+ dataSnapshot.toString() + noPickUp);
                if (!dataSnapshot.hasChild("Ringing") && !dataSnapshot.hasChild("pickup") && noPickUp)
                {
                    Log.d(TAG, "checkIfSomeonePickedUp data: no ringing no pickup");
                    startActivity(new Intent(CallingActivity.this, MainActivity.class));
                    finish();
                    noPickUp = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.img_pickup:
                pickUpCall();
                break;
            case R.id.img_end_call:
                endCall();
                break;
        }
    }

    private void endCall() {
        Log.d(TAG, "endCall");
        userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //look for the Ringing Reference and delete it when the user end the call.
                if (dataSnapshot.hasChild("Ringing"))
                {
                    Log.d(TAG, "onDataChange: delete ringing reference.");
                    userInfoRef.child("Ringing").removeValue();
                    userInfoRef.child("pickup").removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userBoxRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Calling")) {
                    userBoxRef.child("Calling").child(mAuth.getCurrentUser().getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imgVisitor.setVisibility(View.GONE);
        startActivity(new Intent(CallingActivity.this,MainActivity.class));
        finish();
    }

    private void pickUpCall() {
        Log.d(TAG, "pickUpCall");
        userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //look for the Ringing Reference and delete it when the user end the call.
                if (dataSnapshot.hasChild("Ringing"))
                {
                    Log.d(TAG, "onDataChange: delete ringing reference.");
                    userInfoRef.child("pickup").setValue(true);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imgVisitor.setVisibility(View.GONE);
        startActivity(new Intent(CallingActivity.this, VideoChatActivity.class));
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityRunning = false;
        //media.stop();
        r.stop();

    }

}
