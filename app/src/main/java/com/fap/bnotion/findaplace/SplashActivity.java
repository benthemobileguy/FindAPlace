package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    public static int SPLASH_TIME_OUT = 7000;
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
        setContentView(R.layout.activity_splash);
        //setLogoAnimation();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                launchMain();
            }
        }, SPLASH_TIME_OUT);
        setLogoAnimation();
    }

    private void setLogoAnimation() {
        ImageView mImageLogo = findViewById(R.id.logo_image);
        Animation zm = AnimationUtils.loadAnimation(this, R.anim.shake);
        mImageLogo.startAnimation(zm);
    }

    private void launchMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
