package com.fap.bnotion.findaplace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

public class ForSaleActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String TAG = "ERROR:";
    private RecyclerView mRecyclerView;
    private RelativeLayout mRelativeLayout;
    private List<AgentPost> post_list;
    private FirebaseFirestore firebaseFirestore;
    private AgentPostRecyclerAdapter agentPostRecyclerAdapter;
    private ImageView mImageView;
    private TextView mTextView;
    private DocumentSnapshot lastVisible;
    private Spinner spinner;
    private ProgressBar mProgressBar;
    private String retrieveProperty;
    private Boolean mIsSpinnerFirstCall = true;
    private String propertySelect = "For Sale";
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
        setContentView(R.layout.activity_for_sale);
        mProgressBar = findViewById(R.id.progressBarMain);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        retrieveProperty = sharedPreferences.getString("property", propertySelect);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        spinner = findViewById(R.id.spinnerSale);
        mRelativeLayout = findViewById(R.id.relativelayout);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.SaleOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int position = adapter.getPosition(retrieveProperty);
        post_list = new ArrayList<>();
        mImageView = findViewById(R.id.image);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                switch (position){
                    case 0:
                        propertySelect = "For Sale";
                        break;
                    case 1:
                        propertySelect = "Land";
                        break;
                }
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("property", propertySelect);
                editor.apply();
                if(!mIsSpinnerFirstCall){
                    Intent intent = new Intent(ForSaleActivity.this, ForSaleActivity.class);
                    startActivity(intent);
                    finish();
                }
                mIsSpinnerFirstCall = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setAdapter(adapter);
        spinner.setSelection(position, true);
        mTextView = findViewById(R.id.info_text);
        mRecyclerView = findViewById(R.id.recyclerView);
        agentPostRecyclerAdapter = new AgentPostRecyclerAdapter(post_list, ForSaleActivity.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(agentPostRecyclerAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
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
            firebaseFirestore.collection("Posts").whereEqualTo("category", retrieveProperty).limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        Query mQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", retrieveProperty);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_location2, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForSaleActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
    public void loadMorePosts(){

       Query nextQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", retrieveProperty)
                .startAfter(lastVisible).limit(5);
        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
