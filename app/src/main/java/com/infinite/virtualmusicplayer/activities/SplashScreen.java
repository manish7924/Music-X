package com.infinite.virtualmusicplayer.activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MainActivity;

//import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    TextView appname;
//    LottieAnimationView lottieanim;
//    LottieAnimationView chatanim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appname = findViewById(R.id.app_name);
//        lottieanim = findViewById(R.id.lottie_anim);
//        chatanim = findViewById(R.id.chat_anim);


//        appname.animate().translationY(-950).setDuration(0).setStartDelay(0);
//        lottieanim.animate().translationY(3000).setDuration(2200).setStartDelay(2200);
//        chatanim.animate().translationZ(2000).setDuration(2500).setStartDelay(2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 0);

    }
}
