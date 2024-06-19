package com.example.aircraftwar2024.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar2024.ActivityManager;
import com.example.aircraftwar2024.R;
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

public class OnlineActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean isMusicOn;
    private Context context = this;

    public static Socket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(this);

        setContentView(R.layout.activity_online);

        Button createRoomButton = findViewById(R.id.CreateRoomButton);
        Button addRoomButton = findViewById(R.id.AddRoomButton);

        createRoomButton.setOnClickListener(this);
        addRoomButton.setOnClickListener(this);

        this.isMusicOn = getIntent().getBooleanExtra("isMusicOn",false);
    }

    @Override
    public void onClick(View v){
        //设置创建房间功能
        if (v.getId() == R.id.CreateRoomButton) {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage("创建房间中\n\n").create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                new ClientThread(alertDialog).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        //设置加入房间功能
        if (v.getId() == R.id.AddRoomButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入房间号：");
            final EditText editText = new EditText(this);
            builder.setView(editText);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String text = editText.getText().toString().trim();
                    try {
                        int roomId = Integer.parseInt(text);
                        AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage("寻找房间中...\n\n").create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        new ClientThread(alertDialog, roomId).start();
                    } catch (NumberFormatException e){
                        //若输入不为数字
                        Dialog alertDialog = new AlertDialog.Builder(context).setMessage("请输入正确的房间号").create();
                        alertDialog.show();
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private class ClientThread extends Thread{
        private PrintWriter writer = null;
        private BufferedReader in = null;
        private String HOST = "10.0.2.2";
        private int PORT = 9999;
        private boolean isHost;
        private boolean canStart = false;
        private int playerNum = 0;
        private int roomId = 0;
        private AlertDialog dialog = null;
        private AlertDialog dialogForHost = null;
        private boolean isEnd = false;

        ClientThread(AlertDialog dialog) throws IOException {
            isHost = true;
            this.dialog = dialog;
        }

        ClientThread(AlertDialog dialog, int roomId) {
            isHost = false;
            this.roomId = roomId;
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
                //向服务端传输起始信息
                if(isHost){
                    writer.println("host");
                    roomId = Integer.parseInt(in.readLine());
                }else{
                    writer.println(roomId);
                    String content = in.readLine();
                    if(content.equals("notfind")){
                        dialog.setMessage("未找到该房间");
                        dialog.setCancelable(true);
                        isEnd = true;
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //接收服务端信息
            new Thread(() -> {
                try {
                    String content;
                    while (!isEnd && (content = in.readLine()) != null) {
                        if(content.equals("start")){
                            if(!isHost) {
                                dialog.dismiss();
                            }else{
                                dialogForHost.dismiss();
                            }
                            Intent intent = new Intent(OnlineActivity.this, OnlineGameActivity.class);
                            intent.putExtra("isMusicOn", isMusicOn);
                            startActivity(intent);
                            isEnd = true;
                        }else {
                            playerNum = Integer.parseInt(content);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            //更改对话框内容
            new Thread(() -> {
                while (!isEnd) {
                    if(!isHost) {
                        dialog.setMessage("房间号:" + roomId + "\n当前共" + playerNum + "人\n等待房主开始游戏...");
                    }else {
                        if (playerNum <= 1) {
                            dialog.setMessage("房间号:" + roomId + "\n当前共" + playerNum + "人\n等待其他玩家加入...");
                        } else if(!canStart){
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("房间号:" + roomId + "\n当前共" + playerNum + "人")
                                    .setCancelable(false)
                                    .setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            writer.println("start");
                                        }
                                    });
                            Looper.prepare();
                            dialogForHost = builder.create();
                            dialogForHost.show();
                            canStart = true;
                            System.out.println("sad" + playerNum);
                            Looper.loop();
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            //更改对话框内容
            new Thread(() -> {
                while (!isEnd) {
                    if(isHost && canStart && dialogForHost!=null){
                        dialogForHost.setMessage("房间号:" + roomId + "\n当前共" + playerNum + "人");
                        System.out.println("happy" + playerNum);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();


        }

    }

}
