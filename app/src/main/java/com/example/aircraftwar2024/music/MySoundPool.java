package com.example.aircraftwar2024.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class MySoundPool {
    private SoundPool soundPool;
    private SparseIntArray soundMap;
    private Context context;

    public MySoundPool(Context context) {
        this.context = context;
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        soundMap = new SparseIntArray();
    }

    public void loadSound(int resId, int soundId) {
        int sound = soundPool.load(context, resId, 1);
        soundMap.put(soundId, sound);
    }

    public void playSound(int soundId) {
        int sound = soundMap.get(soundId);
        if (sound != 0) {
            soundPool.play(sound, 1, 1, 1, 0, 1);
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
