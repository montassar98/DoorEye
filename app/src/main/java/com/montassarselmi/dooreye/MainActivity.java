package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.montassarselmi.dooreye.Model.User;
import com.montassarselmi.dooreye.Services.ForegroundCallService;
import com.montassarselmi.dooreye.Utils.FamilyRecyclerViewAdapter;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG ="MainActivity";
    private final String CALLING_TAG ="Calling listener";


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout visbleCheck,visibleEvent,visbleSetting;
    private CardView cvEventHistory,cvCheckFrontDoor,cvFamily,cvSettings,cvContactUs,cvLogout;
    private TextView userName;
    private ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeStatusBarToWhite(MainActivity.this);
        Log.d(TAG, "onCreate: ");

        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        //-------------------------------------------------------
        cvCheckFrontDoor = (CardView) findViewById(R.id.cv_front_door);
        cvContactUs = (CardView) findViewById(R.id.cv_contact_us);
        cvEventHistory= (CardView) findViewById(R.id.cv_history);
        cvFamily = (CardView) findViewById(R.id.cv_family);
        cvSettings= (CardView) findViewById(R.id.cv_settings);
        cvLogout= (CardView) findViewById(R.id.cv_logout);
        visbleCheck= (RelativeLayout) findViewById(R.id.visible_check);
        visibleEvent= (RelativeLayout) findViewById(R.id.visible_event);
        visbleSetting= (RelativeLayout) findViewById(R.id.visible_setting);
        cvCheckFrontDoor.setOnClickListener(this);
        cvContactUs.setOnClickListener(this);
        cvEventHistory.setOnClickListener(this);
        cvFamily.setOnClickListener(this);
        cvSettings.setOnClickListener(this);
        cvLogout.setOnClickListener(this);
        //--------------------------------------------------------
        usersRef = database.getReference("BoxList/"+findBoxId()+"/users");
        checkIfUserAvailable();
        checkForCalls();

        //get current user info
        userName = (TextView) findViewById(R.id.txt_user_name);
        imgUser = (ImageView) findViewById(R.id.user_image) ;
        User user;
        getCurrentUserInfo();

        //check users visibility
        checkUserVisibility();



    }

    private void checkUserVisibility() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mAuth.getUid()).child("status").getValue().toString().equals("waiting")) {
                    visbleCheck.setVisibility(View.GONE);
                    visibleEvent.setVisibility(View.GONE);
                    visbleSetting.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCurrentUserInfo() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                {
                    userName.setText(dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("fullName").getValue().toString());
                    if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild("profileImage"))
                    {
                        Picasso.get().load(dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("profileImage").getValue().toString()).into(imgUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void changeStatusBarToWhite(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          //  activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            activity.getWindow().setStatusBarColor(Color.rgb(255,255,255));

        }
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
       // stopService();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    private String findBoxId(){
        String boxId="";
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        return boxId;
    }

    private void checkForCalls() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: data "+ dataSnapshot.getValue());
                if (dataSnapshot.child(mAuth.getUid()).hasChild("Ringing") && dataSnapshot.child(mAuth.getUid()).hasChild("pickup"))
                {
                        if (dataSnapshot.child(mAuth.getUid()).child("pickup").getValue().equals(false)) {
                            Log.d(TAG, " user have a call");
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.uset_have_call), Toast.LENGTH_LONG).show();
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
        //close All opened activities.
        finishAffinity();
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
       // startService();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.cv_history:
                onEventHistoryClicked();
                break;
            case R.id.cv_front_door:
                onCheckFrontDoorClicked();
                break;
            case R.id.cv_family:
                onFamilyClicked();
                break;
            case R.id.cv_settings:
                onSettingsClicked();
                break;
            case R.id.cv_contact_us:
                onContactUsClicked();
                break;
            case R.id.cv_logout:
                onLogoutClicked();
                break;
        }
    }

    private void onEventHistoryClicked() {
        Log.d(TAG, "onEventHistoryClicked ");
        Toast.makeText(this, "onEventHistoryClicked", Toast.LENGTH_SHORT).show();
    }

    private void onCheckFrontDoorClicked() {
        Log.d(TAG, "onCheckFrontDoorClicked");
        Toast.makeText(this, "onCheckFrontDoorClicked", Toast.LENGTH_SHORT).show();
    }

    private void onFamilyClicked() {
        Log.d(TAG, "onFamilyClicked");
        Toast.makeText(this, "onFamilyClicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, FamilyActivity.class));
    }

    private void onSettingsClicked() {
        Log.d(TAG, "onSettingsClicked ");
        Toast.makeText(this, "onSettingsClicked ", Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(MainActivity.this, EditActivity.class));
    }

    private void onContactUsClicked() {
        Log.d(TAG, "onContactUsClicked ");
        Toast.makeText(this, "onContactUsClicked ", Toast.LENGTH_SHORT).show();
    }

    private void onLogoutClicked() {
        Log.d(TAG, "onLogoutClicked");
        Toast.makeText(this, "onLogoutClicked", Toast.LENGTH_SHORT).show();
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        finish();

    }
}
