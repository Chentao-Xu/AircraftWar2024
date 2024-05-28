package com.example.aircraftwar2024.playerDAO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface PlayerDao {

    void sortByScore() throws IOException;

    void addData(Player player) throws IOException;

    void deleteData(int index) throws IOException;

    List<Player> getAllData();

    void writeToFile() throws IOException;

}
