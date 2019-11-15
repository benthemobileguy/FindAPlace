package com.fap.bnotion.findaplace;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HousingDetailActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    String username, title, location, description, price, category, baths, toilets, state, phoneNumber, emailText, status, imageURL, imageURLS, profileURL, userId, dateString;
    private ImageView mPostedHouse;
    private TextView mTitle, mNoBaths, mLocation, mCategoryText, mPriceText, mStateText, mBathsText, mToiletsText, mDescriptionText, mUsernameText, mEmailText, mNumberText, mStatusText, mTimeText;
    private Button mCall, mSMS, mEmail;
    private CircleImageView mProfileImage;
    private FloatingActionButton fab_fav;
    private String fabState = "gone";
    private String buttonState = "hid";
    private int image_no;
    private Button mDeleteButton;
    private ArrayList<String> image_urls;
    // int id = 0;
    private ScrollView mScrollView;
    private AdView mAdView;
    private LinearLayout bathsLayout, toiletsLayout, priceLayout;

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
        setContentView(R.layout.activity_housing_detail);
        StrictMode.VmPolicy.Builder strictBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(strictBuilder.build());
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final TextView textView = findViewById(R.id.trending_text);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        MobileAds.initialize(this, "ca-app-pub-8534200199388023~7161551446");
        mAdView = findViewById(R.id.adView);
        mScrollView  = findViewById(R.id.scrollView);
        bathsLayout = findViewById(R.id.bathsLayout);
        toiletsLayout = findViewById(R.id.toiletsLayout);
        priceLayout = findViewById(R.id.price_layout);
        mLocation = findViewById(R.id.location);
        mCall = findViewById(R.id.call);
        mEmail = findViewById(R.id.email_btn);
        mSMS = findViewById(R.id.sms);
        mCategoryText = findViewById(R.id.category_text);
        mPriceText = findViewById(R.id.price_text);
        mStateText = findViewById(R.id.state_text);
        mBathsText = findViewById(R.id.baths_text);
        mToiletsText = findViewById(R.id.toilets_text);
        mDescriptionText = findViewById(R.id.description_text);
        mProfileImage = findViewById(R.id.profile_image);
        mUsernameText = findViewById(R.id.username_text);
        mEmailText = findViewById(R.id.email_text);
        mNumberText = findViewById(R.id.number_text);
        mStatusText = findViewById(R.id.status_text);
        mTimeText = findViewById(R.id.time_text);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        fab_fav = findViewById(R.id.fab_favourites);
        mNoBaths = findViewById(R.id.no_of_baths);
        mDeleteButton = findViewById(R.id.delete_button);
        databaseHelper = new DatabaseHelper(this);
        //Get String Extras
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        title = intent.getStringExtra("title");
        location = intent.getStringExtra("location");
        description = intent.getStringExtra("description");
        price = intent.getStringExtra("price");
        category = intent.getStringExtra("category");
        baths = intent.getStringExtra("baths");
        toilets = intent.getStringExtra("toilets");
        dateString = intent.getStringExtra("dateString");
        state = intent.getStringExtra("state");
        phoneNumber = intent.getStringExtra("phoneNumber");
        emailText = intent.getStringExtra("email");
        status = intent.getStringExtra("status");
        imageURL = intent.getStringExtra("imageURL");
        image_urls = getIntent().getStringArrayListExtra("imageURLS");
        if (category.equals("Land") || title.contains("LAND") || title.contains("Land") || title.contains("land")) {
            bathsLayout.setVisibility(View.GONE);
            toiletsLayout.setVisibility(View.GONE);
        }
        if (category.equals("Event Centres")) {
            toiletsLayout.setVisibility(View.GONE);
            mNoBaths.setText("Seating Capacity");

        }
        if (category.equals("Service Apartments")) {
            bathsLayout.setVisibility(View.GONE);
            toiletsLayout.setVisibility(View.GONE);

        }
        if(category.equals("Handyman")){
            bathsLayout.setVisibility(View.GONE);
            toiletsLayout.setVisibility(View.GONE);
            priceLayout.setVisibility(View.GONE);
        }
        if (image_urls != null) {
            image_no = image_urls.size();
        }
        final String imageNumberString = String.valueOf(image_no);
        if (image_urls != null) {

            if (imageURL.equals("null") && image_urls.size() > 1) {
                textView.setText(imageNumberString + " Images (Tap to View)");
            } else if (imageURL.equals("null") && image_urls.size() == 1) {
                textView.setText(imageNumberString + " Image (Tap to View)");
            } else {
                textView.setText("1" + " Image (Tap to View)");
            }
            if (image_urls != null) {
                imageURLS = image_urls.get(0);
            }
        }
        profileURL = intent.getStringExtra("profileURL");
        userId = intent.getStringExtra("userID");
        if (intent.hasExtra("fabState")) {
            fabState = intent.getStringExtra("fabState");
        }

        if (fabState.equals("shown")) {
            fab_fav.hide();
        }
        if (intent.hasExtra("showButton")) {
            buttonState = intent.getStringExtra("showButton");
        }

        if (buttonState.equals("show")) {
            mDeleteButton.setVisibility(View.VISIBLE);
        }
        //finding views by IDS
        mTitle = findViewById(R.id.title);
        mPostedHouse = findViewById(R.id.posted_house);
        //id = databaseHelper.getId(description);
        mPostedHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imageURL.equals("null")) {
                    Intent intent = new Intent(HousingDetailActivity.this, LoadImageActivity.class);
                    intent.putExtra("imageURL", imageURL);
                    intent.putExtra("toolbarText", imageNumberString);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HousingDetailActivity.this, LoadImagesActivity.class);
                    intent.putExtra("imageURLS", image_urls);
                    intent.putExtra("toolbarText", imageNumberString);
                    startActivity(intent);
                }

            }
        });
        //setting widgets
        mTitle.setText(title  + " (" + category + ")");
        mLocation.setText(location);
        mUsernameText.setText(username);
        mDescriptionText.setText(description);
        mPriceText.setText("₦" + price);
        mCategoryText.setText(category);
        if (category.equals("Event Centres") && baths.equals("One")) {
            mBathsText.setText("Not available");
        } else {
            mBathsText.setText(baths);
        }
        mToiletsText.setText(toilets);
        mStateText.setText(state);
        mEmailText.setText(emailText);
        mStatusText.setText(status);
        mNumberText.setText(phoneNumber);
        mTimeText.setText(dateString);
        RequestOptions placeholderRequest = new RequestOptions();
        RequestOptions placeholderRequest2 = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.placeholder).centerCrop();
        placeholderRequest2.placeholder(R.drawable.circle_image).centerCrop();
        if (imageURL.equals("null")) {
            Glide.with(this).setDefaultRequestOptions(placeholderRequest).load(imageURLS).into(mPostedHouse);
        } else {
            Glide.with(this).setDefaultRequestOptions(placeholderRequest).load(imageURL).into(mPostedHouse);
        }

        Glide.with(this).setDefaultRequestOptions(placeholderRequest2).load(profileURL).into(mProfileImage);
        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });
       mSMS.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Uri uri = Uri.parse("smsto:"  + phoneNumber);
               Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
               startActivity(intent);
           }
       });
       mEmail.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent email = new Intent(Intent.ACTION_SENDTO);
               email.setType("text/plain");
               email.setData(Uri.parse("mailto:" + emailText));
               email.putExtra(Intent.EXTRA_SUBJECT, "My Request");
               email.putExtra(Intent.EXTRA_TEXT, "Type your message");
               try {
                   startActivity(email);
               } catch (ActivityNotFoundException ex) {
                   Toast.makeText(HousingDetailActivity.this, "No email apps installed", Toast.LENGTH_SHORT).show();
               } catch (Exception ex) {
                   Toast.makeText(HousingDetailActivity.this, "Unexpected Error", Toast.LENGTH_SHORT).show();
               }
           }
       });
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mScrollView != null) {
                    if (mScrollView.getScrollY()==0) {
                      mToolbar.setVisibility(View.VISIBLE);
                    } else {
                        mToolbar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        fab_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (databaseHelper.checkAlreadyExist(description)) {
                    databaseHelper.insertData(imageURL, profileURL, title, category, location, price, toilets, baths, state, userId, username, phoneNumber, status, emailText, image_urls, description, dateString);
                    Toast.makeText(HousingDetailActivity.this, "Added to favourites list", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HousingDetailActivity.this, "(ERROR): Unable to add to favourites. Item already exists!", Toast.LENGTH_LONG).show();
                }
            }
        });
