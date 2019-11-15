package com.fap.bnotion.findaplace;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterAgentOneActivity extends AppCompatActivity {
    private TextView  mVerificationText;
    private ProgressBar  mProgressBarMain;
    private EditText mName, mEmail, mMobileNumber, mAddress;
    private Button mContinueButton;
    private FirebaseAuth mAuth;
    private Handler mHandler;
    private String name, email, mobileNumber;
    String sex = "male";
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
        setContentView(R.layout.activity_register_agent_one);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        // firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // set all variables to their ID's
        mProgressBarMain = findViewById(R.id.progressBarCircle);
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mVerificationText = findViewById(R.id.verification_text);
        mMobileNumber = findViewById(R.id.mobile_number);
        mAddress = findViewById(R.id.address);
        RadioGroup sex_options = findViewById(R.id.sex_options);
        sex_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               switch (checkedId){
                   case R.id.male:
                       sex = "male";
                       break;
                   case R.id.female:
                       sex = "female";
                       break;
               }
           }
       });
        mContinueButton = findViewById(R.id.continue_button);
        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            name = extras.getString("name");
            email = extras.getString("email");
            mobileNumber = extras.getString("mobileNumber");
            mName.setText(name);
            mEmail.setText(email);
            mMobileNumber.setText(mobileNumber);
            mMobileNumber.setEnabled(false);
        }
        mMobileNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if(s.length() == 11){
                    mMobileNumber.setEnabled(false);
                    mProgressBarMain.setVisibility(View.VISIBLE);
                    mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String name = mName.getText().toString();
                            String email = mEmail.getText().toString();
                            String mobileNumber = mMobileNumber.getText().toString();
                            Intent verificationIntent = new Intent(RegisterAgentOneActivity.this, PhoneVerificationActivity.class);
                            verificationIntent.putExtra("name", name);
                            verificationIntent.putExtra("email", email);
                            verificationIntent.putExtra("mobileNumber", mobileNumber);
                            startActivity(verificationIntent);
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            finish();
                        }
                    }, 3000);
                }
            //    String user_id = mAuth.getCurrentUser().getUid();
//                userRef.child(user_id).orderByChild("mobileNumber").equalTo(mobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fetching string values
                String name = mName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String mobileNumber = mMobileNumber.getText().toString().trim();
                String address = mAddress.getText().toString().trim();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(mobileNumber) && mAddress.length() > 15 && android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches() && mMobileNumber.length() == 11){
                    Intent intent = new Intent(RegisterAgentOneActivity.this, RegisterAgentTwoActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("mobileNumber", mobileNumber);
                    intent.putExtra("address", address);
                    intent.putExtra("sex", sex);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    finish();
                } else if(TextUtils.isEmpty(name)){
                    mName.setError("Empty Field");
                    Toast.makeText(RegisterAgentOneActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(email)){
                    mEmail.setError("Empty Field");
                    Toast.makeText(RegisterAgentOneActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                }  else if (mAddress.length() < 15){
                    mAddress.setError("Add more detail");
                    Toast.makeText(RegisterAgentOneActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                } else if (mMobileNumber.length() != 11){
                    mMobileNumber.setError("Invalid phone number");
                } else if(TextUtils.isEmpty(address)){
                    mAddress.setError("Empty field");
                } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
                    mAddress.setError("Invalid email");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            mMobileNumber.setEnabled(false);
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                sendToLogin();
                mProgressBarMain.setVisibility(View.VISIBLE);
                mName.setEnabled(false);
                mEmail.setEnabled(false);
                mAddress.setEnabled(false);
                mVerificationText.setEnabled(false);
                }
            }, 5000);
        }
    }

    private void sendToLogin() {
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RegisterAgentOneActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
     Toast.makeText(RegisterAgentOneActivity.this, "You must be logged in to commence registration", Toast.LENGTH_LONG).show();
            }
        }, 3000);

    }

    @Override
    public void onBackPressed() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage("You are about to quit the registration process. Are you sure about this?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mHandler !=null){
                            mHandler.removeCallbacksAndMessages(null);
                        }
                        finish();
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
