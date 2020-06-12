package com.montassarselmi.dooreye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.montassarselmi.dooreye.Model.User;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    private final String TAG="RegistrationActivity";

    private CountryCodePicker ccp;
    private EditText edtPhone,edtConfirmCode;
    private Button btnContinue;
    private String  phoneNumber="";
    private Boolean isSent =false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks,mcallbacks;

    private FirebaseAuth mAuth;
    private String mVerifcationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingProgress;
    private LinearLayout llPhone;
    private DatabaseReference userRef;
    private DatabaseReference boxUserRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public Boolean saved=false;
    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor editor;
    private TextView txtResend,txtPleaseEnter,txtContactUs,mTextField;
    private ImageView imgBack;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_registration);

        txtPleaseEnter = (TextView) findViewById(R.id.txt_please_enter_verif);
        txtResend = (TextView) findViewById(R.id.txt_resend);
        txtContactUs = (TextView) findViewById(R.id.txt_contact_us_click);
        mTextField = (TextView) findViewById(R.id.text_time_wait);
        imgBack = (ImageView) findViewById(R.id.img_back);

        mSharedPreferences = getBaseContext().getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        userRef = database.getReference("BoxList/"+findBoxId()+"/users/");
        loadingProgress = new ProgressDialog(this);

        edtPhone = (EditText) findViewById(R.id.edt_phone);
        edtConfirmCode = (EditText) findViewById(R.id.edt_confirm_code);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        ccp = (CountryCodePicker) findViewById(R.id.countryCodeHolder);
        ccp.registerCarrierNumberEditText(edtPhone);
        llPhone =(LinearLayout) findViewById(R.id.ll_phone);
        txtContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, ContactUsActivity.class));
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((btnContinue.getText().equals(getResources().getString(R.string.submit))) || isSent )
                {
                    llPhone.setVisibility(View.GONE);
                    findViewById(R.id.txt_any_sms).setVisibility(View.GONE);
                    String verificationCode = edtConfirmCode.getText().toString();
                    if (verificationCode.equals("")) {
                        Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.write_verification_code), Toast.LENGTH_SHORT).show();
                        edtConfirmCode.setError(getResources().getString(R.string.missing_code));
                    }
                    else{
                        loadingProgress.setTitle(getResources().getString(R.string.phone_number_verification));
                        loadingProgress.setMessage(getResources().getString(R.string.wait_verification));
                        loadingProgress.setCanceledOnTouchOutside(false);
                        loadingProgress.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerifcationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }else {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if (!phoneNumber.equals(""))
                    {
                        loadingProgress.setTitle(getResources().getString(R.string.phone_number_verification));
                        loadingProgress.setMessage(getResources().getString(R.string.wait_verification));
                        loadingProgress.setCanceledOnTouchOutside(false);
                        loadingProgress.show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,
                                60,
                                TimeUnit.SECONDS,
                                RegistrationActivity.this,
                                callbacks
                        );
                        txtResend.setVisibility(View.GONE);
                        new CountDownTimer(60000,1000){
                            public void onTick(long millisUntilFinished) {
                                mTextField.setVisibility(View.VISIBLE);
                                mTextField.setText( millisUntilFinished / 1000+getResources().getString(R.string.seconds_remaining));
                            }

                            public void onFinish() {
                                mTextField.setVisibility(View.GONE);
                                txtResend.setVisibility(View.VISIBLE);
                            }
                        }.start();
                    }
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                    llPhone.setVisibility(View.VISIBLE);
                    findViewById(R.id.txt_any_sms).setVisibility(View.VISIBLE);
                loadingProgress.dismiss();
                Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.invalid_code), Toast.LENGTH_SHORT).show();
                btnContinue.setText(getResources().getString(R.string.continu));
                edtConfirmCode.setVisibility(View.GONE);
                findViewById(R.id.txt_enter_verif).setVisibility(View.GONE);
                findViewById(R.id.txt_please_enter_verif).setVisibility(View.GONE);
                findViewById(R.id.ll_resend).setVisibility(View.GONE);
            }


            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerifcationId=s;
                mResendToken = forceResendingToken;
                llPhone.setVisibility(View.GONE);
                findViewById(R.id.txt_any_sms).setVisibility(View.GONE);
                isSent=true;
                btnContinue.setText(getResources().getString(R.string.submit));
                edtConfirmCode.setVisibility(View.VISIBLE);
                imgBack.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_enter_verif).setVisibility(View.VISIBLE);
                findViewById(R.id.txt_please_enter_verif).setVisibility(View.VISIBLE);
                txtPleaseEnter.setText(getResources().getString(R.string.please_enter_code)+ccp.getFullNumberWithPlus());
                findViewById(R.id.ll_resend).setVisibility(View.VISIBLE);
                loadingProgress.dismiss();

            }
        };


        mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                loadingProgress.dismiss();


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingProgress.dismiss();


            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerifcationId=s;
                mResendToken = forceResendingToken;
                isSent=true;
                loadingProgress.dismiss();


            }
        };

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (!isSent) {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    loadingProgress.setTitle(getResources().getString(R.string.resend_code_verification));
                    loadingProgress.setMessage(getResources().getString(R.string.wait_resend_verification));
                    loadingProgress.setCanceledOnTouchOutside(false);
                    loadingProgress.show();
                    resendVerificationCode(phoneNumber,mResendToken);
                    txtResend.setVisibility(View.GONE);
                    new CountDownTimer(11000,1000){
                        public void onTick(long millisUntilFinished) {
                            mTextField.setVisibility(View.VISIBLE);
                            mTextField.setText(getResources().getString(R.string.seconds_remaining)+ millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            mTextField.setVisibility(View.GONE);
                            txtResend.setVisibility(View.VISIBLE);
                        }
                    }.start();
                }
            //}
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, RegistrationActivity.class));
                finish();

            }
        });
    }
    public void resendVerificationCode(String phone,PhoneAuthProvider.ForceResendingToken token)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mcallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    private String findBoxId(){
        String boxId="";
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        return boxId;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            loadingProgress.dismiss();
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(RegistrationActivity.this, "", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            if (mSharedPreferences.getBoolean("IS_SAVED",false))
                            {
                                intent = new Intent(RegistrationActivity.this,ConfigureActivity.class);

                            }else {
                                intent = new Intent(RegistrationActivity.this,ConfigureActivity.class);

                            }
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void isSaved()
    {

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              //  Toast.makeText(RegistrationActivity.this, "onChildAdded - REG", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              //  Toast.makeText(RegistrationActivity.this, "onChildChanged - REG", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                editor.putBoolean("IS_SAVED",false);
                editor.commit();
                startActivity(new Intent(RegistrationActivity.this,ConfigureActivity.class));

              //  Toast.makeText(RegistrationActivity.this, "onChildRemoved - REG:"+dataSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              //  Toast.makeText(RegistrationActivity.this, "onChildMoved - REG", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        };

        ValueEventListener userSaved  =new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                  //  Toast.makeText(RegistrationActivity.this, "onDataChange - REG", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onDataChange: mAuth uid="+mAuth.getUid());
                    Log.d(TAG, "onDataChange: data key="+data.getKey());
                    if (data.getKey().equals(mAuth.getUid()))
                    {
                        editor.putBoolean("IS_SAVED",true);
                        editor.commit();
                        return;
                      //  Toast.makeText(RegistrationActivity.this, " Configure Successful", Toast.LENGTH_SHORT).show();
                    }else {
                        editor.putBoolean("IS_SAVED",false);
                        editor.commit();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userRef.addValueEventListener(userSaved);
        userRef.addChildEventListener(childEventListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null)
        {
            //Toast.makeText(this, "already connected."+user.getPhoneNumber(), Toast.LENGTH_LONG).show();
            Intent intent;
            isSaved();
           // Toast.makeText(this, ""+mSharedPreferences.getBoolean("IS_SAVED",false), Toast.LENGTH_SHORT).show();
            if (mSharedPreferences.getBoolean("IS_SAVED",false))
            {
                intent = new Intent(this,MainActivity.class);
            }else {
                intent = new Intent(this,ConfigureActivity.class);
            }

            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
