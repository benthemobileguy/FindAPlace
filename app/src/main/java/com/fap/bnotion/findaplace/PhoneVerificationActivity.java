package com.fap.bnotion.findaplace;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {
    private EditText  mPhoneNumber, mVerificationCode;
    private ProgressBar mProgressBar;
    private Button mButton;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private TextView mVerificationInfo;
    private String name, email, mobileNumber, mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // locking out landscape screen orientation for mobiles
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // locking out portait screen orientation for tablets
        } if(getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_phone_verification);
        mAuth = FirebaseAuth.getInstance();
        mProgressBar = findViewById(R.id.progressBar);
        mButton = findViewById(R.id.verification_button);
        mPhoneNumber = findViewById(R.id.phone_number);
        mVerificationCode = findViewById(R.id.verification_code);
        mVerificationInfo = findViewById(R.id.verification_info);
        Toast.makeText(this, "tap the button", Toast.LENGTH_LONG).show();
        //receiving extras from previous activity
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        email = extras.getString("email");
        mobileNumber = extras.getString("mobileNumber");
        String mobileNumberSubstring = mobileNumber.substring(1,11);
        mPhoneNumber.setText("+234" + mobileNumberSubstring);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mPhoneNumber.getText().toString();
                String networkProviderSubstring = phoneNumber.substring(0,4);
                if(mButton.getText().toString().equals("SEND VERIFICATION") && networkProviderSubstring.equals("+234")){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mButton.setEnabled(false);
                    mVerificationCode.setEnabled(true);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                            60, TimeUnit.SECONDS,
                            PhoneVerificationActivity.this,
                            mCallBacks);
                } else if(!networkProviderSubstring.equals("+234")){
                    Toast.makeText(PhoneVerificationActivity.this, "The phone number your entered is invalid!", Toast.LENGTH_SHORT).show();
                }else if(mButton.getText().toString().equals("VERIFY CODE") && !mVerificationCode.getText().toString().equals("")) {
                    mButton.setEnabled(true);
                    mProgressBar.setVisibility(View.VISIBLE);
                    String verificationCode = mVerificationCode.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneVerificationActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                mProgressBar.setVisibility(View.INVISIBLE);
                mVerificationInfo.setText("A verification code has been sent to your Phone Number");
                mButton.setText(R.string.verify_code);
                Toast.makeText(PhoneVerificationActivity.this, "code has been sent!", Toast.LENGTH_SHORT).show();
                mButton.setEnabled(true);
                // ...
            }

        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String phoneNumber = mPhoneNumber.getText().toString();
                            String phoneNumberSubstring = phoneNumber.substring(4,14);
                            Intent continueRegistration = new Intent(PhoneVerificationActivity.this, RegisterAgentOneActivity.class);
                            continueRegistration.putExtra("name", name);
                            continueRegistration.putExtra("email", email);
                            continueRegistration.putExtra("mobileNumber", "0"+phoneNumberSubstring);
                            startActivity(continueRegistration);
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            Toast.makeText(PhoneVerificationActivity.this, "Verified! You can now continue the registration process", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(PhoneVerificationActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage("You are about to quit the registration process. Are you sure about this?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    finish();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

}
