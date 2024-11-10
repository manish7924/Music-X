package com.infinite.virtualmusicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.infinite.virtualmusicplayer.R;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backBtn(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        super.onBackPressed();
        finish();
    }

    public void gitBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/manish7924/Music-X"));
        startActivity(intent);
    }

    public void instaBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/mr._stark7?igsh=YzljYTk1ODg3Zg=="));
        startActivity(intent);
    }

    public void linkedInBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/manish-chidar-393b1525b?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app"));
        startActivity(intent);
    }

    public void whatsapp(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029VaQCaJZCnA7zDT0OjE34"));
        startActivity(intent);
    }

    public void shareWithFriends(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share song with");
        String string = "Try this Music player app Music X with amazing features" +
                "" +
                "\n\nhttps://github.com/manish7924/Music-X/releases";
        shareIntent.putExtra(Intent.EXTRA_TEXT, string);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }


    public void xBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://x.com/Manishx792"));
        startActivity(intent);
    }
}