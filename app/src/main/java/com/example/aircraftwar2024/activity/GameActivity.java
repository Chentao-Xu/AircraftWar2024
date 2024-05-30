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

import com.example.aircraftwar2024.ActivityManager;
import com.example.aircraftwar2024.playerDAO.Player;
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
        ActivityManager.getActivityManager().addActivity(this);

        getScreenHW();

        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);
            Log.i("GameType",""+gameType);
        }

        // 游戏结束
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage");
                if (msg.what == 1) {
                    Intent intent = new Intent(GameActivity.this, RecordActivity.class);
                    Player player = (Player) msg.obj;
                    intent.putExtra("user_name", player.getName());
                    intent.putExtra("user_score", player.getScore());
                    intent.putExtra("user_time", player.getTime());
                    intent.putExtra("gameType", gameType);
                    Toast.makeText(GameActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        };

        /*TODO:根据用户选择的难度加载相应的游戏界面*/
        BaseGame baseGameView;
        switch (gameType) {
            case 1 :
                baseGameView = new MediumGame(this,handler);
                break;
            case 2 :
                baseGameView = new HardGame(this,handler);
                break;
            default:
                baseGameView = new EasyGame(this,handler); // 默认为简单模式
                break;
        }
        setContentView(baseGameView);
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
}