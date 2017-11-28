package com.petaltech.flashtest.seq;

import android.graphics.Rect;

import java.util.Locale;

public final class SeqBoxSize{
    private final int width;
    private final int height;

    public SeqBoxSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Rect asRect(){
        return new Rect(0, 0, this.width, this.height);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%dx%d", this.width, this.height);
    }

    public static SeqBoxSize of(String valueStr){
        String[] parts = valueStr.split("x");
        return new SeqBoxSize(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
    }
}