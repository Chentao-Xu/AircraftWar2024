package com.example.aircraftwar2024.playerDAO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Player implements Serializable {
    private String name;
    private int score;
    private LocalDateTime time;

    public Player(String name, int score, LocalDateTime time) {
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

    public LocalDateTime getTime(){
        return time;
    }

}
