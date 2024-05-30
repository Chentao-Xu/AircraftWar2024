package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.aircraftwar2024.ActivityManager;
import com.example.aircraftwar2024.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean isMusicOn = false;
    private RadioGroup musicRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(this);

        setContentView(R.layout.activity_main);

        Button singlePlayerButton = findViewById(R.id.singlePlayerButton);
        musicRadioGroup = findViewById(R.id.musicRadioGroup);

        singlePlayerButton.setOnClickListener(this);

        musicRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.musicOnRadioButton) {
                isMusicOn = true;
            }
            else if (checkedId == R.id.musicOffRadioButton){
                    isMusicOn = false;
            }
        });
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
        intent.putExtra("isMusicOn",isMusicOn);
        startActivity(intent);
    }
}