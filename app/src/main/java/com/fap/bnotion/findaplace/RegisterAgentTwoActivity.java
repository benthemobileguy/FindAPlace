package com.fap.bnotion.findaplace;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class RegisterAgentTwoActivity extends AppCompatActivity {
    private ProgressBar mProgressBarUpload, mProgressBarMain;
    private Button mContinueButton;
    private String name, email, mobileNumber, address, sex;
    private ImageView mImagePassport;
    private FirebaseAuth firebaseAuth;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private Uri mainImageURI = null;
    private String passport = "null";
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
                .start(RegisterAgentTwoActivity.this);
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
        setContentView(R.layout.activity_register_agent_two);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            name = extras.getString("name");
            email = extras.getString("email");
            mobileNumber = extras.getString("mobileNumber");
            address = extras.getString("address");
            sex = extras.getString("sex");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mImagePassport = findViewById(R.id.passport_image);
        mContinueButton = findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAgentTwoActivity.this, RegisterAgentThreeActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("mobileNumber", mobileNumber);
                intent.putExtra("address", address);
                intent.putExtra("sex", sex);
                intent.putExtra("passport", passport);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
        mProgressBarUpload = findViewById(R.id.progressBar);
        mContinueButton.setEnabled(false);
        blickImage();
        mImagePassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unBlinkClickMe();
                setProfileImage();
            }
        });
    }

    private void unBlinkClickMe() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(60);
        anim.setStartOffset(30);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.RELATIVE_TO_SELF);
        mImagePassport.startAnimation(anim);
    }


    private void setProfileImage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(RegisterAgentTwoActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(RegisterAgentTwoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                BringImagePicker();
            }
        } else{
            BringImagePicker();
        }
    }

    private void blickImage() {
        Animation anim = new AlphaAnimation(0.5f, 1.0f);
        anim.setDuration(500);
        anim.setStartOffset(30);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        mImagePassport.startAnimation(anim);
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
                mImagePassport.setImageURI(mainImageURI);
                mProgressBarUpload.setVisibility(View.VISIBLE);
                final String user_id = firebaseAuth.getCurrentUser().getUid();
                StorageReference image_path = storageReference.child(Constants.PASSPORT).child(user_id + ".jpg");
                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            final Uri download_uri = task.getResult().getDownloadUrl();
                            Map<String, String> userMap = new HashMap<>();
                            userMap.put(Constants.PASSPORT, download_uri.toString());
                            firebaseFirestore.collection(Constants.USER_KEY).document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()) {
                                        String  error = task.getException().getMessage();
                                        Toast.makeText(RegisterAgentTwoActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(RegisterAgentTwoActivity.this, "uploaded successfully!", Toast.LENGTH_SHORT).show();
                                        passport = download_uri.toString();
                                        mContinueButton.setEnabled(true);
                                    }
                                mProgressBarUpload.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(RegisterAgentTwoActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                String error = result.getError().toString();
                Toast.makeText(RegisterAgentTwoActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
