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
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.montassarselmi.dooreye.Model.Live;
import com.montassarselmi.dooreye.Model.Ring;

import com.montassarselmi.dooreye.Utils.CustomProgress;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener, View.OnClickListener {
    private static String API_KEY ="" ;
    private static String SESSION_ID ="" ;
    private static String TOKEN ="";
    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;
    private Session mSession;
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseDatabase database;
    private DatabaseReference userInfoRef,userBoxRef, boxHistoryRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private boolean isLive = false;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private DatabaseReference instantImagePathRef;
    public static boolean isActivityRunning;

    private CustomProgress  mProgressDialog = CustomProgress.getInstance();

    private ToggleButton toggleSoundState, toggleDoorState, toggleSwapCamera, toggleVideo;
    private boolean isMuted = false;
    private boolean isVideoPublished = false;

    private View main;
    private  Ring ring;
    private String name;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_chat);
        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        main = findViewById(R.id.main);
        database = FirebaseDatabase.getInstance();
        userInfoRef = database.getReference("BoxList").child(mSharedPreferences.getString("BOX_ID","Null"))
                .child("users").child(mAuth.getUid());
        userBoxRef=database.getReference("BoxList").child(mSharedPreferences.getString("BOX_ID","Null"));
        DatabaseReference mDoorRef=database.getReference("BoxList").child(mSharedPreferences.getString("BOX_ID","Null")).child("door");
        boxHistoryRef = database.getReference("BoxList/"+mSharedPreferences.getString("BOX_ID","Null")+"/history/");
        instantImagePathRef = database.getReference().child("BoxList")
                .child(mSharedPreferences.getString("BOX_ID","Null")).child("history").child("instantImagePath");
        requestPermissions();
        mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);
        name = mSharedPreferences.getString("USERNAME", "Null");

        findViewById(R.id.btn_end_video_call).setOnClickListener(this);
        findViewById(R.id.btn_end_video_call).setVisibility(View.GONE);
        toggleDoorState = findViewById(R.id.toggle_door_state);
        toggleSoundState = findViewById(R.id.toggle_sound_state);
        toggleSwapCamera = findViewById(R.id.toggle_swap_state);
        toggleVideo = findViewById(R.id.toggle_video_state);
        toggleDoorState.setVisibility(View.GONE);
        toggleSoundState.setVisibility(View.GONE);
        toggleSwapCamera.setVisibility(View.GONE);
        toggleVideo.setVisibility(View.GONE);

        toggleDoorState.setOnClickListener(view -> {
                mDoorRef.setValue(1);
                toggleDoorState.setEnabled(false);
        });
        toggleSoundState.setOnClickListener(view -> {
            if (!isMuted)
                mPublisher.setPublishAudio(false);
            else mPublisher.setPublishAudio(true);

            isMuted=!isMuted;
        });
        toggleVideo.setOnClickListener(view ->{
            if (!isVideoPublished)
                mPublisher.setPublishVideo(false);
            else mPublisher.setPublishVideo(true);

            isVideoPublished=!isVideoPublished;
        });

        toggleSwapCamera.setOnClickListener(view -> {
            mPublisher.swapCamera();
        });

        mPublisherViewContainer.setVisibility(View.GONE);
        if (mPublisher !=null){mPublisher.destroy();}
        if (mSubscriber !=null){mSubscriber.destroy();}
        mProgressDialog.showProgress(this, "Waiting For The Connection", false);
    }


    private void addLiveHistory()
    {
        Log.d(LOG_TAG, "adding to the history...");
        // send to the history
        Random random = new Random();
        int id = random.nextInt(99999-10000)+10000;
        DateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format));
        //get current date time with Date()
        Date date = new Date();
        String time = dateFormat.format(date);
        Live live = new Live(id, date,name);
        boxHistoryRef.child("live").child(date.toString()).setValue(live);
    }
    public void fetchSessionConnectionData() {
        RequestQueue reqQueue = Volley.newRequestQueue(this);
        String roomId= mSharedPreferences.getString("BOX_ID","Null");

        Log.d(LOG_TAG, "roomId: "+roomId);
        String url ="https://dooreye.herokuapp.com";
        if(mSharedPreferences.getBoolean("CHECKING", false)){
            url = "https://dooreyebox.herokuapp.com";
            editor.putBoolean("CHECKING", false);
            editor.apply();
            isLive = true;
        }
        reqQueue.add(new JsonObjectRequest(Request.Method.GET,
                url + "/room/:"+roomId,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    API_KEY = response.getString("apiKey");
                    SESSION_ID = response.getString("sessionId");
                    TOKEN = response.getString("token");

                    Log.i(LOG_TAG, "API_KEY: " + API_KEY);
                    Log.i(LOG_TAG, "SESSION_ID: " + SESSION_ID);
                    Log.i(LOG_TAG, "TOKEN: " + TOKEN);

                    mSession = new Session.Builder(VideoChatActivity.this, API_KEY, SESSION_ID).build();
                    mSession.setSessionListener(VideoChatActivity.this);
                    mSession.connect(TOKEN);

                } catch (JSONException error) {
                    Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
            }
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);

            // initialize and connect to the session
            fetchSessionConnectionData();

        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }


    @Override
    public void onConnected(Session session) {
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "onDisconnected: ");
        session.disconnect();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.i(LOG_TAG, "onStreamReceived: ");
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
            if (isLive)
            {
                addLiveHistory();
            }
            else
            createRingHistory();


            mProgressDialog.hideProgress();
            findViewById(R.id.btn_end_video_call).setVisibility(View.VISIBLE);
            mPublisherViewContainer.setVisibility(View.VISIBLE);
            toggleDoorState.setVisibility(View.VISIBLE);
            toggleSoundState.setVisibility(View.VISIBLE);
            toggleVideo.setVisibility(View.VISIBLE);
            toggleSwapCamera.setVisibility(View.VISIBLE);
        }
    }

    private void createRingHistory() {
        Log.d(LOG_TAG, "createRingHistory");
        ring = new Ring();
        Random random = new Random();
        int id = random.nextInt(99999-10000)+10000;
        DateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format));
        //get current date time with Date()
        Date date = new Date();
        String time = dateFormat.format(date);
        ring.setId(id);
        ring.setEventTime(date);
        ring.setStatus("Ring");
        ring.setResponder(name);
        instantImagePathRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "instant image path \n"+dataSnapshot.getValue());
                VideoChatActivity.this.ring.setVisitorImage(dataSnapshot.getValue().toString());
                boxHistoryRef.child("rings").child(String.valueOf(VideoChatActivity.this.ring.getEventTime()))
                        .setValue(VideoChatActivity.this.ring);
                instantImagePathRef.removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "onStreamDropped: ");
        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
            
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

        Log.i(LOG_TAG, "onError: ");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "onStreamCreated: ");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "onStreamDestroyed: ");
        stream.getSession().unpublish(publisherKit);
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.d(LOG_TAG, "onError: " + opentokError.getMessage());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.btn_end_video_call:
                endCall();
                mSession.disconnect();
                startActivity(new Intent(VideoChatActivity.this, MainActivity.class));
                finish();
                break;
        }
    }
    private void endCall() {
        Log.d(LOG_TAG, "endCall");
        userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //look for the Ringing Reference and delete it when the user end the call.
                if (dataSnapshot.hasChild("Ringing"))
                {
                    Log.d(LOG_TAG, "onDataChange: delete ringing reference.");
                    userInfoRef.child("Ringing").removeValue();
                }
                if (dataSnapshot.hasChild("pickup"))
                {
                    Log.d(LOG_TAG, "onDataChange: delete ringing reference.");
                    userInfoRef.child("pickup").removeValue();

                }
                if (dataSnapshot.hasChild("checking"))
                {
                    Log.d(LOG_TAG, "onDataChange: delete checking reference.");
                    userInfoRef.child("checking").removeValue();
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
                    userBoxRef.child("Calling").child(mAuth.getUid()).removeValue();
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
        isActivityRunning =false;
    }
}