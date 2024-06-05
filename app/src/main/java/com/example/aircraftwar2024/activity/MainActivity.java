package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.aircraftwar2024.ActivityManager;
import com.example.aircraftwar2024.R;
import com.example.aircraftwar2024.game.BaseGame;
import com.example.aircraftwar2024.game.OnlineGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean isMusicOn = false;
    private RadioGroup musicRadioGroup;

    private Context context = this;
    public static Socket socket;

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

        if (getIntent() != null) {
            int myScore = getIntent().getIntExtra("myScore", -1);
            int enemyScore = getIntent().getIntExtra("enemyScore", -1);
            if(myScore != -1) {
                Dialog alertDialog = new AlertDialog.Builder(context).setMessage("你的分数:" + myScore + "\n对手分数:" + enemyScore).create();
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
            try {
                Dialog alertDialog = new AlertDialog.Builder(context).setMessage("匹配中，请等待...").create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                new ClientThread(alertDialog).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //客户端线程
    private class ClientThread extends Thread{
        private Dialog dialog;
        private PrintWriter writer = null;
        private BufferedReader in = null;
        private String HOST = "10.0.2.2";
        private int PORT = 9999;
        private int myScore;
        private OnlineGame game = null;

        ClientThread(Dialog dialog) throws IOException {
            this.dialog = dialog;
        }

        @Override
        public void run(){
            try {
                socket = new Socket(HOST,PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));

                writer = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream(),"utf-8")),true);
                String content;

                if(in.readLine().equals("start")){
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
                    intent.putExtra("isMusicOn",isMusicOn);
                    startActivity(intent);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}