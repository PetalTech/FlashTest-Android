package com.petaltech.flashtest.data;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public final class Frequency
implements Comparable<Frequency>{
    public static final Frequency ONE_HERTZ = new Frequency(1, Scale.HERTZ);
    public static final Frequency ONE_KILOHERTZ = new Frequency(1, Scale.KILOHERTZ);
    public static final Frequency ONE_MEGAHERTZ = new Frequency(1, Scale.MEGAHERTZ);
    public static final Frequency ONE_GIGAHERTZ = new Frequency(1, Scale.GIGAHERTZ);

    private final double num;
    private final Scale scale;

    public Frequency(double num, Scale scale){
        this.num = num;
        this.scale = scale;
    }

    public double getNumber(){
        return this.num;
    }

    public Scale getUnits(){
        return this.scale;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%f%s", this.num, this.scale.toString());
    }

    private double getAsHertz(){
        switch(this.scale){
            case HERTZ: return this.num;
            case KILOHERTZ:
            case MEGAHERTZ:
            case GIGAHERTZ:
                return this.num / this.scale.value;
            default: return -0.1;
        }
    }

    @Override
    public int compareTo(@Nonnull Frequency o) {
        return Double.compare(this.getAsHertz(), o.getAsHertz());
    }

    public enum Scale{
        HERTZ(1),
        KILOHERTZ(1000),
        MEGAHERTZ(1000000),
        GIGAHERTZ(1000000000);

        private long value;

        Scale(long value){
            this.value = value;
        }

        @Override
        public String toString() {
            switch(this){
                case HERTZ: return "Hz";
                case KILOHERTZ: return "kHz";
                case MEGAHERTZ: return "MHz";
                case GIGAHERTZ: return "GHz";
                default: return "?";
            }
        }
    }

    private static final Pattern VALUE_PATTERN = Pattern.compile("[+-]?([0-9]+[.])?[0-9]+");

    public static Frequency parse(String value){
        Matcher m = VALUE_PATTERN.matcher(value);
        if(!m.find()) return null;
        String valStr = m.group();
        String scaleStr = value.substring(valStr.length());
        Scale scale = null;
        for(Scale s : Scale.values()){
            if(scaleStr.equalsIgnoreCase(s.toString())){
                scale = s;
                break;
            }
        }
        double val;
        try{
            val = Double.valueOf(valStr);
        } catch(NumberFormatException e){
            val = 0.0;
        }
        return new Frequency(val, scale);
    }
}