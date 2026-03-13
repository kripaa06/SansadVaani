package com.example.parliamentvoiceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parliamentvoiceapp.ui.screens.SplashScreen;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the SplashScreen (Kotlin Compose screen)
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);

        // Finish MainActivity so back press does not come here
        finish();
    }
}