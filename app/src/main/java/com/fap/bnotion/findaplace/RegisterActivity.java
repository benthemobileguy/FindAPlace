package com.fap.bnotion.findaplace;

import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText reg_email_field, reg_pass_field, reg_confirm_pass_field,reg_username, reg_phone;
    private ProgressBar reg_progress;
    private FirebaseAuth mAuth;

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
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        reg_email_field = findViewById(R.id.reg_email);
        reg_pass_field = findViewById(R.id.reg_password);
        reg_username = findViewById(R.id.reg_username);
        reg_phone = findViewById(R.id.reg_number);
        Toolbar mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button already_have_account = findViewById(R.id.already_have_account);
        already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        reg_confirm_pass_field = findViewById(R.id.confirm_password);
        Button reg_btn = findViewById(R.id.reg_button);
        reg_progress = findViewById(R.id.reg_progressBar);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = reg_email_field.getText().toString().trim();
                String pass = reg_pass_field.getText().toString();
                String confirm_pass = reg_confirm_pass_field.getText().toString();
                final String username = reg_username.getText().toString();
                final String phone = reg_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(phone) && reg_phone.length() == 11 && !TextUtils.isEmpty(confirm_pass) && !TextUtils.isEmpty(username)) {
                    if (pass.equals(confirm_pass)) {
                        reg_progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String user_id = mAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child(Constants.USER_KEY).child(user_id);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("username", username);
                                    map.put("email", email);
                                    map.put("phone", phone);
                                    current_user_db.setValue(map);
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "success", Toast.LENGTH_SHORT).show();
                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                                reg_progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else if (!pass.equals(confirm_pass)){
                        Toast.makeText(RegisterActivity.this, "passwords doesn't match!", Toast.LENGTH_LONG).show();
                    }

                } else if (TextUtils.isEmpty(email)) {
                    reg_email_field.setError("Empty Field");
                } else if (TextUtils.isEmpty(pass)) {
                    reg_pass_field.setError("Empty Field");

                } else if (TextUtils.isEmpty(confirm_pass)) {
                    reg_confirm_pass_field.setError("Empty Field");
                } else if (TextUtils.isEmpty(username)){
                    reg_username.setError("Empty Field");
                } else if(TextUtils.isEmpty(phone)){
                    reg_phone.setError("Empty Field");
                } else if (reg_phone.length()!= 11){
                    reg_phone.setError("Error");
                    Toast.makeText(RegisterActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                };
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
