package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

public class ForRentActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String TAG = "ERROR:";
    private ImageView mImageView;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private List<AgentPost> post_list;
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore firebaseFirestore;
    private RelativeLayout mRelativeLayout;
    private AgentPostRecyclerAdapter agentPostRecyclerAdapter;

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
        setContentView(R.layout.activity_for_rent);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mProgressBar = findViewById(R.id.progressBarMain);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mRelativeLayout = findViewById(R.id.relativelayout);
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.info_text);
        post_list = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerView);
        agentPostRecyclerAdapter = new AgentPostRecyclerAdapter(post_list, ForRentActivity.this);
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
        firebaseFirestore.collection("Posts").whereEqualTo("category", "For Rent").limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e!=null){
                    Log.d(TAG,"Error:"+e.getMessage());
                } else {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);
                    }
                    for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            String blogPostID = doc.getDocument().getId();
                            AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                            post_list.add(agentPost);
                            agentPostRecyclerAdapter.notifyDataSetChanged();
                            mImageView.setVisibility(View.GONE);
                            mTextView.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        Query mQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", "For Rent");
        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()){
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
                break;

            case R.id.search:
                Intent intent = new Intent(this, FilterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForRentActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_location2, menu);
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setQueryHint("Search Location...");
//        searchView.setOnQueryTextListener(this);
        return true;
    }
    public void loadMorePosts(){
        firebaseFirestore.collection("Posts").whereEqualTo("category", "For Rent").startAfter(lastVisible).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
    @Override
    public boolean onQueryTextSubmit(String query) {
        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String blogPostID = doc.getDocument().getId();
                        AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                        post_list.add(agentPost);
                        agentPostRecyclerAdapter.notifyDataSetChanged();
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mRelativeLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
        final List<AgentPost> filteredList = filter(post_list, query);
        agentPostRecyclerAdapter.updateList(filteredList);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<AgentPost> filteredList = filter(post_list, newText);
        agentPostRecyclerAdapter.updateList(filteredList);
        return true;
    }
    private List<AgentPost> filter (List<AgentPost> pi, String query){
        query = query.toLowerCase();
        final List<AgentPost> filteredList = new ArrayList<>();
        for(AgentPost agentPost: pi){
            final String text = agentPost.getLocation().toLowerCase();
            if(text.contains(query)){
                filteredList.add(agentPost);
            }
        }
        return filteredList;
    }
}
