package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AgentPostActivity extends AppCompatActivity {
    private TextView toolbarText;
    private RecyclerView mRecyclerView;
    private List<AgentPost> post_list;
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore firebaseFirestore;
    private AgentPostRecyclerAdapter agentPostRecyclerAdapter;
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private RelativeLayout mRelativeLayout;
    private TextView mTextView;
    private String user_id;
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
        setContentView(R.layout.activity_agent_post);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        String username = intent.getStringExtra("username");
        toolbarText = findViewById(R.id.toolbar_text);
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.info_text);
        mRelativeLayout = findViewById(R.id.relativelayout);
        toolbarText.setText(username + " (Posts)");
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        post_list = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBarMain);
        agentPostRecyclerAdapter = new AgentPostRecyclerAdapter(post_list, AgentPostActivity.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(agentPostRecyclerAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean reachedBottom = !mRecyclerView.canScrollVertically(1);
                if(reachedBottom){
                    Snackbar.make(mRelativeLayout,"seeking posts...",Snackbar.LENGTH_LONG).show();
                    loadMorePosts();
                }
            }
        });
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id", user_id).limit(5);
        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if(queryDocumentSnapshots.size() > 0){
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);
                }
                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String blogPostID = doc.getDocument().getId();
                        AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                        post_list.add(agentPost);
                        agentPostRecyclerAdapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
        Query mQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id", user_id);
        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()){
                    mProgressBar.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    mTextView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });


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
       finish();
       overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

    }
    public void loadMorePosts(){
        Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id", user_id).startAfter(lastVisible).limit(5);
        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);
                    for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            String blogPostID = doc.getDocument().getId();
                            AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                            post_list.add(agentPost);
                            agentPostRecyclerAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                }
            }
        });
    }

}
