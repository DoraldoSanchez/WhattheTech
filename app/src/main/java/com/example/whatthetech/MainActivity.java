package com.example.whatthetech;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;

    public void animationLogo () {
        //sumTextView = findViewById (R.id.sumTextView); find view by id in on create method
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(imageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f));
        scaleDown.setDuration(500);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();
    }

    public void Next (View view) {

        Intent intent = new Intent(getApplicationContext(), Second_Activity.class);

        startActivity(intent);
        //finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        animationLogo();

        Intent intent = getIntent();
    }
}