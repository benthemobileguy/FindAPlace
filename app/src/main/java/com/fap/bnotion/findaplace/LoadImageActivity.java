package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class LoadImageActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_load_image);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        TextView mToolbarText = findViewById(R.id.toolbar_text);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Intent intentData = getIntent();
        String imageURL = intentData.getStringExtra("imageURL");
        String toolbar_text = intentData.getStringExtra("toolbarText");
        ImageView mImageView = findViewById(R.id.image_preview);
        ProgressBar mProgresssBar = findViewById(R.id.progressBarPreview);
        Glide.with(this).load(imageURL).into(mImageView);
        mToolbarText.setText(toolbar_text + " Image(s)");
        if(mImageView.isShown()){
            mProgresssBar.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }
}
