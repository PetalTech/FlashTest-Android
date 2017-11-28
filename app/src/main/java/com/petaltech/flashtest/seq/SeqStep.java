package com.petaltech.flashtest.seq;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public interface SeqStep{
    Epoch getEpoch();
    long getLength();
    void invoke(RefreshCallback refresh, ScheduledExecutorService exec, AtomicIntegerArray colors, AtomicReferenceArray<Boolean> visibility);
    boolean applies(SeqBox box);
}