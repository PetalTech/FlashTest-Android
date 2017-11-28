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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@JsonAdapter(Sequence.SeqSerializer.class)
public final class Sequence{
    private final String name;
    private final SeqStep[] steps;
    private final SeqBoxSize[] sizes;
    private final Integer[] colors;

    private Sequence(String name, List<SeqBoxSize> sizes, List<SeqStep> steps, List<Integer> colors){
        this.name = name;
        this.sizes = sizes.toArray(new SeqBoxSize[0]);
        this.steps = steps.toArray(new SeqStep[0]);
        this.colors = colors.toArray(new Integer[0]);
    }

    public String getName(){
        return this.name;
    }

    public SeqStep[][] getSteps(){
        List<List<SeqStep>> steps = new ArrayList<>();
        for(SeqBox box : SeqBox.values()){
            steps.add(new LinkedList<SeqStep>());
        }

        for(SeqStep step : this.steps){
            if(step instanceof SeqCompound){
                SeqCompound comp = (SeqCompound) step;
                for(SeqBox box : SeqBox.values()){
                    List<SeqStep> boxSteps = steps.get(box.ordinal());
                    boxSteps.addAll(comp.getStepsFor(box));
                }
            } else{
                for(SeqBox box : SeqBox.values()){
                    if(step.applies(box)){
                        steps.get(box.ordinal()).add(step);
                    }
                }
            }
        }

        SeqStep[][] rawSteps = new SeqStep[SeqBox.values().length][];
        for(int i = 0; i < SeqBox.values().length; i++){
            rawSteps[i] = steps.get(i).toArray(new SeqStep[0]);
        }
        return rawSteps;
    }

    public SeqBoxSize[] getSizes(){
        SeqBoxSize[] sizes = new SeqBoxSize[this.sizes.length];
        System.arraycopy(this.sizes, 0, sizes, 0, this.sizes.length);
        return sizes;
    }

    public int[] getColors(){
        int[] colors = new int[this.colors.length];
        for(int i = 0; i < this.colors.length; i++){
           colors[i] = this.colors[i];
        }
        return colors;
    }

    public static final class SeqSerializer
    implements JsonSerializer<Sequence>, JsonDeserializer<Sequence>{
        @Override
        public JsonElement serialize(Sequence src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", src.name);

            JsonArray steps = new JsonArray();
            for(SeqStep step : src.steps){
                JsonObject stepObj = new JsonObject();
                stepObj.addProperty("class", step.getClass().getName());
                stepObj.add("data", context.serialize(step, step.getClass()));

                steps.add(stepObj);
            }
            obj.add("steps", steps);

            JsonArray sizes = new JsonArray();
            for(SeqBoxSize size : src.sizes){
                sizes.add(size.toString());
            }
            obj.add("sizes", sizes);

            JsonArray colors = new JsonArray();
            for(Integer i : src.colors){
                colors.add(Integer.toString(i, 16));
            }
            obj.add("colors", colors);

            return obj;
        }

        @Override
        public Sequence deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            List<SeqStep> steps = new LinkedList<>();
            JsonArray array = obj.get("steps").getAsJsonArray();
            for(int i = 0; i < array.size(); i++){
                JsonObject stepObj = array.get(i).getAsJsonObject();

                Type type;
                try {
                    type = Class.forName(stepObj.get("class").getAsString());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace(System.err);
                    throw new JsonParseException("Unknown class '" + stepObj.get("class").getAsString() + "'");
                }

                steps.add((SeqStep) context.deserialize(stepObj.get("data"), type));
            }

            List<SeqBoxSize> sizes = new LinkedList<>();
            array = obj.get("sizes").getAsJsonArray();
            for(int i = 0; i < array.size(); i++){
               sizes.add(SeqBoxSize.of(array.get(i).getAsString()));
            }

            List<Integer> colors = new LinkedList<>();
            array = obj.get("colors").getAsJsonArray();
            for(int i = 0; i < array.size(); i++){
               int rgb = Integer.valueOf(array.get(i).getAsString(), 16);
               int red = (rgb >> 16) & 0xFF;
               int green = (rgb >> 8) & 0xFF;
               int blue = rgb & 0xFF;
               int alpha = 0xFF;
               colors.add(alpha << 24 | red << 16 | green << 8 | blue);
            }

            return new Sequence(obj.get("name").getAsString(), sizes, steps, colors);
        }
    }
}