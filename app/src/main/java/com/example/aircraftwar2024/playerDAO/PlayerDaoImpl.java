package com.example.aircraftwar2024.playerDAO;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerDaoImpl implements PlayerDao{

    private List<Player> playerList;
    private Context context;
    private String file;

    public PlayerDaoImpl(Context context, String file) throws IOException, ClassNotFoundException {
        this.context = context;
        this.file = file;

        // Check if the file exists
        File dataFile = new File(context.getFilesDir(), file);
        if (dataFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile));
            this.playerList = (List<Player>) ois.readObject();
        } else {
            this.playerList = new ArrayList<>();
            writeToFile();
        }
    }

    @Override
    public void sortByScore() throws IOException {
        // 按照分数排序
        Collections.sort(playerList, (a, b)->{return b.getScore() - a.getScore();});

        // 重写进文件
        writeToFile();
    }

    @Override
    public void writeToFile() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(file, Context.MODE_PRIVATE));
        oos.writeObject(playerList);
        oos.close();
    }

    @Override
    public void addData(Player player) throws IOException {
        playerList.add(player);
        sortByScore();
    }

    @Override
    public void deleteData(int index) throws IOException {
        playerList.remove(index);
        writeToFile();
    }

    @Override
    public List<Player> getAllData(){
        return playerList;
    }

}
