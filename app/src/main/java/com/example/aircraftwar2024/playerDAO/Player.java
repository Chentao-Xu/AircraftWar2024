package com.example.aircraftwar2024.playerDAO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Player implements Serializable {
    private String name;
    private int score;
    private String time;

    public Player(String name, int score, String time) {
        this.name = name;
        this.score = score;
        this.time = time;
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }

    public String getTime(){
        return time;
    }

}
