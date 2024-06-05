package com.example.aircraftwar2024.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar2024.game.BaseGame;
import com.example.aircraftwar2024.game.HardGame;
import com.example.aircraftwar2024.game.OnlineGame;
import com.example.aircraftwar2024.playerDAO.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class OnlineActivity extends GameActivity {

    private static final String TAG = "OnlineActivity";

    private Handler handler;
    private boolean isMusicOn;
    private OnlineGame gameView;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage");
                if (msg.what == 1) {
                    try {
                        MainActivity.socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Intent intent = new Intent(OnlineActivity.this, MainActivity.class);
                    Player player = (Player) msg.obj;
                    intent.putExtra("myScore", player.getScore());
                    intent.putExtra("enemyScore", msg.arg1);
                    Toast.makeText(OnlineActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

            }
        };

        if (getIntent() != null) {
            isMusicOn = getIntent().getBooleanExtra("isMusicOn", false);
        }

        try {
            gameView = new OnlineGame(this,handler,isMusicOn,MainActivity.socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setContentView(gameView);

    }



}
