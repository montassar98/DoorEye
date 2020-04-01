package com.montassarselmi.dooreye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactUsActivity extends AppCompatActivity {

    private static final String TAG = ContactUsActivity.class.getSimpleName();
    private EditText edtName, edtEmail, edtMessage;
    private FloatingActionButton fabSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_contact_us);

        edtName = (EditText) findViewById(R.id.edt_contact_us_name);
        edtEmail = (EditText) findViewById(R.id.edt_contact_us_email);
        edtMessage = (EditText) findViewById(R.id.edt_contact_us_message);
        fabSent = (FloatingActionButton) findViewById(R.id.fab_sent);

        fabSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void sendMessage()
    {
        Log.d(TAG,"Send email");

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String message = edtMessage.getText().toString().trim();
        if (name.isEmpty()){
            edtName.setError(getResources().getString(R.string.empty_fullname));
            edtName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError(getResources().getString(R.string.error_email));
            edtEmail.requestFocus();
            return;
        }
        if (email.isEmpty()){
            edtEmail.setError(getResources().getString(R.string.empty_email));
            edtEmail.requestFocus();
            return;
        }
        if (message.isEmpty()){
            edtEmail.setError(getResources().getString(R.string.empty_message));
            edtMessage.requestFocus();
            return;
        }

        String[] TO = {"dooreye98@gmail.com"};
        //String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DoorEye User: "+ name);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_mail)));
            finish();
            Log.i(TAG, "email sent");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactUsActivity.this,
                    getResources().getString(R.string.error_mail_sending), Toast.LENGTH_SHORT).show();
        }


    }
}
