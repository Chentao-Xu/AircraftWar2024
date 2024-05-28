package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    private String userName;
    private int userScore;
    private String userTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        listView = findViewById(R.id.list);
        Button returnBtn = findViewById(R.id.return_btn);

        // 获取Intent传递的数据
        Intent intent = getIntent();
        userScore = intent.getIntExtra("user_score", 0);
        userTime = intent.getStringExtra("user_time");
        int gameType = intent.getIntExtra("gameType",1);

        switch (gameType){
            case 1:
                file = new File(getFilesDir(), "mediumGame.dat");
                break;
            case 2:
                file = new File(getFilesDir(), "hardGame.dat");
                break;
            default:
                file = new File(getFilesDir(), "easyGame.dat");
                break;
        }

        // 初始化PlayerDao
        try {
            playerDao = new PlayerDaoImpl(this,file);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        inputName();

        // 返回按钮点击事件
        returnBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(RankingActivity.this,MainActivity.class);
            startActivity(intent1);
        });
    }

    private void inputName() {
        EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("请输入名字以记录得分")
                .setView(input);
        //.setNegativeButton("取消", null)
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            String name = input.getText().toString();
            if (name.isEmpty()){
                Toast.makeText(this, "输入为空", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    playerDao.addData(new Player(name,userScore,userTime));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                showList();
            }
        });

        builder.setNegativeButton("取消",(dialogInterface, i) -> {
            Toast.makeText(this, "您还未输入姓名", Toast.LENGTH_SHORT).show();
            showList();
        });

        builder.show();
    }

    private void showList() {
        // 获取所有数据并初始化排行榜列表
        rankingList = new ArrayList<>();
        List<Player> users = playerDao.getAllData();
        for (Player user : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", user.getName());
            map.put("score", user.getScore());
            map.put("time", user.getTime());
            rankingList.add(map);
        }

        // 设置适配器
        adapter = new SimpleAdapter(this, rankingList, R.layout.activity_ranking_item,
                new String[]{"name", "score", "time"},
                new int[]{R.id.list_name, R.id.score, R.id.time});
        listView.setAdapter(adapter);
    }

}