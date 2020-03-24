package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class ConfigureActivity extends AppCompatActivity {

    private EditText edtFullName,edtEmail,edtBoxId;
    private Button btnConfirmConf;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRefBox;
    private DatabaseReference mRefBoxUser,mRefUsers;
    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        edtBoxId = (EditText) findViewById(R.id.edt_box_id);
        edtFullName = (EditText) findViewById(R.id.edt_full_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnConfirmConf = (Button) findViewById(R.id.btn_confirm_configure);
        mAuth = FirebaseAuth.getInstance();

        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        mRefBox =database.getReference("BoxList/");

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
                if (fullName.length()<4) {
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
                                Toast.makeText(ConfigureActivity.this, "onDataChange - ConfigureActivity", Toast.LENGTH_SHORT).show();
                                User user = new User(fullName,phoneNumber,email,boxId);
                                mRefBoxUser =database.getReference("BoxList/"+boxId+"/users");
                                mRefBoxUser.child(mAuth.getCurrentUser().getUid()).setValue(user);
                                mRefUsers = database.getReference("Users/"+mAuth.getCurrentUser().getUid());
                                mRefUsers.setValue(user);
                                startActivity(new Intent(ConfigureActivity.this,MainActivity.class));
                                isAvailable=true;

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
                mRefBox.addValueEventListener(boxComparator);
                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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


}
