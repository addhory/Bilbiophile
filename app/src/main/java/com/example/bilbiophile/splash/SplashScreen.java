package com.example.bilbiophile.splash;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bilbiophile.MainActivity;
import com.example.bilbiophile.R;


public class SplashScreen extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    ImageView logoImage;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        constraintLayout = findViewById(R.id.mConstraintSplash);
        logoImage = findViewById(R.id.mImageLogo);
        Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        appName = findViewById(R.id.appName);
        constraintLayout.startAnimation(mAnimation);
        logoImage.startAnimation(mAnimation);
        appName.startAnimation(mAnimation);

        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(5000);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }
}

