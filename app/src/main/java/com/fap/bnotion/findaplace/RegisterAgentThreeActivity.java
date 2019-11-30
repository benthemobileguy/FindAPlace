package com.fap.bnotion.findaplace;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class RegisterAgentThreeActivity extends AppCompatActivity {
    private ImageView mDocumentUploadImage;
    private ProgressBar mProgressBarUpload;
    private Button mFinishButton;
    private String name, email, mobileNumber, address, sex, passport;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private UploadTask mUploadTask;
    private Uri mainImageURI = null;
    private FirebaseAuth mAuth;
    private String document = "document";
    private String document_url = "null";
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BringImagePicker();
                } else {
                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(RegisterAgentThreeActivity.this);
    }

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
        setContentView(R.layout.activity_register_agent_three);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mAuth = FirebaseAuth.getInstance();
        mDocumentUploadImage = findViewById(R.id.document_image);
        mProgressBarUpload = findViewById(R.id.progressBar);
        mFinishButton = findViewById(R.id.continue_button);
        mFinishButton.setAlpha(0.5f);
        mFinishButton.setEnabled(false);
        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            name = extras.getString("name");
            email = extras.getString("email");
            mobileNumber = extras.getString("mobileNumber");
            address = extras.getString("address");
            sex = extras.getString("sex");
            passport = extras.getString("passport");
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child(Constants.AGENTS_KEY).child(user_id);
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("image", passport);
                map.put("status", "Pending verification");
                map.put("thumb_image", "default");
                map.put("email", email);
                map.put("mobile number", mobileNumber);
                map.put("verified", "no");
                map.put("address", address);
                map.put("document_type", document);
                map.put("user_id", user_id);
                map.put("document", document_url);
                map.put("sex", sex);
                map.put("date", FieldValue.serverTimestamp().toString());
                ProgressDialog.show(RegisterAgentThreeActivity.this, "", "Finishing up...", true);
                current_user_db.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent accountIntent = new Intent(RegisterAgentThreeActivity.this, NoticeActivity.class);
                                    startActivity(accountIntent);
                                    finish();
                                }
                            }, 3000);
                        }else{
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegisterAgentThreeActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        blickImage();
        mDocumentUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unBlinkClickMe();
                setProfileImage();
            }
        });
        RadioGroup document_options = findViewById(R.id.document_options);
        document_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.national_id:
                        document = "National Id Card";
                        break;
                    case R.id.drivers_license:
                        document = "Driver's Licence";
                        break;
                    case R.id.international_passport:
                        document = "International Passport";
                        break;
                }
            }
        });
    }

    private void setProfileImage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(RegisterAgentThreeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(RegisterAgentThreeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                BringImagePicker();
            }
        } else{
            BringImagePicker();
        }
    }

    private void unBlinkClickMe() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(60);
        anim.setStartOffset(30);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.RELATIVE_TO_SELF);
        mDocumentUploadImage.startAnimation(anim);
    }

    private void blickImage() {
        Animation anim = new AlphaAnimation(0.5f, 1.0f);
        anim.setDuration(500);
        anim.setStartOffset(30);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        mDocumentUploadImage.startAnimation(anim);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                mDocumentUploadImage.setImageURI(mainImageURI);
                mProgressBarUpload.setVisibility(View.VISIBLE);
                final String user_id = mAuth.getCurrentUser().getUid();
                StorageReference image_path = storageReference.child(Constants.DOCUMENT_KEY).child(user_id + ".jpg");
                mUploadTask = image_path.putFile(mainImageURI);
                Task<Uri> urlTask = mUploadTask.continueWithTask(task -> {
                    if(task.isSuccessful()) {
                    }

                    // Continue with the task to get the download URL
                    return image_path.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final Uri download_uri = task.getResult();
                        Map<String, String> userMap = new HashMap<>();
                        userMap.put(document, download_uri.toString());
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("mobile number", mobileNumber);
                        userMap.put("address", address);
                        userMap.put("sex", sex);
                        firebaseFirestore.collection(Constants.USER_KEY).document(user_id).set(userMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()) {
                                    String  error = task.getException().getMessage();
                                    Toast.makeText(RegisterAgentThreeActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();

                                } else {
                                    mFinishButton.setAlpha(1.0f);
                                    mFinishButton.setEnabled(true);
                                    Toast.makeText(RegisterAgentThreeActivity.this, "Success", Toast.LENGTH_LONG).show();
                                }
                                mProgressBarUpload.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                String error = result.getError().toString();
                Toast.makeText(RegisterAgentThreeActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
