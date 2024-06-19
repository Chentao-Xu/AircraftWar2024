package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.aircraftwar2024.ActivityManager;
import com.example.aircraftwar2024.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean isMusicOn = false;
    private RadioGroup musicRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(this);

        setContentView(R.layout.activity_main);

        Button singlePlayerButton = findViewById(R.id.singlePlayerButton);
        Button onlineGameButton = findViewById(R.id.onlineGameButton);
        musicRadioGroup = findViewById(R.id.musicRadioGroup);

        //设置单机游戏按钮功能
        singlePlayerButton.setOnClickListener(this);
        //设置音乐开关功能
        musicRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.musicOnRadioButton) {
                isMusicOn = true;
            }
            else if (checkedId == R.id.musicOffRadioButton){
                isMusicOn = false;
            }
        });
        //设置联机对战按钮功能
        onlineGameButton.setOnClickListener(this);

        //联机对战结束后直接返回主页，跳出对话框
        if (getIntent() != null) {
            int myScore = getIntent().getIntExtra("myScore", -1);
            int maxScore = getIntent().getIntExtra("maxScore", -1);
            if(myScore != -1) {
                Dialog alertDialog = new AlertDialog.Builder(this).setMessage("你的分数:" + myScore + "\n房间内最高分:" + maxScore).create();
                alertDialog.show();
            }
        }

    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.singlePlayerButton) {
            Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
            intent.putExtra("isMusicOn", isMusicOn);
            startActivity(intent);
        }
        if (v.getId() == R.id.onlineGameButton) {
            Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
            intent.putExtra("isMusicOn", isMusicOn);
            startActivity(intent);
        }
    }

}