package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AgentDashboardActivity extends AppCompatActivity {
    private static final String TAG = "AgentDashboardActivity";
    private RecyclerView mRecyclerView;
    private List<AgentPost> post_list;
    private FirebaseFirestore firebaseFirestore;
    private AgentPostItemRecyclerAdapter agentPostItemRecyclerAdapter;
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private TextView mTextView;
    private FirebaseAuth firebaseAuth;
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
        setContentView(R.layout.activity_agent_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        post_list = new ArrayList<>();
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.info_text);
        mRecyclerView = findViewById(R.id.dashboard_recyclerView);
        mProgressBar = findViewById(R.id.progressBarMain);
        agentPostItemRecyclerAdapter = new AgentPostItemRecyclerAdapter(post_list, AgentDashboardActivity.this);
        agentPostItemRecyclerAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(agentPostItemRecyclerAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Posts").whereEqualTo("user_id", user_id).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if(e!= null){
                    Log.d(TAG,"Error:"+e.getMessage());
                } else {
                    for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            String blogPostID = doc.getDocument().getId();
                            AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                            post_list.add(agentPost);
                            agentPost.setId(doc.getDocument().getId());
                            agentPostItemRecyclerAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.GONE);
                            mImageView.setVisibility(View.GONE);
                            mTextView.setVisibility(View.GONE);
                        }
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
            case R.id.all_agents:
                onBackPressed();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_agents, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
