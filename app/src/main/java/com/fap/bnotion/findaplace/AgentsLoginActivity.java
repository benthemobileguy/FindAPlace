package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class AgentsLoginActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private EditText mPhoneNumber, mVerificationCode;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private Button login_button, resend_btn;
    private String  mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private TextView mVerificationInfo;
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
        setContentView(R.layout.activity_agents_login);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mAuth = FirebaseAuth.getInstance();
        mProgressBar = findViewById(R.id.progressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.AGENTS_KEY);
        mPhoneNumber = findViewById(R.id.phone_number);
        mVerificationCode = findViewById(R.id.verification_code);
        mVerificationInfo = findViewById(R.id.verification_info);
        login_button = findViewById(R.id.login_button);
        resend_btn = findViewById(R.id.resend_button);
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneNumber = mPhoneNumber.getText().toString();
                String mobileNumberSubstring = phoneNumber.substring(1,11);
                final String phoneNumberData = "+234" + mobileNumberSubstring;
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                mDatabase.orderByChild("mobile number").equalTo(phoneNumber).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            mProgressBar.setVisibility(View.VISIBLE);
                            login_button.setEnabled(false);
                            mVerificationCode.setEnabled(true);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumberData,
                                    60, TimeUnit.SECONDS,
                                    AgentsLoginActivity.this,
                                    mCallBacks);
                        } else {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.relativeLayout),
                                    "Phone Number not found on database", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneNumber = mPhoneNumber.getText().toString();
                if (login_button.getText().toString().equals("SEND VERIFICATION") && phoneNumber.length() == 11){
                    String mobileNumberSubstring = phoneNumber.substring(1,11);
                    final String phoneNumberData = "+234" + mobileNumberSubstring;
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        mDatabase.orderByChild("mobile number").equalTo(phoneNumber).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    login_button.setEnabled(false);
                                    mVerificationCode.setEnabled(true);
                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumberData,
                                            60, TimeUnit.SECONDS,
                                            AgentsLoginActivity.this,
                                            mCallBacks);
                                } else {
                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.relativeLayout),
                                            "Phone Number not found on database", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                } else if(login_button.getText().toString().equals("VERIFY CODE") && !mVerificationCode.getText().toString().equals("")) {
                    login_button.setEnabled(true);
                    mProgressBar.setVisibility(View.VISIBLE);
                    String verificationCode = mVerificationCode.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                } else if(phoneNumber.length() != 11){
                    Toast.makeText(AgentsLoginActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AgentsLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                resend_btn.setAlpha(1);
                resend_btn.setEnabled(true);
                mProgressBar.setVisibility(View.INVISIBLE);
                mVerificationInfo.setText("A verification code has been sent to your Phone Number");
                login_button.setText(R.string.verify_code);
                Toast.makeText(AgentsLoginActivity.this, "code has been sent!", Toast.LENGTH_SHORT).show();
                login_button.setEnabled(true);
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
                            Intent intent = new Intent(AgentsLoginActivity.this, NewPostActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            Toast.makeText(AgentsLoginActivity.this, "login success", Toast.LENGTH_LONG).show();
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(AgentsLoginActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(AgentsLoginActivity.this, AgentsActivity.class);
            startActivity(intent);
            finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
