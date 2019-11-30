package com.fap.bnotion.findaplace;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MainActivity extends AppCompatActivity implements UpdateHelper.onUpdateCheckListener {
    private static final String TAG = "MainActivity";
    private String mLocation = "Location";
    private String mCategory = "Category";
    private BottomAppBar bottomAppBar;
    private SearchableSpinner locationSpinner, categorySpinner;
    private SpinKitView spinKitView;
    private DatabaseReference mDatabase, mDatabaseUsers, databaseValue;
    private LinearLayout mLatest, mForSale, mSpinnerLocLayout, mSpinnerCategoryLayout, mForRent, mNews, mHandyman, mSupport, mEstateAgents, mSearchBtn, mEventCentre, mLease, mServiceApartment;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    private List<String> areaArray;
    private String keywordText;
    private LinearLayout mLinear1, mLinear2, mLinear3, mLinear4;
    private Toolbar mToolbar;
    private TextView mHomeBtn, mFavBtn, mNotificationsBtn, mAccountBtn;
    private EditText mSearch;
    private DocumentSnapshot lastVisible = null;
    private List<AgentPost> post_list, post_list2;
    private FirebaseFirestore firebaseFirestore;
    private AgentPostRecyclerAdapter agentPostRecyclerAdapter, agentPostRecyclerAdapter2;
    private ImageView mBackImage, mBack;
    private Boolean booleanExtra;
    private RecyclerView favRecyclerView, mRecyclerView, mRecyclerView2;
    private FavoriteListRecyclerAdapter favoriteListRecyclerAdapter;
    private Dialog mDialog;
    private AppBarLayout appBarLayout;
    private List<FavList> fav_list;
    private ScrollView mFavouritesLayout, mMessagesLayout, mAccountLayout;
    private NestedScrollView mHomeLayout, mSearchLayout;
    private Animation animation;
    private TextView mAccountType, mPlace, mNoFind, mPlaceNothing, mNothingText, mToolbarTitle, mUsername, mPhone, mAddress, mEmail;
    private String usernameValue, emailValue, phoneValue, addressValue;
    private Button mEditDetails, mLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // locking out landscape screen orientation for mobiles
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // locking out portait screen orientation for tablets
        }
        if (getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_main);
        mBack = findViewById(R.id.back);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        areaArray = new ArrayList<>();
        spinKitView = findViewById(R.id.spin_kit);
        mLinear1 = findViewById(R.id.linear_layout);
        mLinear2 = findViewById(R.id.linear_layout2);
        mLinear3 = findViewById(R.id.linear_layout3);
        mLinear4 = findViewById(R.id.linear_layout4);
        locationSpinner = findViewById(R.id.location);
        mSpinnerCategoryLayout = findViewById(R.id.spinner_category_layout);
        mSpinnerLocLayout = findViewById(R.id.spinner_loc_layout);
        mPlace = findViewById(R.id.place);
        mNoFind = findViewById(R.id.no_find);
        mHomeBtn = findViewById(R.id.homeBtn);
        setTextViewDrawableColor(mHomeBtn, R.color.lime);
        mHomeBtn.setTextColor(getResources().getColor(R.color.lime));
        mNotificationsBtn = findViewById(R.id.notificationBtn);
        mAccountBtn = findViewById(R.id.accountBtn);
        mFavBtn = findViewById(R.id.favBtn);
        mNothingText = findViewById(R.id.nothing_text);
        mPlaceNothing = findViewById(R.id.place_nothing);
        mRecyclerView2 = findViewById(R.id.recyclerView2);
        appBarLayout = findViewById(R.id.appBarLayout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mToolbar = findViewById(R.id.toolbar);
        categorySpinner = findViewById(R.id.category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.Category, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        locationSpinner.setTitle("Select Location");
        categorySpinner.setTitle("Select Category");
        locationSpinner.setPositiveButton("OK");
        getLocationsArray();
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLocation = locationSpinner.getSelectedItem().toString();
                spinKitView.setVisibility(View.VISIBLE);
                performSearchbyLocation(mLocation);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mCategory = categorySpinner.getSelectedItem().toString();
                spinKitView.setVisibility(View.VISIBLE);
                performFilterByCategory(mCategory);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        categorySpinner.setPositiveButton("OK");
        mToolbarTitle = findViewById(R.id.toolbar_title);
        int badgeCount = 1;
        ShortcutBadger.applyCount(this, badgeCount); //for 1.1.4+
        mAuth = FirebaseAuth.getInstance();
        mDialog = new Dialog(this);
        mBackImage = findViewById(R.id.back_img);
        //check for newer app versions on google play
        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();
        mSearch = findViewById(R.id.search);
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!v.getText().toString().equals("")) {
                        spinKitView.setVisibility(View.VISIBLE);
                        performSearch(v.getText().toString());
                        keywordText = v.getText().toString();
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                        return true;
                    }
                }
                return false;
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_list2.clear();
                agentPostRecyclerAdapter2.notifyDataSetChanged();
                mSearchLayout.setVisibility(View.GONE);
                animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                mHomeLayout.setVisibility(View.GONE);
                mLinear1.setVisibility(View.GONE);
                mLinear2.setVisibility(View.GONE);
                mLinear3.setVisibility(View.GONE);
                mLinear4.setVisibility(View.GONE);
                mRecyclerView2.setVisibility(View.GONE);
                mSearchLayout.setVisibility(View.VISIBLE);
                mSearchLayout.startAnimation(animation);
                appBarLayout.setExpanded(false);
                mSearch.setFocusable(true);
                mSearch.setFocusableInTouchMode(true);
                mSearch.requestFocus();
                mBack.setVisibility(View.VISIBLE);
                mSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        ImageView mSort = findViewById(R.id.sort);
        mSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        booleanExtra = getIntent().getBooleanExtra("isGuestButtonClicked", false);
        firebaseAuth = FirebaseAuth.getInstance();
        fav_list = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.AGENTS_KEY);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constants.USER_KEY);
        favRecyclerView = findViewById(R.id.fav_recyclerView);
        favoriteListRecyclerAdapter = new FavoriteListRecyclerAdapter(fav_list, MainActivity.this);
        favRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favRecyclerView.setAdapter(favoriteListRecyclerAdapter);
        mDatabase.keepSynced(true);
        mAccountType = findViewById(R.id.account_type_text);
        mSearchLayout = findViewById(R.id.search_layout);
        mUsername = findViewById(R.id.username_text);
        post_list = new ArrayList<>();
        post_list2 = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView2 = findViewById(R.id.recyclerView2);
        agentPostRecyclerAdapter = new AgentPostRecyclerAdapter(post_list, MainActivity.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(agentPostRecyclerAdapter);
        agentPostRecyclerAdapter2 = new AgentPostRecyclerAdapter(post_list2, MainActivity.this);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView2.setAdapter(agentPostRecyclerAdapter2);
        Query query = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error:" + e.getMessage());
                } else {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    }
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostID = doc.getDocument().getId();
                            AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                            post_list.add(agentPost);
                            agentPostRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        mPhone = findViewById(R.id.phone_text);
        mAddress = findViewById(R.id.address_text);
        mEmail = findViewById(R.id.email_text);
        mEditDetails = findViewById(R.id.edit_details);
        mLogOut = findViewById(R.id.log_out);
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //setting account details
        if (firebaseUser != null) {
            final String user_uid = firebaseUser.getUid();
            mDatabase.child(user_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mAccountType.setText("Agent");
                        mDatabase.child(user_uid).child("mobile number").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                phoneValue = dataSnapshot.getValue(String.class);
                                mPhone.setText(phoneValue);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child(user_uid).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                usernameValue = dataSnapshot.getValue(String.class);
                                mUsername.setText(usernameValue);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child(user_uid).child("email").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                emailValue = dataSnapshot.getValue(String.class);
                                mEmail.setText(emailValue);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child(user_uid).child("address").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                addressValue = dataSnapshot.getValue(String.class);
                                mAddress.setText(addressValue);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        mAccountType.setText("User");
                        mAddress.setText("Not yet assigned");
                        mDatabaseUsers.child(user_uid).child("username").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                usernameValue = dataSnapshot.getValue(String.class);
                                mUsername.setText(usernameValue);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabaseUsers.child(user_uid).child("phone").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                phoneValue = dataSnapshot.getValue(String.class);
                                if (dataSnapshot.exists()) {
                                    mPhone.setText(phoneValue);
                                } else {
                                    mPhone.setText("Not yet assigned");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabaseUsers.child(user_uid).child("email").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                emailValue = dataSnapshot.getValue(String.class);
                                mEmail.setText(emailValue);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }


        final FloatingActionButton add_post_btn = findViewById(R.id.fab);
        add_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Intent mAddPost = new Intent(MainActivity.this, NewPostActivity.class);
                                startActivity(mAddPost);
                                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            } else {
                                mDialog.setContentView(R.layout.custom_agent_login_dialog);
                                ImageView closeDialog = mDialog.findViewById(R.id.close_dialog);
                                closeDialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mDialog.dismiss();
                                    }
                                });
                                Button registerBtn = mDialog.findViewById(R.id.register_btn);
                                Button already_registered_btn = mDialog.findViewById(R.id.already_registered_btn);
                                registerBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(MainActivity.this, RegisterAgentOneActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                                        mDialog.dismiss();
                                    }
                                });
                                already_registered_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(MainActivity.this, AgentsLoginActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                                        mDialog.dismiss();
                                    }
                                });
                                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                mDialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, "You need to be logged into an account", Toast.LENGTH_LONG).show();
                    sendToLogin();
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                }

            }
        });

        mHomeLayout = findViewById(R.id.home_layout);
        mFavouritesLayout = findViewById(R.id.favourites_layout);
        mMessagesLayout = findViewById(R.id.notifications_layout);
        mAccountLayout = findViewById(R.id.account_layout);
        mEventCentre = findViewById(R.id.event_centre);
        mLatest = findViewById(R.id.latest);
        mForSale = findViewById(R.id.for_sale);
        mForRent = findViewById(R.id.for_rent);
        mNews = findViewById(R.id.news);
        mHandyman = findViewById(R.id.handyman);
        mSupport = findViewById(R.id.support);
        mServiceApartment = findViewById(R.id.service_apartment);
        mLease = findViewById(R.id.lease);
        mEstateAgents = findViewById(R.id.estate_agents);
        mSearchBtn = findViewById(R.id.search_properties);
        mLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int scrollTo = ((View) mRecyclerView.getParent().getParent()).getTop() + mRecyclerView.getTop() + 700;
                mHomeLayout.smoothScrollTo(0, scrollTo);
                appBarLayout.setExpanded(false);
            }
        });
        mForSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mForSale = new Intent(MainActivity.this, ForSaleActivity.class);
                startActivity(mForSale);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mForRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mForRent = new Intent(MainActivity.this, ForRentActivity.class);
                startActivity(mForRent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mHandyman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mHandyMan = new Intent(MainActivity.this, HandyManActivity.class);
                startActivity(mHandyMan);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mNews = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(mNews);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mServiceApartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mRegisterAgent = new Intent(MainActivity.this, AgentsActivity.class);
                startActivity(mRegisterAgent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mSupportAgent = new Intent(MainActivity.this, SupportActivity.class);
                startActivity(mSupportAgent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mEventCentre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mSupportAgent = new Intent(MainActivity.this, EventCentreActivity.class);
                startActivity(mSupportAgent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });

        mHomeLayout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= oldScrollY) {
                    Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up);
                    //scroll down

                    bottomAppBar.setVisibility(View.VISIBLE);
                    add_post_btn.show();
                    //back to position
                    bottomAppBar.animate().translationY(2);
                } else {
                    //scroll up
                    bottomAppBar.setVisibility(View.INVISIBLE);
                    add_post_btn.hide();
                    //animate away from screen
                    bottomAppBar.animate().translationY(bottomAppBar.getHeight());
                    add_post_btn.animate().translationY(bottomAppBar.getHeight());
                }
                //Do something here when ur scroll reached the bottom by scrolling up

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (lastVisible != null) {
                        loadMorePosts();
                    }
                }

            }
        });
        mLease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mLease = new Intent(MainActivity.this, LeaseActivity.class);
                startActivity(mLease);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mServiceApartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mServiceApartment = new Intent(MainActivity.this, ServiceApartmentActivity.class);
                startActivity(mServiceApartment);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mSearch = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(mSearch);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mEstateAgents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mEstateAgents = new Intent(MainActivity.this, AgentsActivity.class);
                startActivity(mEstateAgents);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });
        mHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHomeLayout.getVisibility() == View.INVISIBLE) {
                    removeSearchFocus();
                    mBack.setVisibility(View.GONE);
                    mSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_24dp, 0, 0, 0);
                    setTextViewDrawableColor(mHomeBtn, R.color.lime);
                    mHomeBtn.setTextColor(getResources().getColor(R.color.lime));
                    mFavBtn.setTextColor(getResources().getColor(R.color.white));
                    mNotificationsBtn.setTextColor(getResources().getColor(R.color.white));
                    mAccountBtn.setTextColor(getResources().getColor(R.color.white));
                    setTextViewDrawableColor(mFavBtn, R.color.white);
                    setTextViewDrawableColor(mNotificationsBtn, R.color.white);
                    setTextViewDrawableColor(mAccountBtn, R.color.white);
                    mToolbar.setVisibility(View.GONE);
                    appBarLayout.setVisibility(View.VISIBLE);
                    mHomeLayout.setVisibility(View.VISIBLE);
                    favRecyclerView.setVisibility(View.INVISIBLE);
                    mFavouritesLayout.setVisibility(View.INVISIBLE);
                    favRecyclerView.setVisibility(View.INVISIBLE);
                    mMessagesLayout.setVisibility(View.INVISIBLE);
                    mAccountLayout.setVisibility(View.INVISIBLE);
                    mEditDetails.setVisibility(View.INVISIBLE);
                    mBackImage.setVisibility(View.VISIBLE);
                    mLogOut.setVisibility(View.INVISIBLE);
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                    mHomeLayout.startAnimation(animation);
                }
            }
        });
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_24dp, 0, 0, 0);
                if (mFavouritesLayout.getVisibility() == View.INVISIBLE) {
                    removeSearchFocus();
                    mBack.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.VISIBLE);
                    mToolbarTitle.setText("Favourites");
                    appBarLayout.setVisibility(View.INVISIBLE);
                    setTextViewDrawableColor(mHomeBtn, R.color.white);
                    mFavBtn.setTextColor(getResources().getColor(R.color.lime));
                    setTextViewDrawableColor(mFavBtn, R.color.lime);
                    setTextViewDrawableColor(mNotificationsBtn, R.color.white);
                    setTextViewDrawableColor(mAccountBtn, R.color.white);
                    mHomeBtn.setTextColor(getResources().getColor(R.color.white));
                    mNotificationsBtn.setTextColor(getResources().getColor(R.color.white));
                    mAccountBtn.setTextColor(getResources().getColor(R.color.white));
                    mBackImage.setVisibility(View.VISIBLE);
                    mHomeLayout.setVisibility(View.INVISIBLE);
                    favRecyclerView.setVisibility(View.VISIBLE);
                    mMessagesLayout.setVisibility(View.INVISIBLE);
                    mAccountLayout.setVisibility(View.INVISIBLE);
                    mEditDetails.setVisibility(View.INVISIBLE);
                    mLogOut.setVisibility(View.INVISIBLE);
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                    if (favoriteListRecyclerAdapter.getItemCount() == 0) {
                        mFavouritesLayout.setVisibility(View.VISIBLE);
                        mFavouritesLayout.startAnimation(animation);
                        favRecyclerView.startAnimation(animation);
                    }
                }
            }
        });
        mNotificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBack.setVisibility(View.GONE);
                mSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_24dp, 0, 0, 0);
                if (mMessagesLayout.getVisibility() == View.INVISIBLE) {
                    removeSearchFocus();
                    mToolbar.setVisibility(View.VISIBLE);
                    mToolbarTitle.setText("Messages");
                    appBarLayout.setVisibility(View.INVISIBLE);
                    setTextViewDrawableColor(mHomeBtn, R.color.white);
                    mNotificationsBtn.setTextColor(getResources().getColor(R.color.lime));
                    setTextViewDrawableColor(mFavBtn, R.color.white);
                    setTextViewDrawableColor(mNotificationsBtn, R.color.lime);
                    setTextViewDrawableColor(mAccountBtn, R.color.white);
                    mFavBtn.setTextColor(getResources().getColor(R.color.white));
                    mHomeBtn.setTextColor(getResources().getColor(R.color.white));
                    mAccountBtn.setTextColor(getResources().getColor(R.color.white));
                    mBackImage.setVisibility(View.VISIBLE);
                    mHomeLayout.setVisibility(View.INVISIBLE);
                    mFavouritesLayout.setVisibility(View.INVISIBLE);
                    mMessagesLayout.setVisibility(View.VISIBLE);
                    favRecyclerView.setVisibility(View.INVISIBLE);
                    mEditDetails.setVisibility(View.INVISIBLE);
                    mLogOut.setVisibility(View.INVISIBLE);
                    mAccountLayout.setVisibility(View.INVISIBLE);
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                    mMessagesLayout.startAnimation(animation);
                }

            }
        });
        mAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccountLayout.getVisibility() == View.INVISIBLE) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (!booleanExtra && firebaseUser == null) {
                        sendToLogin();
                    } else {
                        removeSearchFocus();
                        mBack.setVisibility(View.GONE);
                        mSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_24dp, 0, 0, 0);
                        mToolbar.setVisibility(View.VISIBLE);
                        mToolbarTitle.setText("Account");
                        appBarLayout.setVisibility(View.INVISIBLE);
                        setTextViewDrawableColor(mHomeBtn, R.color.white);
                        setTextViewDrawableColor(mFavBtn, R.color.white);
                        setTextViewDrawableColor(mNotificationsBtn, R.color.white);
                        setTextViewDrawableColor(mAccountBtn, R.color.lime);
                        mFavBtn.setTextColor(getResources().getColor(R.color.white));
                        mNotificationsBtn.setTextColor(getResources().getColor(R.color.white));
                        mAccountBtn.setTextColor(getResources().getColor(R.color.lime));
                        mHomeLayout.setVisibility(View.INVISIBLE);
                        mBackImage.setVisibility(View.GONE);
                        mFavouritesLayout.setVisibility(View.INVISIBLE);
                        mMessagesLayout.setVisibility(View.INVISIBLE);
                        favRecyclerView.setVisibility(View.INVISIBLE);
                        mAccountLayout.setVisibility(View.VISIBLE);
                        mEditDetails.setVisibility(View.VISIBLE);
                        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                        mEditDetails.startAnimation(animation);
                        mAccountLayout.startAnimation(animation);
                        mLogOut.setVisibility(View.VISIBLE);
                        mLogOut.startAnimation(animation);
                    }
                }

            }
        });
        // edit account details
        mEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditDetailsActivity.class);
                intent.putExtra("name", usernameValue);
                intent.putExtra("email", emailValue);
                intent.putExtra("address", addressValue);
                intent.putExtra("phone", phoneValue);
                startActivity(intent);
            }
        });
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sendToLogin();
            }
        });
    }

    private void performFilterByCategory(final String mCategory) {
        final Query nextQuery;
        mLinear1.setVisibility(View.INVISIBLE);
        mLinear2.setVisibility(View.INVISIBLE);
        mLinear3.setVisibility(View.INVISIBLE);
        mLinear4.setVisibility(View.INVISIBLE);
        mRecyclerView2.setVisibility(View.GONE);
        if (!mLocation.equals("Location")) {
            nextQuery = firebaseFirestore.collection("Posts").whereEqualTo("area", mLocation).whereEqualTo("category", mCategory);
        } else {
            nextQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", mCategory);
        }
        post_list2.clear();
        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String blogPostID = doc.getDocument().getId();
                                    AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                                    post_list2.add(agentPost);
                                    List<AgentPost> filteredList = filter(post_list2, keywordText);
                                    agentPostRecyclerAdapter2.updateList(filteredList);
                                    agentPostRecyclerAdapter2.notifyDataSetChanged();
                                    mRecyclerView2.setVisibility(View.VISIBLE);
                                    spinKitView.setVisibility(View.GONE);
                                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                                    if (filteredList.size() < 1) {
                                        mNothingText.setText(keywordText);
                                        mPlaceNothing.setText(mCategory);
                                        mLinear1.setVisibility(View.VISIBLE);
                                        mLinear2.setVisibility(View.VISIBLE);
                                        mLinear3.setVisibility(View.INVISIBLE);
                                        mLinear4.setVisibility(View.VISIBLE);
                                    } else {
                                        mNoFind.setText(String.valueOf(filteredList.size()));
                                        mPlace.setText(mCategory);
                                        mLinear1.setVisibility(View.VISIBLE);
                                        mLinear2.setVisibility(View.VISIBLE);
                                        mLinear3.setVisibility(View.VISIBLE);
                                        mLinear4.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }
                }, 5000);


            }
        });

    }

    private void performSearchbyLocation(final String mLocation) {
        final Query nextQuery;
        mLinear1.setVisibility(View.INVISIBLE);
        mLinear2.setVisibility(View.INVISIBLE);
        mLinear3.setVisibility(View.INVISIBLE);
        mLinear4.setVisibility(View.INVISIBLE);
        mRecyclerView2.setVisibility(View.GONE);
        if (!mCategory.equals("Category")) {
            nextQuery = firebaseFirestore.collection("Posts").whereEqualTo("area", mLocation).whereEqualTo("category", mCategory);
        } else {
            nextQuery = firebaseFirestore.collection("Posts").whereEqualTo("area", mLocation);
        }
        post_list2.clear();
        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String blogPostID = doc.getDocument().getId();
                                    AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                                    post_list2.add(agentPost);
                                    List<AgentPost> filteredList = filter(post_list2, keywordText);
                                    agentPostRecyclerAdapter2.updateList(filteredList);
                                    agentPostRecyclerAdapter2.notifyDataSetChanged();
                                    mRecyclerView2.setVisibility(View.VISIBLE);
                                    spinKitView.setVisibility(View.GONE);
                                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                                    if (filteredList.size() < 1) {
                                        mNothingText.setText(keywordText);
                                        mPlaceNothing.setText(mLocation);
                                        mLinear1.setVisibility(View.VISIBLE);
                                        mLinear2.setVisibility(View.VISIBLE);
                                        mLinear3.setVisibility(View.INVISIBLE);
                                        mLinear4.setVisibility(View.VISIBLE);
                                    } else {
                                        mNoFind.setText(String.valueOf(filteredList.size()));
                                        mPlace.setText(mLocation);
                                        mLinear1.setVisibility(View.VISIBLE);
                                        mLinear2.setVisibility(View.VISIBLE);
                                        mLinear3.setVisibility(View.VISIBLE);
                                        mLinear4.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }
                }, 5000);


            }
        });

    }


    private void getLocationsArray() {
        firebaseFirestore.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String blogPostID = document.getId();
                            AgentPost agentPost = document.toObject(AgentPost.class).withId(blogPostID);
                            if (agentPost.area != null) {
                                areaArray.add(agentPost.area);
                            }
                        }
                        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, areaArray.toArray(new String[0]));
                        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        locationSpinner.setAdapter(locationAdapter);

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }

            }
        });
    }

    private void performSearch(final String keyword) {
        mLinear1.setVisibility(View.INVISIBLE);
        mLinear2.setVisibility(View.INVISIBLE);
        mLinear3.setVisibility(View.INVISIBLE);
        mLinear4.setVisibility(View.INVISIBLE);
        mRecyclerView2.setVisibility(View.GONE);
        if (mLocation.equals("Location") && mCategory.equals("Category")) {
            final Query nextQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(final QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String blogPostID = doc.getDocument().getId();
                                        AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                                        post_list2.add(agentPost);
                                        List<AgentPost> filteredList = filter(post_list2, keyword);
                                        agentPostRecyclerAdapter2.updateList(filteredList);
                                        agentPostRecyclerAdapter2.notifyDataSetChanged();
                                        mRecyclerView2.setVisibility(View.VISIBLE);
                                        spinKitView.setVisibility(View.GONE);
                                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                                        if (filteredList.size() < 1) {
                                            mNothingText.setText(keywordText);
                                            mPlaceNothing.setText(mLocation);
                                            mLinear1.setVisibility(View.VISIBLE);
                                            mLinear2.setVisibility(View.VISIBLE);
                                            mLinear3.setVisibility(View.INVISIBLE);
                                            mLinear4.setVisibility(View.VISIBLE);
                                        } else {
                                            mNoFind.setText(String.valueOf(filteredList.size()));
                                            mPlace.setText(mLocation);
                                            mLinear1.setVisibility(View.VISIBLE);
                                            mLinear2.setVisibility(View.VISIBLE);
                                            mLinear3.setVisibility(View.VISIBLE);
                                            mLinear4.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                    }, 5000);


                }
            });
        } else if (!mLocation.equals("Location")) {
            performSearchbyLocation(mLocation);
        }

    }

    public void loadMorePosts() {

        Query nextQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible).limit(5);
        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String blogPostID = doc.getDocument().getId();
                                AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                                post_list.add(agentPost);
                                agentPostRecyclerAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                }
            }
        });
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public void onUpdateCheckListener(String urlApp) {
        mDialog.setContentView(R.layout.custom_update_dialog);
        ImageView closeDialog = mDialog.findViewById(R.id.close_dialog);
        final TextView mVersionNumber = mDialog.findViewById(R.id.new_version_text);
        final TextView mFeatureOne = mDialog.findViewById(R.id.new_feature_one);
        final TextView mFeatureTwo = mDialog.findViewById(R.id.new_feature_two);
        final TextView mFeatureThree = mDialog.findViewById(R.id.new_feature_three);
        final TextView mAppRemovalText = mDialog.findViewById(R.id.app_removal_text);
        Button mUpdateButton = mDialog.findViewById(R.id.update_btn);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
                    startActivity(i);
                }
            }
        });
        //set text fields
        mDatabase = FirebaseDatabase.getInstance().getReference().child("AppUpdate");
        //version number
        databaseValue = mDatabase.child("versionNo");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String versionNo = dataSnapshot.getValue(String.class);
                mVersionNumber.setText(versionNo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //feature one
        databaseValue = mDatabase.child("featureOne");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String featureOne = dataSnapshot.getValue(String.class);
                mFeatureOne.setText(featureOne);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //feature two
        databaseValue = mDatabase.child("featureTwo");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String featureTwo = dataSnapshot.getValue(String.class);
                mFeatureTwo.setText(featureTwo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //feature three
        databaseValue = mDatabase.child("featureThree");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String featureThree = dataSnapshot.getValue(String.class);
                mFeatureThree.setText(featureThree);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //removal text
        databaseValue = mDatabase.child("removalText");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String removalText = dataSnapshot.getValue(String.class);
                mAppRemovalText.setText(removalText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mSearch.isFocused()) {
            mLocation = "Location";
            mCategory = "Category";
            animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
            mHomeLayout.setVisibility(View.VISIBLE);
            mHomeLayout.startAnimation(animation);
            mSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_24dp, 0, 0, 0);
            mBack.setVisibility(View.GONE);
            removeSearchFocus();
            appBarLayout.setExpanded(true);
        } else {
            finish();
        }
    }

    private void removeSearchFocus() {
        mLinear1.setVisibility(View.INVISIBLE);
        mLinear2.setVisibility(View.INVISIBLE);
        mLinear3.setVisibility(View.INVISIBLE);
        mLinear4.setVisibility(View.INVISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mRecyclerView2.setVisibility(View.GONE);
        spinKitView.setVisibility(View.GONE);
        post_list2.clear();
        agentPostRecyclerAdapter2.notifyDataSetChanged();
        mSearch.getText().clear();
        appBarLayout.setExpanded(true);
        mSearch.setFocusable(false);
        mSearch.setFocusableInTouchMode(false);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
    }

    private List<AgentPost> filter(List<AgentPost> pi, String query) {
        query = query.toLowerCase();
        final List<AgentPost> filteredList = new ArrayList<>();
        for (AgentPost agentPost : pi) {
            final String text = agentPost.getDesc().toLowerCase();
            if (text.contains(query)) {
                filteredList.add(agentPost);
            }
        }
        return filteredList;
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    drawable.setColorFilter(new PorterDuffColorFilter(getColor(color), PorterDuff.Mode.SRC_IN));
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser==null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
