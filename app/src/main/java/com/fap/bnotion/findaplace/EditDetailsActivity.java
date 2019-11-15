package com.fap.bnotion.findaplace;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class EditDetailsActivity extends AppCompatActivity {
private EditText mName, mEmail, mAddress, mPhoneNumber;
private Button mButton;
private String name = "name";
private String email = "email";
private String address = "address";
private String phone = "phone";
private ProgressBar mProgressBar;
private DatabaseReference databaseReference, databaseReferenceAgents;
private String user_id;
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
        setContentView(R.layout.activity_edit_details);
        Toolbar mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.USER_KEY).child(user_id);
        databaseReferenceAgents = FirebaseDatabase.getInstance().getReference().child(Constants.AGENTS_KEY).child(user_id);
        Intent intent = getIntent();
        mName = findViewById(R.id.reg_username);
        mEmail = findViewById(R.id.reg_email);
        mAddress = findViewById(R.id.reg_address);
        mPhoneNumber = findViewById(R.id.reg_number);
        mButton = findViewById(R.id.update_details);
        mProgressBar = findViewById(R.id.reg_progressBar);
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        address = intent.getStringExtra("address");
        phone = intent.getStringExtra("phone");
        mName.setText(name);
        mEmail.setText(email);
        if (phone==null) {
            mPhoneNumber.setText("phone");
        } else {
            mPhoneNumber.setText(phone);
        }
        if (address==null) {
            mAddress.setVisibility(View.GONE);
        } else {
            mAddress.setText(address);
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = mName.getText().toString();
                email = mEmail.getText().toString();
                phone = mPhoneNumber.getText().toString();
                if (mAddress.isShown()) {
                    address = mAddress.getText().toString();
                }
                if(mAddress.getVisibility() == View.GONE){
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) && phone.length() == 11) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        Map<String, String> map = new HashMap<>();
                        map.put("username", name);
                        map.put("email", email);
                        map.put("phone", phone);
                        databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditDetailsActivity.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(EditDetailsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    } else if (TextUtils.isEmpty(name)) {
                        mName.setError("Empty Field");
                    } else if (TextUtils.isEmpty(email)) {
                        mEmail.setError("Empty Field");
                    } else if (TextUtils.isEmpty(phone)) {
                        mPhoneNumber.setError("Invalid Number");
                    } else if(mPhoneNumber.length() != 11){
                        mPhoneNumber.setError("Invalid Number");
                    }
                } else {
                    if (TextUtils.isEmpty(name)){
                        mName.setError("Empty Field");
                    } else if(TextUtils.isEmpty(email)){
                        mEmail.setError("Empty Field");
                    } else if(TextUtils.isEmpty(phone)){
                        mPhoneNumber.setError("Invalid Number");
                    }else if(mPhoneNumber.length() != 11){
                        mPhoneNumber.setError("Invalid Number");
                    } else if(TextUtils.isEmpty(address)){
                        mAddress.setError("Empty Field");
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);
                        //update selcted values
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("email", email);
                        map.put("mobile number", phone);
                        map.put("address", address);
                        databaseReferenceAgents.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditDetailsActivity.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(EditDetailsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }

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
