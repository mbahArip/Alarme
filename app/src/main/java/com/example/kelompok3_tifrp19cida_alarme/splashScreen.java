package com.example.kelompok3_tifrp19cida_alarme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class splashScreen extends AppCompatActivity {
    boolean backPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.layout_splash);
        getSupportActionBar().hide();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(splashScreen.this, dashboard.class);
                startActivity(loginIntent);
                finish();
            }
        };
        new Handler().postDelayed(runnable, 2500);
    }

    @Override
    public void onBackPressed(){
        if(backPressed){
            finishAffinity();
            System.exit(0);
        } else {
            Toast.makeText(this, "Press again to exit...", Toast.LENGTH_SHORT).show();
            backPressed = true;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                backPressed = false;
            }
        };

        new Handler().postDelayed(runnable, 2000);
    }
}