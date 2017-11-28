package com.petaltech.flashtest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.petaltech.flashtest.seq.Sequence;

import java.util.LinkedList;
import java.util.List;

public final class FlashTest{
    public static final Gson GSON = new GsonBuilder()
            .create();

    public static final List<Sequence> SEQUENCES = new LinkedList<>();
}