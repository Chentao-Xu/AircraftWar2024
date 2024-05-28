package com.example.aircraftwar2024.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar2024.dao.Player;
import com.example.aircraftwar2024.game.BaseGame;
import com.example.aircraftwar2024.game.EasyGame;
import com.example.aircraftwar2024.game.HardGame;
import com.example.aircraftwar2024.game.MediumGame;


public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private int gameType=0;
    public static int screenWidth,screenHeight;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getScreenHW();

        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);
            Log.i("GameType",""+gameType);
        }

        /*TODO:根据用户选择的难度加载相应的游戏界面*/
        BaseGame baseGameView;
        switch (gameType) {
            case 1 :
                baseGameView = new MediumGame(this);
                break;
            case 2 :
                baseGameView = new HardGame(this);
                break;
            default:
                baseGameView = new EasyGame(this); // 默认为简单模式
                break;
        }
        setContentView(baseGameView);

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage");
                if (msg.what == 1) {
                    Intent intent = new Intent(GameActivity.this, RankingActivity.class);
                    Player user = (Player) msg.obj;
                    intent.putExtra("user_name", user.getName());
                    intent.putExtra("user_score", user.getScore());
                    intent.putExtra("user_time", user.getTime());
                    Toast.makeText(GameActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        };

        simulateGameOver();
    }

    public void getScreenHW(){
        //定义DisplayMetrics 对象
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getDisplay().getRealMetrics(dm);

        //窗口的宽度
        screenWidth= dm.widthPixels;
        //窗口高度
        screenHeight = dm.heightPixels;

        Log.i(TAG, "screenWidth : " + screenWidth + " screenHeight : " + screenHeight);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void simulateGameOver() {
        new Thread(() -> {
            try {
                Thread.sleep(10000); // 模拟游戏运行3秒后结束
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Player user = new Player("Player1", 100, "2024-5-7");
            Message message = handler.obtainMessage(1, user);
            handler.sendMessage(message);
        }).start();
    }
}