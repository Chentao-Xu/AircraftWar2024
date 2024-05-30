package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.aircraftwar2024.ActivityManager;
import com.example.aircraftwar2024.R;

public class OfflineActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean isMusicOn;
    private int gameType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(this);

        setContentView(R.layout.activity_offline);

        Button easyModButton = findViewById(R.id.EasyModButton);
        Button normalModButton = findViewById(R.id.NormalModButton);
        Button hardModButton = findViewById(R.id.HardModButton);

        easyModButton.setOnClickListener(this);
        normalModButton.setOnClickListener(this);
        hardModButton.setOnClickListener(this);

        this.isMusicOn = getIntent().getBooleanExtra("isMusicOn",false);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.EasyModButton) {
            gameType = 0;
        }else if (v.getId() == R.id.NormalModButton) {
            gameType = 1;
        } else if (v.getId() == R.id.HardModButton) {
            gameType = 2;
        }
        Intent intent = new Intent(OfflineActivity.this, GameActivity.class);
        intent.putExtra("isMusicOn",isMusicOn);
        intent.putExtra("gameType",gameType);
        startActivity(intent);
    }
}