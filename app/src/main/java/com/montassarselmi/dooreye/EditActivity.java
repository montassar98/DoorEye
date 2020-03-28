package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.montassarselmi.dooreye.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

import static com.montassarselmi.dooreye.FamilyActivity.changeStatusBarToWhite;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private static  final  String TAG = EditText.class.getSimpleName();

    private final int REQUEST_FROM_GALLERY =1000;
    private final int REQUEST_FROM_CAMERA =2000;
    private final int CAMERA_PERMISSIONS =123;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String boxId;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private DatabaseReference mRefUser=database.getReference();
    private  Bitmap imageBitmap;
    private CircleImageView imgProfile;
    private CircularProgressButton btnSubmit;
    //private Button btnSubmit;
    private EditText edtName,edtEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        changeStatusBarToWhite(EditActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setTitle("Edit");
            getSupportActionBar().setCustomView(R.layout.appbar_edit_layout);
            findViewById(R.id.img_back_arrow_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            initUi();
            btnSubmit.setOnClickListener(this);
            imgProfile.setOnClickListener(this);

        }
    }

    private void initUi() {
        btnSubmit =(CircularProgressButton) findViewById(R.id.btn_submit_edit);
       // btnSubmit =(Button) findViewById(R.id.btn_submit_edit);
        edtEmail = (EditText) findViewById(R.id.edt_email_edit);
        edtName = (EditText) findViewById(R.id.edt_full_name_edit);
        imgProfile = (CircleImageView) findViewById(R.id.img_profile_edit);
        edtName.setText(getIntent().getStringExtra("FULL_NAME"));
        edtEmail.setText(getIntent().getStringExtra("EMAIL"));
    }
    private void requestPermissions() {
        String[] perms = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                             Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
            EasyPermissions.requestPermissions(this,
                    "This app needs access to your camera and storage to take a photo", CAMERA_PERMISSIONS, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions,grantResults, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_submit_edit :

                submitModification();
                break;
            case R.id.img_profile_edit:
                changePhoto();
                break;
        }
    }

    private void submitModification() {
        Log.d(TAG, "submitModification ");
        btnSubmit.startAnimation();

         final String profileImage = getFileToByte(imageBitmap);
        Log.d(TAG, "image to string\n "+profileImage);
        mRefUser = database.getReference("BoxList").child(boxId).child("users");
        mRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    Log.d(TAG, "" + dataSnapshot.toString());
                    User user;
                    user = data.getValue(User.class);
                    if (user != null && user.getPhoneNumber().equals(mAuth.getCurrentUser().getPhoneNumber())) {
                        user.setFullName(edtName.getText().toString());
                        user.setEmail(edtEmail.getText().toString());
                        if (!profileImage.isEmpty())
                        {
                            user.setProfileImage(profileImage);
                        }
                        mRefUser.child(mAuth.getCurrentUser().getUid()).setValue(user);
                        btnSubmit.dispose();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public static String getFileToByte(Bitmap bitmap){
        Bitmap bmp = bitmap;
        ByteArrayOutputStream bos = null;
        byte[] bt = null;
        String encodeString = null;
        try{
            bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bt = bos.toByteArray();
            encodeString = Base64.encodeToString(bt, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }
        return encodeString;
    }

    private void changePhoto() {
        Log.d(TAG, "changePhoto ");
        final Dialog dialog = new Dialog(EditActivity.this);
        dialog.setContentView(R.layout.dialog_edit_choose_photo);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.btn_from_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: From Camera");
                requestPermissions();
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(takePicture , REQUEST_FROM_CAMERA);

                dialog.dismiss();

            }
        });
        dialog.findViewById(R.id.btn_from_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: From Gallery");
                requestPermissions();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_FROM_GALLERY);

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_FROM_CAMERA:
                if (resultCode == RESULT_OK)
                {
                    Log.d(TAG, "onActivityResult: approved From Camera");
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        imgProfile.setImageBitmap(imageBitmap);
                    }else imgProfile.setImageDrawable(getDrawable(R.drawable.profile));
                }
                break;
            case REQUEST_FROM_GALLERY:
                if (resultCode == RESULT_OK)
                {
                    Log.d(TAG, "onActivityResult: approved From Gallery");
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imgProfile.setImageBitmap(imageBitmap);
                    }else imgProfile.setImageDrawable(getDrawable(R.drawable.profile));
                }
                break;
        }
    }


}
