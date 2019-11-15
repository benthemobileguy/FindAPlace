package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class NewsDetailActivity extends AppCompatActivity {
    private TextView mTitle, mSubtitle, mDate, mMessageText;
    private ImageView imageView;
    private String title, subtitle, image, webLink, message;
    private Button mWebLinkBtn;
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
        setContentView(R.layout.activity_news_detail);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //Get String Extras
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        subtitle = intent.getStringExtra("subtitle");
        image = intent.getStringExtra("image");
        webLink = intent.getStringExtra("webLink");
        message = intent.getStringExtra("message");
        mTitle = findViewById(R.id.title);
        mSubtitle = findViewById(R.id.subtitle);
        mDate = findViewById(R.id.date);
        mMessageText = findViewById(R.id.message_text);
        imageView = findViewById(R.id.image);
        mWebLinkBtn = findViewById(R.id.webLinkBtn);
        mTitle.setText(title);
        mSubtitle.setText(subtitle);
        mMessageText.setText(message);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.placeholder);
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(image).into(imageView);
        mWebLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(webLink));
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, webLink + ": " + title + " -- " + subtitle);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
