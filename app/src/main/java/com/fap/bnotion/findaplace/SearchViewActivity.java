package com.fap.bnotion.findaplace;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchViewActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
private AgentPostRecyclerAdapter agentPostRecyclerAdapter;
private ImageView imageView;
private TextView textView;
private List<AgentPost> post_list;
private RecyclerView mRecyclerView;
private FirebaseFirestore firebaseFirestore;
private ProgressBar mProgressBar;
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
        setContentView(R.layout.activity_search_view);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBarMain);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mRecyclerView = findViewById(R.id.recyclerView);
        post_list = new ArrayList<>();
        agentPostRecyclerAdapter = new AgentPostRecyclerAdapter(post_list, SearchViewActivity.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(agentPostRecyclerAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();

            imageView = findViewById(R.id.image);
            textView = findViewById(R.id.info_text);
        Query query = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
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
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            AgentPost agentPost = queryDocumentSnapshot.toObject(AgentPost.class);
                            post_list.add(agentPost);
                            agentPostRecyclerAdapter.notifyDataSetChanged();
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    } else{
                        Toast.makeText(SearchViewActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                    if(task.getResult().isEmpty()){
                        mProgressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setVisibility(View.GONE);
                }
            });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search by location");
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchViewActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
