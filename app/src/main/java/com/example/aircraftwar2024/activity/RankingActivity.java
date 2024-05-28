package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.aircraftwar2024.R;
import com.example.aircraftwar2024.playerDAO.Player;
import com.example.aircraftwar2024.playerDAO.PlayerDao;
import com.example.aircraftwar2024.playerDAO.PlayerDaoImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter adapter;
    private ArrayList<Map<String, Object>> rankingList;
    private PlayerDao playerDao;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        listView = findViewById(R.id.list);
        Button returnBtn = findViewById(R.id.return_btn);

        // 获取Intent传递的数据
        Intent intent = getIntent();
        String userName = intent.getStringExtra("user_name");
        int userScore = intent.getIntExtra("user_score", 0);
        String userTime = intent.getStringExtra("user_time");
        int gameType = intent.getIntExtra("gameType",1);

        switch (gameType){
            case 1 :
                file = new File("mediumRank.dat");
                break;
            case 2:
                file = new File("hardRank.dat");
                break;
            default:
                file = new File("easyRank.dat");
                break;
        }

        //getName();

        // 初始化PlayerDao
        try {
            playerDao = new PlayerDaoImpl(this,file);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // 将新用户数据添加到榜单
        Player newPlayer = new Player(userName, userScore, userTime);
        try {
            playerDao.addData(newPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取所有数据并初始化排行榜列表
        rankingList = new ArrayList<>();
        List<Player> players = playerDao.getAllData();
        for (Player player : players) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", player.getName());
            map.put("score", player.getScore());
            map.put("time", player.getTime());
            rankingList.add(map);
        }


        // 设置适配器
        adapter = new SimpleAdapter(this, rankingList, R.layout.activity_ranking,
                new String[]{"name", "score", "time"},
                new int[]{R.id.list_name, R.id.score, R.id.time});
        listView.setAdapter(adapter);

        // 返回按钮点击事件
        returnBtn.setOnClickListener(view -> finish());
    }

//    private void showNameInputDialog() {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View dialogView = inflater.inflate(R.layout.dialog_input_name, null);
//        EditText editTextUserName = dialogView.findViewById(R.id.editTextUserName);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("输入用户名");
//        builder.setView(dialogView);
//        builder.setPositiveButton("确定", (dialog, which) -> {
//            String userName = editTextUserName.getText().toString().trim();
//            if (userName.isEmpty()) {
//                userName = "DefaultName"; // 默认名称
//            }
//        });
//        builder.setNegativeButton("取消", (dialog, which) -> {
//            finish(); // 取消操作后返回
//        });
//
//        builder.setCancelable(false); // 设置为不可取消
//        builder.show();
//    }

}