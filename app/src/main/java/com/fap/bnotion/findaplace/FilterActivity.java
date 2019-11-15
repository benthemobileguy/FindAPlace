package com.fap.bnotion.findaplace;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import java.util.ArrayList;
import java.util.List;



public class FilterActivity extends AppCompatActivity{
private static final String TAG = "Main";
private TextView mNothingText;
private Spinner categorySpinner, stateSpinner, bathsSpinner, toiletsSpinner, priceSpinner;
private SearchableSpinner locationSpinner, occupationSpinner;
private String selected_state, selected_area, selected_occupation, selected_category, selected_baths, selected_toilets, selected_price = "Lagos";
private Button filterButton;
private RecyclerView mRecyclerView;
private AlertDialog dialog;
List<String> areaArray, occupationArray;
private List<AgentPost> post_list;
private LinearLayout linearLayout;
private FirebaseFirestore firebaseFirestore;
private AgentPostRecyclerAdapter agentPostRecyclerAdapter;
private ProgressBar mProgress;
private RelativeLayout mRelativeLayout;
private ImageView mImageView;
private TextView mTextView, price_sort_text, occupationText, number_baths_text, number_toilets_text;
private View view1, view2, view3, view4, view5;
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
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        areaArray = new ArrayList<>();
        occupationArray = new ArrayList<>();
        dialog = builder.create();
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        linearLayout = findViewById(R.id.linear_layout);
        mNothingText = findViewById(R.id.nothing_text);
        occupationSpinner = findViewById(R.id.occupation);
        occupationText = findViewById(R.id.occupation_text);
        price_sort_text = findViewById(R.id.price_sort_text);
        number_baths_text = findViewById(R.id.number_baths_text);
        number_toilets_text = findViewById(R.id.number_toilets_text);
        categorySpinner = findViewById(R.id.category);
        stateSpinner = findViewById(R.id.state);
        locationSpinner = findViewById(R.id.location);
        locationSpinner.setTitle("Search Location");
        locationSpinner.setPositiveButton("OK");
        occupationSpinner.setTitle("Search Handyman");
        occupationSpinner.setPositiveButton("OK");
        bathsSpinner = findViewById(R.id.number_baths);
        toiletsSpinner = findViewById(R.id.number_toilets);
        priceSpinner = findViewById(R.id.price_sort);
        filterButton = findViewById(R.id.apply_filter_button);
        mRecyclerView = findViewById(R.id.recyclerView);
        mProgress = findViewById(R.id.progressBar);
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.info_text);
        mRelativeLayout = findViewById(R.id.relativeLayout);
        post_list = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getLocationsArray();
        getOccupationsArray();
        agentPostRecyclerAdapter = new AgentPostRecyclerAdapter(post_list, FilterActivity.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(agentPostRecyclerAdapter);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.AllStates2, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        ArrayAdapter<CharSequence> priceAdapter = ArrayAdapter.createFromResource(this, R.array.Prices, android.R.layout.simple_spinner_item);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(priceAdapter);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.Category, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        ArrayAdapter<CharSequence> bathsAdapter = ArrayAdapter.createFromResource(this, R.array.Toilets, android.R.layout.simple_spinner_item);
        bathsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bathsSpinner.setAdapter(bathsAdapter);
        ArrayAdapter<CharSequence> toiletsAdapter = ArrayAdapter.createFromResource(this, R.array.Toilets, android.R.layout.simple_spinner_item);
        toiletsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toiletsSpinner.setAdapter(toiletsAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected_state = stateSpinner.getSelectedItem().toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected_category = categorySpinner.getSelectedItem().toString();
                if (selected_category.equals("Handyman")) {
                toiletsSpinner.setVisibility(View.GONE);
                bathsSpinner.setVisibility(View.GONE);
                number_baths_text.setVisibility(View.GONE);
                number_toilets_text.setVisibility(View.GONE);
                price_sort_text.setText("Sort by Price");
                priceSpinner.setVisibility(View.GONE);
                price_sort_text.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view4.setVisibility(View.GONE);
                view5.setVisibility(View.GONE);
                occupationSpinner.setVisibility(View.VISIBLE);
                occupationText.setVisibility(View.VISIBLE);

                } else if (selected_category.equals("All")) {
                 number_baths_text.setVisibility(View.VISIBLE);
                 number_toilets_text.setVisibility(View.VISIBLE);
                 toiletsSpinner.setVisibility(View.VISIBLE);
                 bathsSpinner.setVisibility(View.VISIBLE);
                 priceSpinner.setVisibility(View.VISIBLE);
                 price_sort_text.setVisibility(View.VISIBLE);
                 price_sort_text.setText("Sort by Price");
                 view2.setVisibility(View.VISIBLE);
                 view4.setVisibility(View.VISIBLE);
                 view5.setVisibility(View.VISIBLE);
                 occupationSpinner.setVisibility(View.GONE);
                    occupationText.setVisibility(View.GONE);

                } else if(selected_category.equals("For Sale")){
                    number_baths_text.setVisibility(View.VISIBLE);
                    number_toilets_text.setVisibility(View.VISIBLE);
                    toiletsSpinner.setVisibility(View.VISIBLE);
                    bathsSpinner.setVisibility(View.VISIBLE);
                    priceSpinner.setVisibility(View.VISIBLE);
                    price_sort_text.setVisibility(View.VISIBLE);
                    price_sort_text.setText("Sort by Price");
                    view2.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.VISIBLE);
                    view5.setVisibility(View.VISIBLE);
                    occupationSpinner.setVisibility(View.GONE);
                    occupationText.setVisibility(View.GONE);

                } else if(selected_category.equals("For Rent")){
                    number_baths_text.setVisibility(View.VISIBLE);
                    number_toilets_text.setVisibility(View.VISIBLE);
                    toiletsSpinner.setVisibility(View.VISIBLE);
                    bathsSpinner.setVisibility(View.VISIBLE);
                    priceSpinner.setVisibility(View.VISIBLE);
                    price_sort_text.setVisibility(View.VISIBLE);
                    price_sort_text.setText("Sort by Price");
                    view2.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.VISIBLE);
                    view5.setVisibility(View.VISIBLE);
                    occupationText.setVisibility(View.GONE);
                    occupationSpinner.setVisibility(View.GONE);

                } else if(selected_category.equals("Event Centres")){
                    number_baths_text.setVisibility(View.GONE);
                    number_toilets_text.setVisibility(View.GONE);
                    toiletsSpinner.setVisibility(View.GONE);
                    bathsSpinner.setVisibility(View.GONE);
                    priceSpinner.setVisibility(View.VISIBLE);
                    price_sort_text.setVisibility(View.VISIBLE);
                    price_sort_text.setText("Seating Capacity");
                    view2.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.GONE);
                    view5.setVisibility(View.GONE);
                    occupationSpinner.setVisibility(View.GONE);
                    occupationText.setVisibility(View.GONE);

                } else if(selected_category.equals("Service Apartments")){
                    number_baths_text.setVisibility(View.GONE);
                    number_toilets_text.setVisibility(View.GONE);
                    toiletsSpinner.setVisibility(View.GONE);
                    bathsSpinner.setVisibility(View.GONE);
                    priceSpinner.setVisibility(View.VISIBLE);
                    price_sort_text.setVisibility(View.VISIBLE);
                    price_sort_text.setText("Sort by Price");
                    view2.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.GONE);
                    view5.setVisibility(View.GONE);
                    occupationText.setVisibility(View.GONE);
                    occupationSpinner.setVisibility(View.GONE);

                } else if(selected_category.equals("Lease")){
                    number_baths_text.setVisibility(View.GONE);
                    number_toilets_text.setVisibility(View.GONE);
                    toiletsSpinner.setVisibility(View.GONE);
                    bathsSpinner.setVisibility(View.GONE);
                    priceSpinner.setVisibility(View.VISIBLE);
                    price_sort_text.setVisibility(View.VISIBLE);
                    price_sort_text.setText("Sort by Price");
                    view2.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.GONE);
                    view5.setVisibility(View.GONE);
                    occupationSpinner.setVisibility(View.GONE);
                    occupationText.setVisibility(View.GONE);

                } else if (selected_category.equals("Land")) {
                    number_baths_text.setVisibility(View.GONE);
                    number_toilets_text.setVisibility(View.GONE);
                    toiletsSpinner.setVisibility(View.GONE);
                    bathsSpinner.setVisibility(View.GONE);
                    priceSpinner.setVisibility(View.VISIBLE);
                    price_sort_text.setText("Sort by Price");
                    price_sort_text.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.GONE);
                    view5.setVisibility(View.GONE);
                    occupationText.setVisibility(View.GONE);
                    occupationSpinner.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bathsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected_baths = bathsSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        occupationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected_occupation = occupationSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        toiletsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected_toilets = toiletsSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_price = priceSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_area = locationSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
     filterButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             new Handler().postDelayed(new Runnable() {
                 public void run() {
                     final Query firstQuery;
                     if(selected_area!=null){
                         post_list.clear();
                         mProgress.setVisibility(View.VISIBLE);
                         if (selected_category.equals("All") && selected_price.equals("All Prices") && selected_state.equals("All") && selected_baths.equals("All") && selected_toilets.equals("All")) {
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("area", selected_area);
                         }else if(!selected_category.equals("All") && selected_price.equals("All Prices") && selected_state.equals("All") && selected_baths.equals("All") && selected_toilets.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("area", selected_area);
                         }else if(!selected_category.equals("All") && !selected_price.equals("All Prices") && selected_state.equals("All") && selected_baths.equals("All") && selected_toilets.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("price_range", selected_price).whereEqualTo("area", selected_area);
                         }else if(!selected_category.equals("All") && !selected_price.equals("All Prices") && !selected_state.equals("All") && selected_baths.equals("All") && selected_toilets.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("state", selected_state).whereEqualTo("price_range", selected_price).whereEqualTo("area", selected_area);
                         }else if(!selected_category.equals("All") && !selected_price.equals("All Prices") && !selected_state.equals("All") && !selected_baths.equals("All") && selected_toilets.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("state", selected_state).whereEqualTo("price_range", selected_price).whereEqualTo("area", selected_area).whereEqualTo("number_of_baths",selected_baths);
                         }else if(!selected_category.equals("All") && selected_price.equals("All Prices") && !selected_state.equals("All") && !selected_baths.equals("All") && !selected_toilets.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("state", selected_state).whereEqualTo("area", selected_area).whereEqualTo("number_of_baths",selected_baths);
                         } else if(!selected_category.equals("All") && selected_price.equals("All Prices") && selected_state.equals("All") && !selected_baths.equals("All") && !selected_toilets.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("area", selected_area).whereEqualTo("number_of_baths",selected_baths);
                         }  else if(!selected_category.equals("All") && selected_price.equals("All Prices") && selected_state.equals("All") && selected_baths.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("area", selected_area);
                         }else if(!selected_category.equals("All") && selected_price.equals("All Prices") && !selected_state.equals("All") && selected_baths.equals("All") && selected_toilets.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("state", selected_state).whereEqualTo("area", selected_area);
                         }else if(!selected_category.equals("All") && !selected_price.equals("All Prices") && !selected_state.equals("All") && !selected_baths.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("state", selected_state).whereEqualTo("price_range", selected_price).whereEqualTo("area", selected_area);
                         }else if(!selected_category.equals("All") && !selected_price.equals("All Prices") && selected_state.equals("All") && selected_baths.equals("All")){
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("price_range", selected_price).whereEqualTo("state", selected_state).whereEqualTo("area", selected_area);
                         } else {
                             firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("category", selected_category).whereEqualTo("state", selected_state).whereEqualTo("price_range", selected_price).whereEqualTo("area", selected_area).whereEqualTo("number_of_baths",selected_baths).whereEqualTo("number_of_toilets",selected_toilets);
                         }
                         firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                             @Override
                             public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                 if (e!=null){
                                     Log.d(TAG,"Error:"+e.getMessage());
                                 } else {
                                     for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                                         if (doc.getType() == DocumentChange.Type.ADDED){
                                             String blogPostID = doc.getDocument().getId();
                                             AgentPost agentPost = doc.getDocument().toObject(AgentPost.class).withId(blogPostID);
                                             post_list.add(agentPost);
                                             agentPostRecyclerAdapter.notifyDataSetChanged();
                                             mImageView.setVisibility(View.GONE);
                                             mTextView.setVisibility(View.GONE);
                                             mRecyclerView.setVisibility(View.VISIBLE);
                                             mRelativeLayout.setVisibility(View.GONE);
                                             dialog.dismiss();

                                         }
                                     }

                                 }
                             }
                         });
                         firstQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 if (task.getResult().isEmpty()){
                                     linearLayout.setVisibility(View.GONE);
                                     mImageView.setVisibility(View.VISIBLE);
                                     mTextView.setVisibility(View.VISIBLE);
                                     mRecyclerView.setVisibility(View.GONE);
                                     mRelativeLayout.setVisibility(View.GONE);
                                     dialog.dismiss();
                                 } else {
                                     mNothingText.setText(String.valueOf(task.getResult().size()));
                                     linearLayout.setVisibility(View.VISIBLE);
                                 }
                                 mProgress.setVisibility(View.GONE);
                             }
                         });
                     }else {
                         Toast.makeText(FilterActivity.this, "you have not selected a location", Toast.LENGTH_SHORT).show();
                     }


                 }
             }, 1000);

         }
     });


    }

    private void getOccupationsArray() {
        Query query = firebaseFirestore.collection("Posts").whereEqualTo("category", "Handyman");
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(task.getResult()!=null){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String blogPostID = document.getId();
                                AgentPost agentPost = document.toObject(AgentPost.class).withId(blogPostID);
                                occupationArray.add(agentPost.title);
                            }
                            ArrayAdapter<String> occupationAdapter = new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_spinner_item, occupationArray.toArray(new String[0]));
                            occupationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            occupationSpinner.setAdapter(occupationAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                }
            });
    }

    private void getLocationsArray() {
        firebaseFirestore.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult()!=null){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String blogPostID = document.getId();
                            AgentPost agentPost = document.toObject(AgentPost.class).withId(blogPostID);
                            if(agentPost.area!=null){
                                areaArray.add(agentPost.area);
                            }
                        }
                        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_spinner_item, areaArray.toArray(new String[0]));
                        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        locationSpinner.setAdapter(locationAdapter);

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.filter:
                resetLayout();
                break;
            case R.id.search:
                default:
                    super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void resetLayout() {
        mRelativeLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mImageView.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_location, menu);
        MenuItem item = menu.findItem(R.id.search);
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setQueryHint("Search your location");
//        searchView.setOnQueryTextListener(this);
        return true;
    }

    private List<AgentPost> filter (List<AgentPost> pi, String query){
        query = query.toLowerCase();
        final List<AgentPost> filteredList = new ArrayList<>();
        for(AgentPost agentPost: pi){
            final String text = agentPost.getArea().toLowerCase();
            if(text.contains(query)){
                filteredList.add(agentPost);
            }
        }
        return filteredList;
    }
    @Override
    public void onBackPressed() {
     if (mRelativeLayout.getVisibility() == View.GONE){
            resetLayout();

        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        }

}

