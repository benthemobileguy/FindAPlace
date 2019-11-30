package com.fap.bnotion.findaplace;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private Uri mainImageURI = null;
    private ImageView mUploadHouse;
    private RecyclerView mRecyclerPhotos;
    private EditText mTitle, mDescription, mLocation, mPrice, mSeatingCapacity, mArea;
    private List<String> fileNameList;
    private RadioGroup toilets_options, baths_options;
    private ArrayList<String> image_urls;
    private UploadTask mUploadTask;
    private ScrollView mScrollView;
    private AgentPost agentPost;
    private List<String> fileDoneList;
    private ProgressBar progress;
    private TextView mNumberPhotos, mToolbarTitleText, mPriceRangeText, toilets_text, baths_text, upload_instruction;
    private View view3, view4;
    private String fileName, downloadUri, user_id,  mobileNumber, email, username, status, image, titleData, stateData, locationData, areaData, descData, priceData, categoryData, imageURLData, bathsData, toiletsData, priceRangeData;
    private String dataCheck = "check";
    private int myNum = 1;
    String categories = "For Rent";
    String toilets = "One";
    String baths = "One";
    String selected_state, selected_price_range = "Lagos";
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private Spinner spinner, price_range;
    private UploadListAdapter uploadListAdapter;
    private ProgressBar mProgressBar;
    private Uri fileUri;
    private DatabaseReference databaseReference, databaseValue;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BringImagePicker();
                } else {
                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(NewPostActivity.this);
    }

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
        setContentView(R.layout.activity_new_post);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        image_urls = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        upload_instruction = findViewById(R.id.upload_instruction);
        storageReference = FirebaseStorage.getInstance().getReference();
        toilets_text = findViewById(R.id.toilets_text);
        baths_text = findViewById(R.id.baths_text);
        view3 = findViewById(R.id.view3);
        mPriceRangeText = findViewById(R.id.price_range_text);
        view4 = findViewById(R.id.view4);
        //retrieving agent info from firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.AGENTS_KEY);
        //mobile Number
        databaseValue = databaseReference.child(user_id).child("mobile number");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mobileNumber = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //email
        databaseValue = databaseReference.child(user_id).child("email");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //username
        databaseValue = databaseReference.child(user_id).child("name");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //status
        databaseValue = databaseReference.child(user_id).child("status");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //image
        databaseValue = databaseReference.child(user_id).child("image");
        databaseValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                image = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        firebaseFirestore = FirebaseFirestore.getInstance();
        mProgressBar = findViewById(R.id.progressBar);
        mNumberPhotos = findViewById(R.id.number_photos);
        mUploadHouse = findViewById(R.id.upload_house);
        mScrollView = findViewById(R.id.scrollView);
        mTitle = findViewById(R.id.title);
        mDescription = findViewById(R.id.description);
        mArea  = findViewById(R.id.area);
        mLocation = findViewById(R.id.location);
        mDescription.setHint("Description (minimum 50 characters)");
        mLocation.setHint("e.g Ikeja, Lagos");
        mTitle.setHint("Title (minimum 10 characters)");
        mPrice = findViewById(R.id.price);
        mSeatingCapacity = findViewById(R.id.seating_capacity);
        //automatically add commas as thousand separator.
        mSeatingCapacity.addTextChangedListener(new NumberTextWatcherForThousand(mSeatingCapacity));
        mPrice.addTextChangedListener(new NumberTextWatcherForThousand(mPrice));
        price_range = findViewById(R.id.price_range);
        spinner = findViewById(R.id.states);
        mToolbarTitleText = findViewById(R.id.basic_info_text);
        Button postButton = findViewById(R.id.post_button);
        Button deleteButton = findViewById(R.id.delete_button);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.AllStates, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final ArrayAdapter<CharSequence> price_range_adapter = ArrayAdapter.createFromResource(this, R.array.Prices2, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> seating_capacity_adapter = ArrayAdapter.createFromResource(this, R.array.Capacity2, android.R.layout.simple_spinner_item);
        price_range_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seating_capacity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        price_range.setAdapter(price_range_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected_state = spinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        price_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected_price_range = price_range.getSelectedItem().toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //setting radio buttons
        final RadioGroup categories_options = findViewById(R.id.categories_options);
        categories_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.for_rent:
                        categories = "For Rent";
                        price_range.setAdapter(price_range_adapter);
                        mDescription.setHint("Description (minimum 50 characters)");
                        mLocation.setHint("e.g Ikeja, Lagos");
                        mTitle.setHint("Title (minimum 6 characters)");
                        mPriceRangeText.setVisibility(View.VISIBLE);
                        price_range.setVisibility(View.VISIBLE);
                        mPrice.setVisibility(View.VISIBLE);
                        mPriceRangeText.setText("Price Range");
                        upload_instruction.setText("Upload at least a photo of your property here");
                        toilets_options.setVisibility(View.VISIBLE);
                        baths_options.setVisibility(View.VISIBLE);
                        mSeatingCapacity.setVisibility(View.GONE);
                        toilets_text.setVisibility(View.VISIBLE);
                        baths_text.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.VISIBLE);
                        view4.setVisibility(View.VISIBLE);
                        break;
                    case R.id.for_sale:
                        categories = "For Sale";
                        price_range.setAdapter(price_range_adapter);
                        mDescription.setHint("Description (minimum 50 characters)");
                        mLocation.setHint("e.g Ikeja, Lagos");
                        mTitle.setHint("Title (minimum 6 characters)");
                        mPriceRangeText.setText("Price Range");
                        upload_instruction.setText("Upload at least a photo of your property here");
                        mPriceRangeText.setVisibility(View.VISIBLE);
                        price_range.setVisibility(View.VISIBLE);
                        mPrice.setVisibility(View.VISIBLE);
                        toilets_options.setVisibility(View.VISIBLE);
                        baths_options.setVisibility(View.VISIBLE);
                        toilets_text.setVisibility(View.VISIBLE);
                        baths_text.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.VISIBLE);
                        view4.setVisibility(View.VISIBLE);
                        mSeatingCapacity.setVisibility(View.GONE);
                        break;
                    case R.id.service_apartment:
                        price_range.setAdapter(price_range_adapter);
                        categories = "Service Apartments";
                        mDescription.setHint("Description of the place");
                        mLocation.setHint("e.g Ikeja, Lagos");
                        mTitle.setHint("Title (minimum 6 characters)");
                        mPriceRangeText.setText("Price Range");
                        upload_instruction.setText("Upload at least a photo of your property here");
                        mPriceRangeText.setVisibility(View.VISIBLE);
                        price_range.setVisibility(View.VISIBLE);
                        mPrice.setVisibility(View.VISIBLE);
                        toilets_options.setVisibility(View.GONE);
                        baths_options.setVisibility(View.GONE);
                        mSeatingCapacity.setVisibility(View.GONE);
                        toilets_text.setVisibility(View.GONE);
                        baths_text.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        view4.setVisibility(View.GONE);
                        break;
                    case R.id.handyman:
                        categories = "Handyman";
                        price_range.setAdapter(price_range_adapter);
                        mDescription.setHint("Brief description of yourself");
                        mLocation.setHint("Your location");
                        mTitle.setHint("Your Occupation");
                        mPriceRangeText.setText("Price Range");
                        upload_instruction.setText("Upload at least a photo of yourself here");
                        mPriceRangeText.setVisibility(View.GONE);
                        price_range.setVisibility(View.GONE);
                        mPrice.setVisibility(View.GONE);
                        toilets_options.setVisibility(View.GONE);
                        baths_options.setVisibility(View.GONE);
                        mSeatingCapacity.setVisibility(View.GONE);
                        toilets_text.setVisibility(View.GONE);
                        baths_text.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        view4.setVisibility(View.GONE);
                        break;
                    case R.id.event_centre:
                        categories = "Event Centres";
                        price_range.setAdapter(seating_capacity_adapter);
                        mDescription.setHint("Description of the hall");
                        mLocation.setHint("e.g Ikeja, Lagos");
                        mTitle.setHint("Title (minimum 6 characters)");
                        mPriceRangeText.setText("Seating Capacity");
                        upload_instruction.setText("Upload at least a photo of your building here");
                        mPriceRangeText.setVisibility(View.VISIBLE);
                        price_range.setVisibility(View.VISIBLE);
                        mPrice.setVisibility(View.VISIBLE);
                        toilets_options.setVisibility(View.GONE);
                        baths_options.setVisibility(View.GONE);
                        mSeatingCapacity.setVisibility(View.VISIBLE);
                        toilets_text.setVisibility(View.GONE);
                        baths_text.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        view4.setVisibility(View.GONE);
                        break;
                    case R.id.lease:
                        categories = "Lease";
                        price_range.setAdapter(price_range_adapter);
                        mDescription.setHint("Description (minimum 50 characters)");
                        mLocation.setHint("e.g Ikeja, Lagos");
                        mTitle.setHint("Title (minimum 6 characters)");
                        upload_instruction.setText("Upload at least a photo of your property here");
                        mPriceRangeText.setText("Price Range");
                        mPriceRangeText.setVisibility(View.VISIBLE);
                        price_range.setVisibility(View.VISIBLE);
                        mPrice.setVisibility(View.VISIBLE);
                        toilets_options.setVisibility(View.GONE);
                        baths_options.setVisibility(View.GONE);
                        mSeatingCapacity.setVisibility(View.GONE);
                        toilets_text.setVisibility(View.GONE);
                        baths_text.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        view4.setVisibility(View.GONE);
                        break;
                    case R.id.land:
                        categories = "Land";
                        price_range.setAdapter(price_range_adapter);
                        mDescription.setHint("Description (minimum 50 characters)");
                        mLocation.setHint("e.g Ikeja, Lagos");
                        mTitle.setHint("Title (minimum 6 characters)");
                        upload_instruction.setText("Upload at least a photo of your land here");
                        mPriceRangeText.setText("Price Range");
                        mPriceRangeText.setVisibility(View.VISIBLE);
                        price_range.setVisibility(View.VISIBLE);
                        mPrice.setVisibility(View.VISIBLE);
                        toilets_options.setVisibility(View.GONE);
                        baths_options.setVisibility(View.GONE);
                        mSeatingCapacity.setVisibility(View.GONE);
                        toilets_text.setVisibility(View.GONE);
                        baths_text.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        view4.setVisibility(View.GONE);
                        break;
                }
            }
        });
        toilets_options = findViewById(R.id.toilet_options);
        toilets_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.toilet_one:
                        toilets = "1";
                        break;
                    case R.id.toilet_two:
                        toilets = "2";
                        break;
                    case R.id.toilet_three:
                        toilets = "3";
                        break;
                    case R.id.toilet_plus_three:
                        toilets = "3+";
                        break;
                }
            }
        });
        baths_options = findViewById(R.id.bath_options);
        baths_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.one_bath:
                        baths = "1";
                        break;
                    case R.id.two_bath:
                        baths = "2";
                        break;
                    case R.id.three_bath:
                        baths = "3";
                        break;
                    case R.id.three_plus_bath:
                        baths = "3+";
                        break;
                }
            }
        });
        Intent intentData = getIntent();
        if (intentData.hasExtra("data")){
            agentPost = (AgentPost) getIntent().getSerializableExtra("agentPost");
            titleData = intentData.getStringExtra("title");
            descData = intentData.getStringExtra("description");
            locationData = intentData.getStringExtra("location");
            priceData = intentData.getStringExtra("price");
            imageURLData = intentData.getStringExtra("imageURL");
            categoryData = intentData.getStringExtra("category");
            bathsData = intentData.getStringExtra("baths");
            toiletsData = intentData.getStringExtra("toilets");
            stateData = intentData.getStringExtra("state");
            dataCheck = intentData.getStringExtra("data");
            areaData = intentData.getStringExtra("area");
            priceRangeData = intentData.getStringExtra("price_range");
            image_urls = getIntent().getStringArrayListExtra("imageURLS");
            mToolbarTitleText.setText("Edit Post");
            postButton.setText("UPDATE POST");
            deleteButton.setVisibility(View.VISIBLE);
            mTitle.setText(titleData);
            mDescription.setText(descData);
            mLocation.setText(locationData);
            if(areaData!=null){
                mArea.setText(areaData);
            }
            mPrice.setText(priceData);
            downloadUri = imageURLData;
            mSeatingCapacity.setText(bathsData);
            switch (categoryData){
                case "For Rent":
                    categories_options.check(R.id.for_rent);
                    break;
                case "For Sale":
                    categories_options.check(R.id.for_sale);
                    break;
                case "Lease":
                    categories_options.check(R.id.lease);
                    break;
                case "Service Apartments":
                    categories_options.check(R.id.service_apartment);
                    break;
                case "Handyman":
                    categories_options.check(R.id.handyman);
                    break;
                case "Event Centres":
                    categories_options.check(R.id.event_centre);
                    break;
            }
            switch (toiletsData){
                case "1":
                    toilets_options.check(R.id.toilet_one);
                    break;
                case "2":
                    toilets_options.check(R.id.toilet_two);
                    break;
                case "3":
                    toilets_options.check(R.id.toilet_three);
                    break;
                case "3+":
                    toilets_options.check(R.id.toilet_plus_three);
                    break;
            }
            switch (bathsData){
                case "1":
                    baths_options.check(R.id.one_bath);
                    break;
                case "2":
                    baths_options.check(R.id.two_bath);
                    break;
                case "3":
                    baths_options.check(R.id.three_bath);
                    break;
                case "3+":
                    baths_options.check(R.id.three_plus_bath);
                    break;
            }
            // load image preview
            mUploadHouse.setEnabled(false);
            if (!imageURLData.equals("null")) {
                Glide.with(this).load(imageURLData).into(mUploadHouse);
                mNumberPhotos.setText("1");
            } else {
                Glide.with(this).load(image_urls.get(0)).into(mUploadHouse);
                mNumberPhotos.setText(String.valueOf(image_urls.size()));
            }

            int spinnerPosition = adapter.getPosition(stateData);
            spinner.setSelection(spinnerPosition);
            int priceRangeSpinnerPosition = price_range_adapter.getPosition(priceRangeData);
            price_range.setSelection(priceRangeSpinnerPosition);

        }
        mRecyclerPhotos = findViewById(R.id.recycler_photos);
        fileDoneList = new ArrayList<>();
        fileNameList = new ArrayList<>();
        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList, progress );
        //Recycler View
        mRecyclerPhotos.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerPhotos.setHasFixedSize(true);
        mRecyclerPhotos.setAdapter(uploadListAdapter);
        mUploadHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postImage();
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fetching string values
                final String title = mTitle.getText().toString().trim();
                final String description = mDescription.getText().toString().trim();
                final String area = mArea.getText().toString().trim();
                String location = mLocation.getText().toString().trim();
                String seatingCapacity = mSeatingCapacity.getText().toString().trim();
                String price = mPrice.getText().toString().trim();
                if (mSeatingCapacity.isShown()) {
                    baths = seatingCapacity;
                }

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(location) &&!TextUtils.isEmpty(area) && mDescription.length() >= 50 && mTitle.length() >= 6 && mLocation.length() >= 10 && downloadUri != null  && !dataCheck.equals("data")){
                    final String user_id = mAuth.getCurrentUser().getUid();
                    mProgressBar.setVisibility(View.VISIBLE);
                    Map<String, Object> map = new HashMap<>();
                    map.put("image_urls", image_urls);
                    map.put("image_url","null" );
                    map.put("title", title);
                    map.put("category", categories);
                    map.put("location", area);
                    map.put("price", price);
                    map.put("area", location);
                    map.put("number_of_toilets", toilets);
                    map.put("number_of_baths", baths);
                    map.put("state", selected_state);
                    map.put("user_id", user_id);
                    map.put("username", username);
                    map.put("mobile_number", mobileNumber);
                    map.put("status", status);
                    map.put("email", email);
                    map.put("profile_url", image);
                    map.put("price_range", selected_price_range);
                    map.put("timestamp", FieldValue.serverTimestamp());
                    map.put("desc", description);
                    firebaseFirestore.collection("Posts").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                finish();
                                Toast.makeText(NewPostActivity.this, "New Post Added", Toast.LENGTH_SHORT).show();
                                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(NewPostActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else if(TextUtils.isEmpty(title)){
                    mTitle.setError("Empty Field");
                    Toast.makeText(NewPostActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(area)){
                    mArea.setError("Empty Field");
                    Toast.makeText(NewPostActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(description)){
                    mDescription.setError("Empty Field");
                    Toast.makeText(NewPostActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                }  else if (mTitle.length() < 6){
                    mTitle.setError("Add more detail");
                    Toast.makeText(NewPostActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                } else if (mDescription.length() < 50){
                    mDescription.setError("Add more detail");
                    Toast.makeText(NewPostActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                } else if (downloadUri == null){
                    Toast.makeText(NewPostActivity.this, "Please upload at least an image", Toast.LENGTH_SHORT).show();
                } else if (mLocation.length() < 10){
                    mLocation.setError("Add more detail");
                    Toast.makeText(NewPostActivity.this, "You missed something. Check above for errors", Toast.LENGTH_LONG).show();
                }
                else if (mToolbarTitleText.toString().equals("New Post") && downloadUri == null){
                    Toast.makeText(NewPostActivity.this, "No image Uploaded", Toast.LENGTH_SHORT).show();
                } else if(mToolbarTitleText.toString().equals("Edit Post") && downloadUri == null){
                    Toast.makeText(NewPostActivity.this, "Image preview still loading", Toast.LENGTH_SHORT).show();
                } else if (dataCheck.equals("data")){
                    mProgressBar.setVisibility(View.VISIBLE);
                    Map<String, Object> map = new HashMap<>();
                    map.put("image_url", downloadUri);
                    map.put("title", title);
                    map.put("category", categories);
                    map.put("location", area);
                    map.put("price", price);
                    map.put("number_of_toilets", toilets);
                    map.put("number_of_baths", baths);
                    map.put("state", selected_state);
                    map.put("user_id", user_id);
                    map.put("username", username);
                    map.put("mobile_number", mobileNumber);
                    map.put("status", status);
                    map.put("price_range", priceRangeData);
                    map.put("email", email);
                    map.put("area", location);
                    map.put("profile_url", image);
                    map.put("latestTime", FieldValue.serverTimestamp());
                    map.put("desc", description);
                    firebaseFirestore.collection("Posts").document(agentPost.getId()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(NewPostActivity.this, AgentDashboardActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(NewPostActivity.this, "Post Updated Successfully.", Toast.LENGTH_SHORT).show();
                                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(NewPostActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });
    }

    private void showDeleteDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage("Do you really want to delete this post?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    private void deletePost() {
        mProgressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Posts").document(agentPost.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(NewPostActivity.this, AgentDashboardActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(NewPostActivity.this, "Post Deleted Successfully.", Toast.LENGTH_SHORT).show();
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(NewPostActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void postImage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(NewPostActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(NewPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                BringImagePicker();
            }
        } else{
            BringImagePicker();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                for (int i = 0; i < 1; i++){
                    mainImageURI = result.getUri();
                    fileName = getFileName(mainImageURI);
                    fileNameList.add(fileName);
                    fileDoneList.add("uploading");
                    uploadListAdapter.notifyDataSetChanged();
                    mUploadHouse.setImageURI(mainImageURI);
                    String user_id = mAuth.getCurrentUser().getUid();
                    final StorageReference postPath = storageReference.child("Images").child(user_id).child(String.valueOf(System.currentTimeMillis())).child(fileName);
                    final int finalI = i;
                    mUploadTask = postPath.putFile(mainImageURI);
                    Task<Uri> urlTask = mUploadTask.continueWithTask(task -> {
                        if(task.isSuccessful()) {
                        }

                        // Continue with the task to get the download URL
                        return postPath.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            final Uri[] downloadUri = {task.getResult()};
                            Map<String, String> userMap = new HashMap<>();
                            userMap.put(Constants.PASSPORT, downloadUri[0].toString());
                            firebaseFirestore.collection(Constants.USER_KEY).document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()) {
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(NewPostActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();

                                    } else {
                                        mNumberPhotos.setText("" + myNum++);
                                        downloadUri[0] = Uri.parse(downloadUri[0].toString());
                                        image_urls.add(String.valueOf(downloadUri));
                                        //   fileDoneList.remove(finalI);
                                        fileDoneList.add(finalI, "done");
                                        uploadListAdapter.notifyDataSetChanged();

                                    }
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                            Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.dashboard:
                Intent intent = new Intent(NewPostActivity.this, AgentDashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage("Quit the posting process?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }
    public String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null, null);
            try{
                if (cursor !=null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }

        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
