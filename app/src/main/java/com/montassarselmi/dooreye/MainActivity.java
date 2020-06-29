package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.montassarselmi.dooreye.Model.EventHistory;
import com.montassarselmi.dooreye.Services.ForegroundCallService;

import com.squareup.picasso.Picasso;

import org.imaginativeworld.oopsnointernet.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.NoInternetDialog;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity{

    private final String TAG ="MainActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView userName;
    private CircleImageView imgUser;
    public static String boxId;
    private NoInternetDialog noInternetDialog;
    public static boolean isActivityRunning;




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
        //get current user info
        userName = (TextView) findViewById(R.id.txt_user_name);
        imgUser = (CircleImageView) findViewById(R.id.user_image) ;
        //--------------------------------------------------------
        usersRef = database.getReference("BoxList/"+findBoxId()+"/users");
        getCurrentUserInfo();
        checkIfUserAvailable();
        checkForCalls();
        boxId = findBoxId();

    }


    private void getCurrentUserInfo() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser().getUid() != null) {
                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                        String name = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("fullName").getValue().toString();
                        editor.putString("USERNAME", name);
                        editor.apply();
                        userName.setText(name);
                        if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild("profileImage")) {
                            Picasso.get().load(dataSnapshot.child(mAuth.getCurrentUser()
                                    .getUid()).child("profileImage").getValue().toString()).into(imgUser);
                            imgUser.setRotation(90f);
                        }
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

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser().getUid() != null) {
                    Log.d(TAG, "onDataChange: uid=" + mAuth.getCurrentUser().getUid());
                    if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                        Toast.makeText(MainActivity.this, "nope", Toast.LENGTH_SHORT).show();
                        editor.putBoolean("IS_SAVED", false);
                        editor.apply();
                        startActivity(new Intent(MainActivity.this, ConfigureActivity.class));
                        finish();
                    }
                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                        Log.d(TAG, "onDataChange: has child "+dataSnapshot.toString());
                        if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("status").getValue().equals("waiting")) {
                            startActivity(new Intent(MainActivity.this, WaitingActivity.class));
                            finish();
                        }
                    }
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
        IntentFilter filter = new IntentFilter();
        isActivityRunning = true;

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        //initiate no internet dialog.
        initNoInternetDialog();


    }

    public String findBoxId(){
        String boxId="";
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        Log.d(TAG, "findBoxId: "+boxId);
        return boxId;
    }

    private void checkForCalls() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: data "+ dataSnapshot.getValue());
                    if (dataSnapshot.child(mAuth.getUid()).hasChild("Ringing") && dataSnapshot.child(mAuth.getUid()).hasChild("pickup")) {
                        if (dataSnapshot.child(mAuth.getUid()).child("pickup").getValue().equals(false)) {
                            Log.d(TAG, " user have a call");
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.uset_have_call), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, CallingActivity.class));
                            finish();
                        }


                    } else Log.d(TAG, "onDataChange: there is no call yet.");

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

        // No Internet Dialog
        if (noInternetDialog != null) {
            noInternetDialog.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

    }

    public  void initNoInternetDialog(){
        // No Internet Dialog
        NoInternetDialog.Builder builder1 = new NoInternetDialog.Builder(this);

        builder1.setConnectionCallback(new ConnectionCallback() { // Optional
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                // ...
            }
        });
        builder1.setCancelable(false); // Optional
        builder1.setNoInternetConnectionTitle(getResources().getString(R.string.no_internet)); // Optional
        builder1.setNoInternetConnectionMessage(getResources().getString(R.string.check_your_internet)); // Optional
        builder1.setShowInternetOnButtons(true); // Optional
        builder1.setPleaseTurnOnText(getResources().getString(R.string.please_turn_on)); // Optional
        builder1.setWifiOnButtonText(getResources().getString(R.string.wifi)); // Optional
        builder1.setMobileDataOnButtonText(getResources().getString(R.string.mobile_data)); // Optional

        builder1.setOnAirplaneModeTitle(getResources().getString(R.string.no_internet)); // Optional
        builder1.setOnAirplaneModeMessage(getResources().getString(R.string.turned_on_airplane_mod)); // Optional
        builder1.setPleaseTurnOffText(getResources().getString(R.string.please_turn_off)); // Optional
        builder1.setAirplaneModeOffButtonText(getResources().getString(R.string.airplane_mode)); // Optional
        builder1.setShowAirplaneModeOffButtons(true); // Optional

        noInternetDialog = builder1.build();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);//Start the same Activity
        finish(); //finish Activity.
    }

    @Override
    protected void onStop() {
        super.onStop();
       // startService();
        Log.d(TAG, "onStop: ");
        isActivityRunning = false;

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

    public void onEventHistoryClicked(View view) {
        Log.d(TAG, "onEventHistoryClicked ");
        Toast.makeText(this, "onEventHistoryClicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, EventHistoryActivity.class));
    }

    public void onCheckFrontDoorClicked(View view) {
        Log.d(TAG, "onCheckFrontDoorClicked");
        Toast.makeText(this, "onCheckFrontDoorClicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, CheckFrontDoorActivity.class));
    }

    public void onFamilyClicked(View view) {
        Log.d(TAG, "onFamilyClicked");
        Toast.makeText(this, "onFamilyClicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, FamilyActivity.class));
    }

    public void onSettingsClicked(View view) {
        Log.d(TAG, "onSettingsClicked ");
        Toast.makeText(this, "onSettingsClicked ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, IncommingMessage.class));
    }

    public void onContactUsClicked(View view) {
        Log.d(TAG, "onContactUsClicked ");
        Toast.makeText(this, "onContactUsClicked ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
    }

    public void onLogoutClicked(View view) {
        Log.d(TAG, "onLogoutClicked");
        Toast.makeText(this, "onLogoutClicked", Toast.LENGTH_SHORT).show();
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        finish();

    }


}
