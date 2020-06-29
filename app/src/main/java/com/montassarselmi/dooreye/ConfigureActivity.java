package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.montassarselmi.dooreye.Model.User;

import static com.montassarselmi.dooreye.MainActivity.changeStatusBarToWhite;

public class ConfigureActivity extends AppCompatActivity {

    private static final String TAG = ConfigureActivity.class.getSimpleName();
    private EditText edtFullName,edtEmail,edtBoxId;
    private Button btnConfirmConf;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRefBox;
    private DatabaseReference usersRef ;
    private DatabaseReference  mRefBoxUser,mRefBoxStatus, mRefUsers;
    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor editor;
    public static boolean isActivityRunning;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        changeStatusBarToWhite(this);

        edtBoxId = (EditText) findViewById(R.id.edt_box_id);
        edtFullName = (EditText) findViewById(R.id.edt_full_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnConfirmConf = (Button) findViewById(R.id.btn_confirm_configure);
        mAuth = FirebaseAuth.getInstance();

        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        mRefBox =database.getReference("BoxList/");
        usersRef = database.getReference("BoxList/"+findBoxId()+"/users/");

        checkIfUserAvailable();

        btnConfirmConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edtEmail.getText().toString().trim();
                final String fullName = edtFullName.getText().toString().trim();
                final String boxId = edtBoxId.getText().toString().trim();
                final String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

                if (fullName.isEmpty()) {
                    edtFullName.setError(getResources().getString(R.string.empty_fullname));
                    edtFullName.requestFocus();
                    return;
                }
                if (fullName.length()<3) {
                    edtFullName.setError(getResources().getString(R.string.error_fullname));
                    edtFullName.requestFocus();
                    return;
                }
                if (fullName.length()>15) {
                    edtFullName.setError(getResources().getString(R.string.error_fullname));
                    edtFullName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    edtEmail.setError(getResources().getString(R.string.empty_email));
                    edtEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.setError(getResources().getString(R.string.error_email));
                    edtEmail.requestFocus();
                    return;
                }

                if (boxId.isEmpty()) {
                    edtBoxId.setError(getResources().getString(R.string.empty_boxId));
                    edtBoxId.requestFocus();
                    return;
                }

                ValueEventListener boxComparator  =new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Boolean isAvailable=false;
                        for (DataSnapshot data : dataSnapshot.getChildren())
                        {
                            if (data.getKey().equals(boxId))
                            {
                                editor.putString("BOX_ID", boxId);
                                editor.apply();
                                //Toast.makeText(ConfigureActivity.this, "onDataChange - ConfigureActivity", Toast.LENGTH_SHORT).show();
                                mRefBoxStatus =database.getReference("BoxList/"+boxId);
                                String status = "admin";
                                Log.d(TAG, " data Ref: "+data.toString());
                                if (data.child("users").hasChildren())
                                {
                                    status = "waiting";
                                }

                                User user = new User(fullName,phoneNumber,email,boxId,status);
                                mRefBoxUser =database.getReference("BoxList/"+boxId+"/users");
                                mRefBoxUser.child(mAuth.getCurrentUser().getUid()).setValue(user);
                                isAvailable=true;
                                mRefUsers = database.getReference().child("Users");
                                mRefUsers.child(mAuth.getCurrentUser().getUid()).setValue(user);
                                startActivity(new Intent(ConfigureActivity.this,MainActivity.class));
                                finish();

                                return;
                            }
                        }
                        if (!isAvailable)
                        edtBoxId.setError(getResources().getString(R.string.error_boxId));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mRefBox.addListenerForSingleValueEvent(boxComparator);
                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d(TAG, "onChildAdded: ");
                        //Toast.makeText(ConfigureActivity.this, "onChildAdded", Toast.LENGTH_SHORT).show();
                        editor.putBoolean("IS_SAVED",true);
                        editor.apply();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //Toast.makeText(ConfigureActivity.this, "onChildChanged", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        //Toast.makeText(ConfigureActivity.this, "onChildRemoved :"+dataSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //Toast.makeText(ConfigureActivity.this, "onChildMoved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mRefBox.addChildEventListener(childEventListener);



            }
        });

    }
    private String findBoxId(){
        String boxId="";
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        return boxId;
    }
    private void checkIfUserAvailable() {
        DatabaseReference mUsersRef = database.getReference().child("Users");
        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: uid="+mAuth.getCurrentUser().getUid());
                Log.d(TAG, "onDataChange: uid="+dataSnapshot.getChildrenCount());

                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                {
                    editor.putString("BOX_ID", dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("boxId").getValue().toString());
                    editor.apply();
                    startActivity(new Intent(ConfigureActivity.this,MainActivity.class));
                    finish();

                }else {
                    Toast.makeText(ConfigureActivity.this, "nope", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("IS_SAVED",false);
                    editor.apply();

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
    protected void onPause() {
        super.onPause();
        isActivityRunning = false;
    }
}
