package com.kemoi.kvwallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    private static int SPLASH_SCREEN = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView backgroundImage = findViewById(R.id.SplashScreenImage);
        Animation slideAnimation = AnimationUtils.loadAnimation(this, R.anim.animator);
        backgroundImage.startAnimation(slideAnimation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, MainPage.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);

    }
}