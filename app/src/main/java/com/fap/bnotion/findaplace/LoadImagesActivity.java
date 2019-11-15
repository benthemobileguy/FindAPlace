package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class LoadImagesActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_load_images);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        TextView mToolbarText = findViewById(R.id.toolbar_text);
        ViewPager viewPager = findViewById(R.id.view_pager);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Intent intentData = getIntent();
        ArrayList<String> image_urls = intentData.getStringArrayListExtra("imageURLS");
        String toolbar_text = intentData.getStringExtra("toolbarText");
        if(image_urls != null){
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, image_urls);
            viewPager.setAdapter(viewPagerAdapter);
            mToolbarText.setText(toolbar_text + " Image(s)");
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
