package com.example.aircraftwar2024.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.aircraftwar2024.game.OnlineGame;
import com.example.aircraftwar2024.playerDAO.Player;

import java.io.IOException;


public class OnlineGameActivity extends GameActivity {

    private static final String TAG = "OnlineActivity";

    private Handler handler;
    private boolean isMusicOn;
    private OnlineGame gameView;

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
                        OnlineActivity.socket.close();
                        OnlineActivity.socket = null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Intent intent = new Intent(OnlineGameActivity.this, MainActivity.class);
                    Player player = (Player) msg.obj;
                    intent.putExtra("myScore", player.getScore());
                    intent.putExtra("maxScore", msg.arg1);
                    Toast.makeText(OnlineGameActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

            }
        };

        if (getIntent() != null) {
            isMusicOn = getIntent().getBooleanExtra("isMusicOn", false);
        }

        try {
            gameView = new OnlineGame(this,handler,isMusicOn,OnlineActivity.socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setContentView(gameView);

    }

}
