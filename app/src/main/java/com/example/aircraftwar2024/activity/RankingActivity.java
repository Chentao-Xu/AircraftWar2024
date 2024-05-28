package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aircraftwar2024.R;

import java.util.ArrayList;

public class RankingActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> rankingList;
    private TextView scoreView, timeView;

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

        // 初始化数据
        rankingList = new ArrayList<>();
        rankingList.add("用户1 -  120 -  1000");  // 示例数据
        rankingList.add("用户2 -  110 -  900");   // 示例数据
        rankingList.add(userName + " - " + userScore + " - " + userTime); // 当前用户数据

        // 设置适配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rankingList);
        listView.setAdapter(adapter);

        // 返回按钮点击事件
        returnBtn.setOnClickListener(view -> finish());
    }
}