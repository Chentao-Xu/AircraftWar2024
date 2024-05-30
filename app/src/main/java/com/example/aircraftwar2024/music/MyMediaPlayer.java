package com.example.aircraftwar2024.music;

import android.content.Context;
import android.media.MediaPlayer;

public class MyMediaPlayer {
    private MediaPlayer mediaPlayer;
    private Context context;
    private int resId;

    public MyMediaPlayer(Context context, int resId) {
        this.context = context;
        this.resId = resId;
        mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.setLooping(true); // Set looping
    }

    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(context, resId);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
