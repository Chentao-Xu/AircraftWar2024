package com.example.aircraftwar2024.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.aircraftwar2024.ImageManager;
import com.example.aircraftwar2024.activity.MainActivity;
import com.example.aircraftwar2024.aircraft.AbstractEnemyAircraft;
import com.example.aircraftwar2024.playerDAO.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class OnlineGame extends HardGame{

    private int enemyScore;
    private Socket socket;

    public OnlineGame(Context context, Handler handler, boolean isMusicOn, Socket socket) throws IOException {
        super(context, handler, isMusicOn);
        this.socket = socket;
        new ClientThread().start();
    }

    public void setEnemyScore(int enemyScore) {
        this.enemyScore = enemyScore;
    }

    @Override
    protected void gameOver(){
        gameOverFlag = true;

        if(isMusicOn) {
            bgmPlayer.release();
            bossBgmPlayer.release();
            mySoundPool.playSound(SOUND_GAME_OVER);
        }

        Log.i(TAG, "heroAircraft is not Valid");
        isPaused = true;
    }

    @Override
    protected void paintScoreAndLife() {
        super.paintScoreAndLife();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(60);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // 绘制敌方分数
        canvas.drawText("EnemyScore: " + enemyScore, 400, 100, mPaint);
    }

    private class ClientThread extends Thread{
        private Socket socket = null;
        private PrintWriter writer = null;
        private BufferedReader in = null;

        ClientThread() throws IOException {}

        @Override
        public void run(){
            try {
                socket = MainActivity.socket;
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
                writer = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream(),"utf-8")),true);

                new Thread(()->{
                    while (!gameOverFlag) {
                        try {
                            writer.println("" + getScore());
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    writer.println("end");
                }).start();

                new Thread(()->{
                    try {
                        String content;
                        while((content = in.readLine()) != null){
                            if (content.equals("end")){
                                //获取当前时间
                                Date date = new Date();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
                                player = new Player("test",getScore(),formatter.format(date));

                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = player;
                                message.arg1 = enemyScore;
                                handler.sendMessage(message);

                                mbLoop = false;

                                break;
                            }else {
                                enemyScore = Integer.parseInt(content);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
