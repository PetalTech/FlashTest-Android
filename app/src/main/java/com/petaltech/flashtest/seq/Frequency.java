package com.petaltech.flashtest.seq;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public final class Frequency
implements Comparable<Frequency>{
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

    public long asLong(){
        return (long) this.num * this.scale.value;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%f%s", this.num, this.scale);
    }

    private static final Pattern VALUE_PATTERN = Pattern.compile("[+-]?([0-9]+[.])?[0-9]+");

    public static Frequency of(String value){
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
        if(scale == null) throw new NullPointerException(String.format(Locale.ENGLISH, "Unknown scale '%s'", scaleStr));

        double val = 0.0;
        try{
            val = Double.valueOf(valStr);
        } catch(NumberFormatException e){
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Cannot parse value: '%f'", val));
        }

        return new Frequency(val, scale);
    }

    @Override
    public int compareTo(@Nonnull Frequency o) {
        long l1 = this.asLong();
        long l2 = o.asLong();
        if(l1 < l2){
            return -1;
        } else if(l1 > l2){
            return 1;
        }
        return 0;
    }

    public enum Scale{
        HERTZ(1),
        KILOHERTZ(1000),
        MEGAHERTZ(1000000),
        GIGAHERTZ(1000000000);

        private final long value;

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
}