package com.fap.bnotion.findaplace;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AgentsActivity extends AppCompatActivity {
private RecyclerView mRecyclerView;
private DatabaseReference mDatabase;
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
        setContentView(R.layout.activity_agents);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.AGENTS_KEY);
        mDatabase.keepSynced(true);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mRecyclerView = findViewById(R.id.all_users_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            String user_id = firebaseAuth.getCurrentUser().getUid();

            mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        getMenuInflater().inflate(R.menu.menu_agents_2, menu);
                    } else {
                        getMenuInflater().inflate(R.menu.menu_agents, menu);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            getMenuInflater().inflate(R.menu.menu_agents, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.dashboard:
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                sendToDashBoard();
                            } else {
                                sendToLogin();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Intent intent = new Intent(AgentsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "You need to log in first.", Toast.LENGTH_SHORT).show();
                }

                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.register:
            Intent registerIntent = new Intent(AgentsActivity.this, RegisterAgentOneActivity.class);
            startActivity(registerIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                break;
            case R.id.already_registered:
                FirebaseUser firebaseUser2 = firebaseAuth.getCurrentUser();
                if (firebaseUser2 != null){
                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                sendToDashBoard();
                            } else {
                                sendToLogin();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Intent intent = new Intent(AgentsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "You need to log in first.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    private void sendToDashBoard() {
        Intent agentDasboard = new Intent(AgentsActivity.this, AgentDashboardActivity.class);
        startActivity(agentDasboard);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<Agents, AgentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Agents,
                AgentsViewHolder>(Agents.class,
                R.layout.agents_single_layout,
                AgentsViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(final AgentsViewHolder viewHolder, final Agents model, int position) {
                final String user_uid = model.getUser_id();
                final String username = model.getName();
                viewHolder.setName(username);
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getImage(), getApplicationContext());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Agents");
                DatabaseReference finalRef = databaseReference.child(user_uid).child("status");
                finalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(String.class);
                        if (status.equals("Pending verification")) {
                            viewHolder.mVerifyIndicator.setImageResource(R.drawable.dot);
                            viewHolder.mVerifyIndicator.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent2), android.graphics.PorterDuff.Mode.SRC_IN);
                        } else if (status.equals("Verified")){
                            viewHolder.mVerifyIndicator.setImageResource(R.drawable.approved);
                            viewHolder.mVerifyIndicator.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                Intent intent = new Intent(AgentsActivity.this, AgentPostActivity.class);
                intent.putExtra("user_id", user_uid);
                intent.putExtra("username", username);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void sendToLogin() {
        Intent agentLogin = new Intent(AgentsActivity.this, AgentsLoginActivity.class);
        startActivity(agentLogin);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
    }

    public static class AgentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;
        private ItemClickListener itemClickListener;
        private ImageView mVerifyIndicator;
        public AgentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mVerifyIndicator = mView.findViewById(R.id.mDot);
            itemView.setOnClickListener(this);
        }

        public void setName(String name) {
            TextView name_agent = mView.findViewById(R.id.user_name);
            name_agent.setText(name);
        }

        public void setStatus(String status) {
            TextView name_agent = mView.findViewById(R.id.user_status);
            name_agent.setText(status);
        }
        public void setImage(String image, Context ctx) {
            CircleImageView image_agent = mView.findViewById(R.id.profile_image);
            RequestOptions placeHolderRequest = new RequestOptions();
            placeHolderRequest.placeholder(R.drawable.avatar_image);
            Glide.with(ctx).setDefaultRequestOptions(placeHolderRequest).load(image).into(image_agent);
        }
        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }
    }

    @Override
    public void onBackPressed() {
       finish();
      overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
