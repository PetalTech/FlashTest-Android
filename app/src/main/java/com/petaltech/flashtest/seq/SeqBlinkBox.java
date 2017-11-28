package com.petaltech.flashtest.seq;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

@JsonAdapter(SeqBlinkBox.SeqBlinkBoxSerializer.class)
public final class SeqBlinkBox
implements SeqStep{
    private final SeqBox box;
    private final Epoch epoch;
    private final Frequency frequency;
    private final int[] colors;

    public SeqBlinkBox(SeqBox box, Epoch epoch, Frequency freq, int[] colors){
        this.box = box;
        this.epoch = epoch;
        this.frequency = freq;
        this.colors = colors;
    }

    public int[] getColors(){
        return this.colors;
    }

    @Override
    public long getLength() {
        return this.epoch.getLength();
    }

    public long getFrequency(){
        return this.frequency.asLong();
    }

    @Override
    public void invoke(final RefreshCallback cb, ScheduledExecutorService exec, final AtomicIntegerArray colors, AtomicReferenceArray<Boolean> visibility){
        exec.scheduleAtFixedRate(
                new Runnable(){
                    private final long start = System.currentTimeMillis();
                    private final AtomicInteger ptr = new AtomicInteger(0);

                    @Override
                    public void run(){
                        do{
                            long delta = System.currentTimeMillis() - this.start;
                            if(delta >= getLength()){
                                return;
                            }

                            int idx = this.ptr.getAndIncrement();
                            if(idx >= getColors().length){
                                this.ptr.set(0);
                            }

                            System.out.println(getBox().name() + " := " + getColors()[idx]);
                            colors.set(getBox().ordinal(), getColors()[idx]);
                            cb.refresh();
                        } while(!Thread.interrupted());
                    }
                },
                0, this.getFrequency(),
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public boolean applies(SeqBox box) {
        return this.box.equals(box);
    }

    @Override
    public Epoch getEpoch() {
        return this.epoch;
    }

    public SeqBox getBox() {
        return this.box;
    }

    public static final class SeqBlinkBoxSerializer
    implements JsonSerializer<SeqStep>, JsonDeserializer<SeqStep>{
        @Override
        public SeqStep deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
            JsonObject obj = json.getAsJsonObject();
            SeqBox box = SeqBox.valueOf(obj.get("box").getAsString().toUpperCase());
            Epoch epoch = Epoch.of(obj.get("epoch").getAsString());
            Frequency freq = Frequency.of(obj.get("frequency").getAsString());
            JsonArray jsonColors = obj.get("colors").getAsJsonArray();
            int[] colors = new int[jsonColors.size()];
            for(int i = 0; i < jsonColors.size(); i++) {
                try{
                    int rgb = Integer.parseInt(jsonColors.get(i).getAsString(), 16);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    int alpha = 0xFF;
                    colors[i] = (alpha << 24 | red << 16 | green << 8 | blue);
                } catch(NumberFormatException e){
                    e.printStackTrace(System.err);
                }
            }
            return new SeqBlinkBox(box, epoch, freq, colors);
        }

        @Override
        public JsonElement serialize(SeqStep src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            SeqBlinkBox blinkStep = (SeqBlinkBox) src;

            obj.addProperty("box", blinkStep.box.name());
            obj.addProperty("epoch", blinkStep.epoch.toString());
            obj.addProperty("frequency", blinkStep.frequency.toString());

            JsonArray array = new JsonArray();
            for(int i = 0; i < blinkStep.colors.length; i++){
                array.add(blinkStep.colors[i] << 24);
            }

            obj.add("colors", array);
            return obj;
        }
    }
}