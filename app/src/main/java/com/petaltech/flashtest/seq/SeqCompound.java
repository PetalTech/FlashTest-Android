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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

@JsonAdapter(SeqCompound.SeqCompoundSerializer.class)
public final class SeqCompound
implements SeqStep{
    private final List<SeqStep> steps;

    public SeqCompound(SeqStep... steps){
        this.steps = Arrays.asList(steps);
    }

    public List<SeqStep> getSteps(){
        return Collections.unmodifiableList(this.steps);
    }

    public List<SeqStep> getStepsFor(SeqBox box){
        List<SeqStep> steps = new LinkedList<>();
        for(SeqStep step : this.steps){
            if(step.applies(box)){
                steps.add(step);
            }
        }
        return steps;
    }

    @Override
    public Epoch getEpoch(){
        List<Epoch> epochList = new LinkedList<>();
        for(SeqStep step : this.steps) epochList.add(step.getEpoch());
        return epochList.get(epochList.size() - 1);
    }

    @Override
    public long getLength(){
        return this.getEpoch().asMillis();
    }

    @Override
    public void invoke(RefreshCallback cb,  AtomicIntegerArray colors, AtomicReferenceArray<Boolean> visibility){
        for(SeqStep step : this.steps) step.invoke(cb, colors, visibility);
    }

    @Override
    public boolean applies(SeqBox box){
        for(SeqStep step : this.steps){
            if(!step.applies(box)) return false;
        }
        return true;
    }

    public static final class SeqCompoundSerializer
    implements JsonSerializer<SeqCompound>, JsonDeserializer<SeqCompound>{
        @Override
        public SeqCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<SeqStep> steps = new LinkedList<>();
            JsonArray array = json.getAsJsonArray();
            for(int i = 0; i < array.size(); i++){
                JsonObject obj = array.get(i).getAsJsonObject();

                Type type;
                try{
                    type = Class.forName(obj.get("class").getAsString());
                } catch(ClassNotFoundException e){
                    e.printStackTrace(System.err);
                    throw new JsonParseException("Cannot find step class", e);
                }
                steps.add((SeqStep) context.deserialize(obj.get("data").getAsJsonObject(), type));
            }
            return new SeqCompound(steps.toArray(new SeqStep[0]));
        }

        @Override
        public JsonElement serialize(SeqCompound src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray();
            for(SeqStep step : src.steps){
                JsonElement data = context.serialize(step, step.getClass());

                JsonObject obj = new JsonObject();
                obj.addProperty("class", step.getClass().getName());
                obj.add("data", data);

                array.add(obj);
            }
            return array;
        }
    }
}