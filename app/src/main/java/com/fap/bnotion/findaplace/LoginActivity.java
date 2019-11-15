package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private ImageView  mImgLogo;
    EditText loginEmail;
    EditText loginPass;
    Button loginButton;
    private LinearLayout mLinearLogin;
    private LinearLayout mDotLayout;
    private RelativeLayout mSkipLogin, mFieldsLayout;
    private TextView[] mDots;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    SharedPreferences.Editor editor;
    private Boolean isGuestButtonClicked = false;
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
        setContentView(R.layout.activity_login);
        Toolbar mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mLinearLogin = findViewById(R.id.linearLogin);
        mSkipLogin = findViewById(R.id.skip_login);
        mFieldsLayout = findViewById(R.id.fieldsLayout);
        loginEmail = findViewById(R.id.email);
        loginPass = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        // Skip Login
        TextView skipLogin = findViewById(R.id.skip_login_text);
        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                isGuestButtonClicked = true;
                intent.putExtra("isGuestButtonClicked", isGuestButtonClicked);
                startActivity(intent);
                //set value with shared preferences
                SharedPreferences mPref = getApplicationContext().getSharedPreferences("myPref", MODE_PRIVATE);
                editor = mPref.edit();
                editor.putBoolean("isGuestButtonClicked",isGuestButtonClicked);
                editor.apply();
            }
        });
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Button googleLoginBtn = findViewById(R.id.gmail_sign_in);
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLoginLogic();
            }
        });
        //on click listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmailText = loginEmail.getText().toString().trim();
                String loginPassText = loginPass.getText().toString();
                if(!TextUtils.isEmpty(loginEmailText) && !TextUtils.isEmpty(loginPassText)){
                    mAuth.signInWithEmailAndPassword(loginEmailText, loginPassText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendToMain();
                               clearSharedPreference();
                            } else {
                                String  errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else if(TextUtils.isEmpty(loginEmailText)){
                    loginEmail.setError("Empty Field");
                } else if(TextUtils.isEmpty(loginPassText)){
                    loginPass.setError("Empty Field");
                }

            }
        });
        Button mNewAccount = findViewById(R.id.new_account);
        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });

        TextView mForgotPassword = findViewById(R.id.forgot_password);
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgot_password = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgot_password);
            }
        });
    }

    private void clearSharedPreference() {
        if(editor != null){
            editor.clear().apply();
        }
    }

    private void googleLoginLogic() {
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null){
            sendToMain();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            clearSharedPreference();
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                            Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                        } else {
                            String  errorMessage = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();
        isGuestButtonClicked = false;
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
