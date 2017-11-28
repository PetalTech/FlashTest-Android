package com.petaltech.flashtest.seq;

public enum SeqBox{
    TOP(0.25),
    MIDDLE(0.75F),
    BOTTOM(0.75F);

    private final double scale;

    SeqBox(double scale){
        this.scale = scale;
    }

    public float getScale(){
        return (float) this.scale;
    }
}