package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.aircraftwar2024.R;
import com.example.aircraftwar2024.playerDAO.Player;
import com.example.aircraftwar2024.playerDAO.PlayerDao;
import com.example.aircraftwar2024.playerDAO.PlayerDaoImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter adapter;
    private ArrayList<Map<String, Object>> rankingList;
    private PlayerDao playerDao;
    private int userScore;
    private String userTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        listView = findViewById(R.id.list);
        Button returnBtn = findViewById(R.id.return_btn);

        // 获取Intent传递的数据
        Intent intent = getIntent();
        userScore = intent.getIntExtra("user_score", 0);
        userTime = intent.getStringExtra("user_time");
        int gameType = intent.getIntExtra("gameType",1);

        String file;
        switch (gameType){
            case 1:
                file = "mediumGame.dat";
                break;
            case 2:
                file = "hardGame.dat";
                break;
            default:
                file = "easyGame.dat";
                break;
        }

        // 初始化PlayerDao
        try {
            playerDao = new PlayerDaoImpl(this, file);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        inputName();

        // 返回按钮点击事件
        returnBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(RecordActivity.this,MainActivity.class);
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
            }
            showList();
        });

        builder.setNegativeButton("取消",(dialogInterface, i) -> {
            showList();
        });

        builder.show();
    }

    private void showList() {
        // 获取所有数据并初始化排行榜列表
        rankingList = getRankingList();

        // 设置适配器
        adapter = new SimpleAdapter(this, rankingList, R.layout.activity_item,
                new String[]{"index", "name", "score", "time"},
                new int[]{R.id.index, R.id.list_name, R.id.score, R.id.time});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, l) -> {

            AlertDialog alertDialog = new AlertDialog.Builder(RecordActivity.this)
                    .setTitle("提示")
                    .setMessage("确认删除该条记录吗")
                    .setPositiveButton("确定", (dialogInterface, j) -> {

                        //更新排行榜文件
                        try {
                            playerDao.deleteData(position);
                            rankingList = getRankingList();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                    })

                    .setNegativeButton("取消", (dialogInterface, i) -> {
                    })
                    .create();
            alertDialog.show();
        });
    }

    public ArrayList<Map<String, Object>> getRankingList(){
        // 获取所有数据并初始化排行榜列表
        rankingList = new ArrayList<>();
        List<Player> players = playerDao.getAllData();
        int index = 0;
        for (Player player : players) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", ++index);
            map.put("name", player.getName());
            map.put("score", player.getScore());
            map.put("time", player.getTime());
            rankingList.add(map);
        }
        return rankingList;
    }

}