package com.petaltech.flashtest.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SequenceModel
implements Serializable{
    private final String name;
    private final int[] colors;

    public SequenceModel(String name, int... colors) {
        this.name = name;
        this.colors = colors;
    }

    public String getName(){
        return this.name;
    }

    public int[] getColors(){
        int[] colors = new int[this.colors.length];
        System.arraycopy(this.colors, 0, colors, 0, this.colors.length);
        return colors;
    }

    public void writeObject(ObjectOutputStream oos) throws IOException{
        oos.writeInt(this.name.length());
        oos.writeBytes(this.name);
        oos.writeInt(this.colors.length);
        for(int color : this.colors) oos.writeInt(color);
    }

    public SequenceModel readObject(ObjectInputStream ois) throws IOException{
        int nameLen = ois.readInt();
        byte[] nameBytes = new byte[nameLen];
        ois.readFully(nameBytes, 0, nameLen);
        int colorsLen = ois.readInt();
        int[] colors = new int[colorsLen];
        for(int i = 0; i < colorsLen; i++){
            colors[i] = ois.readInt();
        }
        return new SequenceModel(new String(nameBytes, "utf-8"), colors);
    }
}