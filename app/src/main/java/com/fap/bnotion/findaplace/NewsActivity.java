package com.fap.bnotion.findaplace;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<News> news_list;
    private DatabaseReference mDatabaseMessages;
    private ProgressBar mProgressBar;
    private AdView mAdView;
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
        setContentView(R.layout.activity_news);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        MobileAds.initialize(this, "ca-app-pub-8534200199388023~7161551446");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        news_list = new ArrayList<>();
        mRecyclerView = findViewById(R.id.news_recyclerView);
        mProgressBar = findViewById(R.id.progressBarMain);
        mDatabaseMessages = FirebaseDatabase.getInstance().getReference().child("News");
        mDatabaseMessages.keepSynced(true);
        mDatabaseMessages.keepSynced(true);
        mRecyclerView =  findViewById(R.id.news_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    public void onBackPressed() {
        Intent intent = new Intent(NewsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<News, NewsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<News,
                NewsViewHolder>(News.class,
                R.layout.message_item,
                NewsViewHolder.class, mDatabaseMessages) {
            @Override
            protected void populateViewHolder(final NewsViewHolder viewHolder, final News model, int position) {
                final String title = model.getTitle();
                final String subtitle = model.getSubtitle();
                final String image = model.getImage();
                final String webLink = model.getWebLink();
                final String message = model.getMessage();
                viewHolder.setTitle(title);
                viewHolder.setStatus(subtitle);
                viewHolder.setImage(image, getApplicationContext());
                viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("subtitle", subtitle);
                        intent.putExtra("image", image);
                        intent.putExtra("webLink", webLink);
                        intent.putExtra("message", message);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                    }
                });
                mProgressBar.setVisibility(View.GONE);
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        CardView parentLayout;

        public  NewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            parentLayout = itemView.findViewById(R.id.parentView);
        }

        public void setTitle(String title) {
            TextView title_msg = mView.findViewById(R.id.title);
            title_msg.setText(title);
        }

        public void setStatus(String subtitle) {
            TextView subtitle_msg = mView.findViewById(R.id.subtitle);
            subtitle_msg.setText(subtitle);
        }

        public void setImage(String image, Context ctx) {
            ImageView imageView = mView.findViewById(R.id.image);
            RequestOptions placeHolderRequest = new RequestOptions();
            placeHolderRequest.placeholder(R.drawable.placeholder);
            Glide.with(ctx).setDefaultRequestOptions(placeHolderRequest).load(image).into(imageView);
        }
    }
}
