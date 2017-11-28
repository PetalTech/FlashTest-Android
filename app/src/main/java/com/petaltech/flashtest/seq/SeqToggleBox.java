package com.petaltech.flashtest.seq;

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
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

@JsonAdapter(SeqToggleBox.SeqToggleBoxSerializer.class)
public final class SeqToggleBox
implements SeqStep{
    private final SeqBox box;
    private final Epoch epoch;
    private final boolean visible;

    public SeqToggleBox(SeqBox box, Epoch epoch, boolean visible){
        this.box = box;
        this.epoch = epoch;
        this.visible = visible;
    }

    public boolean isVisible(){
        return this.visible;
    }

    public SeqBox getBox() {
        return this.box;
    }

    @Override
    public Epoch getEpoch() {
        return this.epoch;
    }

    @Override
    public long getLength(){
        return getEpoch().getLength();
    }

    @Override
    public void invoke(final RefreshCallback cb, ScheduledExecutorService exec, AtomicIntegerArray colors, final AtomicReferenceArray<Boolean> visibility) {
        exec.submit(new Runnable(){
            @Override
            public void run(){
                try{
                    visibility.set(getBox().ordinal(), isVisible());
                    cb.refresh();
                    Thread.sleep(getLength());
                    visibility.set(getBox().ordinal(), !isVisible());
                    cb.refresh();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
    }

    @Override
    public boolean applies(SeqBox box) {
        return box.equals(this.box);
    }

    public static final class SeqToggleBoxSerializer
    implements JsonSerializer<SeqToggleBox>, JsonDeserializer<SeqToggleBox>{
        @Override
        public SeqToggleBox deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
            JsonObject obj = json.getAsJsonObject();
            SeqBox box = SeqBox.valueOf(obj.get("box").getAsString().toUpperCase());
            Epoch epoch = Epoch.of(obj.get("epoch").getAsString());
            return new SeqToggleBox(box, epoch, obj.get("visible").getAsBoolean());
        }

        @Override
        public JsonElement serialize(SeqToggleBox src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("box", src.box.name());
            obj.addProperty("epoch", src.epoch.toString());
            obj.addProperty("visible", src.visible);
            return obj;
        }
    }
}