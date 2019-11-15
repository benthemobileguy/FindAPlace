package com.fap.bnotion.findaplace;

import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText mEmail;
    private FirebaseAuth mAuth;
    private String emailInput;
    private ProgressBar mProgressBar;
    private ConstraintLayout mParentLayout;
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
        setContentView(R.layout.activity_forgot_password);
        Toolbar mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.registered_email);
        mProgressBar = findViewById(R.id.forgot_pass_progressBar);
        mParentLayout = findViewById(R.id.activity_forgot);
        Button mResetPassword = findViewById(R.id.reset_password_button);
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailInput = mEmail.getText().toString().trim();
                if(!TextUtils.isEmpty(emailInput) && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(emailInput).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Snackbar mSnackbar = Snackbar.make(mParentLayout, "Your password has been sent to your email: "+ emailInput, Snackbar.LENGTH_LONG);
                                mSnackbar.show();
                            } else {
                                String  error = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    mEmail.setError("Valid email required");
                }
            }
        });
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
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
