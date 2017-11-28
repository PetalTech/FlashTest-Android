package com.petaltech.flashtest.seq;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public final class Epoch
implements Comparable<Epoch>{
    private final long length;
    private final TimeUnit scale;

    public Epoch(long length, TimeUnit scale){
        this.length = length;
        this.scale = scale;
    }

    public long getLength(){
        return this.length;
    }

    public TimeUnit getScale(){
        return this.scale;
    }

    public long asMillis(){
        return this.scale.toMillis(this.getLength());
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%d%s", this.getLength(), UNITS[this.scale.ordinal()]);
    }

    private static final Pattern VALUE_PATTERN = Pattern.compile("\\d+");
    private static final String[] UNITS = {
            "ns",
            "µs",
            "ms",
            "s",
            "m",
            "h",
            "d"
    };
    private static TimeUnit getTimeUnit(String value){
        for (int i = 0; i < UNITS.length; i++) {
            if(UNITS[i].equalsIgnoreCase(value)){
                return TimeUnit.values()[i];
            }
        }
        return TimeUnit.MILLISECONDS;
    }

    public static Epoch of(String value){
        Matcher m = VALUE_PATTERN.matcher(value);
        if(!m.find()) throw new IllegalStateException("Invalid value " + value);
        String valueStr = m.group();
        String unitStr = value.substring(valueStr.length());
        return new Epoch(Long.valueOf(valueStr), getTimeUnit(unitStr));
    }

    @Override
    public int compareTo(@Nonnull Epoch o){
        long l1 = this.asMillis();
        long l2 = o.asMillis();
        if(l1 > l2){
            return 1;
        } else if(l1 < l2){
            return -1;
        }
        return 0;
    }
}