package com.fap.bnotion.findaplace;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SupportActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_support);
        RelativeLayout emailBtn = findViewById(R.id.email_btn);
        RelativeLayout callBtn = findViewById(R.id.call_btn);
        RelativeLayout whatsapBtn = findViewById(R.id.whatsapp_btn);
        homeButtonIntent();
        sendEmailIntent();
        faceBookIntent();
        twitterIntent();
        instagramIntent();
//        line1();
//        line2();
//        line3();
//        line4();
//        webIntent();
//        mailIntent();
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:bnotionsoftware@gmail.com"));
                email.putExtra(Intent.EXTRA_SUBJECT, "Your Name");
                try {
                    startActivity(email);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(SupportActivity.this, "No email apps installed", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(SupportActivity.this, "Unexpected Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+2348136174253";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        whatsapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsNumber="+2347087384070";
                Uri uri = Uri.parse("smsto:" + smsNumber);
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                startActivity(i);
            }
        });

    }

    private void mailIntent() {
        LinearLayout mWebsite = findViewById(R.id.email);
        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void webIntent() {
        LinearLayout mMail = findViewById(R.id.website);
        mMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.findaplace.com.ng");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }


    private void line1() {
        LinearLayout line1 = findViewById(R.id.line_1);
        line1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+2347087384070"));
                startActivity(intent);
            }
        });
    }
    private void line2() {
        LinearLayout line2 = findViewById(R.id.line_2);
        line2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+2348100599608"));
                startActivity(intent);
            }
        });
    }
    private void line3() {
        LinearLayout line3 = findViewById(R.id.line_3);
        line3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+2348033761447"));
                startActivity(intent);
            }
        });
    }
    private void line4() {
        LinearLayout line4 = findViewById(R.id.line_4);
        line4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+2347087384070"));
                startActivity(intent);
            }
        });
    }
    private void instagramIntent() {
        ImageView mInstagramIntent = findViewById(R.id.instagram_web);
        mInstagramIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/findaplace.com.ng/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void twitterIntent() {
        ImageView mTwitterIntent = findViewById(R.id.twitter_web);
        mTwitterIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://twitter.com/FindaplaceN");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void faceBookIntent() {
        ImageView mFacebookIntent = findViewById(R.id.facebook_web);
        mFacebookIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.facebook.com/Find-A-Place-345288319368270/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void sendEmailIntent() {
        ImageView mSendEmail = findViewById(R.id.mail);
        mSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setType("text/plain");
        email.setData(Uri.parse("mailto:bnotionsoftware@gmail.com"));
        email.putExtra(Intent.EXTRA_SUBJECT, "Enter Subject Here");
        email.putExtra(Intent.EXTRA_TEXT, "Type your message");
        try {
            startActivity(email);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No email apps installed", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void homeButtonIntent() {
        ImageView mHomeButton = findViewById(R.id.home);
        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SupportActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
