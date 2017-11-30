package com.petaltech.flashtest.seq;

import android.widget.TextView;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public interface SeqStep{
    Epoch getEpoch();
    long getLength();
    void invoke(RefreshCallback refresh, AtomicIntegerArray colors, AtomicReferenceArray<Boolean> visibility);
    void bind(TextView view);
    String getTextView();
    boolean applies(SeqBox box);
}