//        mDeleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final AlertDialog alertDialog = new AlertDialog.Builder(HousingDetailActivity.this)
//                        .setCancelable(true)
//                        .setMessage("Do you really want to delete this post from favourite list?")
//                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Integer deleteRow = databaseHelper.deleteData(String.valueOf(id));
//                                if (deleteRow > 0) {
//                                    Toast.makeText(HousingDetailActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
//                                    Intent intent1 = new Intent(HousingDetailActivity.this, MainActivity.class);
//                                    startActivity(intent1);
//                                    finish();
//                                } else {
//                                    Toast.makeText(HousingDetailActivity.this, "Unable to Delete", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).create();
//                alertDialog.show();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.share:
                shareHousingInfo();
                break;
            case R.id.report_abuse:
                reportAbuse();
                break;
        }
        return true;
    }

    private void reportAbuse() {
        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setType("text/plain");
        email.setData(Uri.parse("mailto:info@findaplace.com.ng"));
        email.putExtra(Intent.EXTRA_SUBJECT, "Abuse Report");
        try {
            startActivity(email);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No email apps installed", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareHousingInfo() {
        Bitmap bitmap = getBitmapFromView(mPostedHouse);
        try {
            File file = new File(this.getExternalCacheDir(), "image.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Find A Place");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, title.toUpperCase() + "\n\n" + description + "\n\nTap on the Link Below to View Full Property Details\n\nhttps://goo.gl/kmL9zi");
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Choose App"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}