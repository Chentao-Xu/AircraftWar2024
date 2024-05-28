package com.example.aircraftwar2024.playerDAO;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

public class PlayerDaoImpl implements PlayerDao{

    private List<Player> playerList;
    private Context context;
    private File file;

    public PlayerDaoImpl(Context context, File file) throws IOException, ClassNotFoundException {
        this.context = context;
        this.file = file;
        ObjectInputStream ois = new ObjectInputStream(context.openFileInput(file.getName()));
        this.playerList = (List<Player>) ois.readObject();
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
        ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(file.getName(), Context.MODE_PRIVATE));
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
