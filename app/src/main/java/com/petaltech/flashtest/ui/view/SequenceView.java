package com.petaltech.flashtest.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.petaltech.flashtest.seq.RefreshCallback;
import com.petaltech.flashtest.seq.SeqBox;
import com.petaltech.flashtest.seq.SeqBoxSize;
import com.petaltech.flashtest.seq.SeqStep;
import com.petaltech.flashtest.seq.Sequence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class SequenceView
extends LinearLayout
implements RefreshCallback{
    private static final SeqBoxSize DEFAULT_SIZE = new SeqBoxSize(0, 0);
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(6);

    private final AtomicReference<Sequence> seqRef = new AtomicReference<>();
    private final Rect[] boxes = new Rect[SeqBox.values().length];
    private final AtomicIntegerArray colors = new AtomicIntegerArray(SeqBox.values().length);
    private final AtomicReferenceArray<Boolean> visibility = new AtomicReferenceArray<>(SeqBox.values().length);
    private final Paint paint = new Paint();

    public SequenceView(Context context) {
        super(context);
        this.init();
    }

    public SequenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SequenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init(){
        this.setWillNotDraw(false);
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        this.setBackgroundColor(Color.WHITE);
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(Color.BLACK);

        for(int i = 0; i < this.boxes.length; i++) this.boxes[i] = new Rect(0, 0, DEFAULT_SIZE.getWidth(), DEFAULT_SIZE.getHeight());
        for(int i = 0; i < this.colors.length(); i++) this.colors.set(i, Color.WHITE);
        for(int i = 0; i < this.visibility.length(); i++) this.visibility.set(i, true);
    }

    public void setSequence(Sequence seq){
        if(this.seqRef.get() != null) throw new IllegalStateException("Sequence already set");
        this.seqRef.set(seq);
        SeqBoxSize[] sizes = seq.getSizes();
        for(int i = 0; i < sizes.length; i++){
            this.boxes[i] = new Rect(0, 0, sizes[i].getWidth(), sizes[i].getHeight());
        }

        int[] colors = seq.getColors();
        for(int i = 0; i < SeqBox.values().length; i++){
            this.colors.set(i, colors[i]);
        }

        SeqStep[][] steps = seq.getSteps();
        SeqBox[] boxes = SeqBox.values();
        for(int i = 0; i < boxes.length; i++){
            EXECUTOR.submit(new SequenceTicker(boxes[i], this, steps[i]));
        }
    }

    public Sequence getSequence(){
        if(this.seqRef.get() == null) throw new IllegalStateException("Sequence == null");
        return this.seqRef.get();
    }

    @Override
    protected void onDraw(Canvas c){
        int yOff = 100;
        for(int i = 0; i < SeqBox.values().length; i++){
            if(!this.visibility.get(i)) continue;
            Rect rect = this.boxes[i];
            int color = this.colors.get(i);

            rect.left = (c.getWidth() - rect.width() + 100) / 2;

            this.paint.setColor(color);
            c.translate(0, yOff);
            c.drawRect(rect, this.paint);
            yOff = (rect.height() + 100);
        }
    }

    @Override
    public void refresh() {
        this.postInvalidate();
    }

    private static final class SequenceTicker
    implements Runnable{
        private final AtomicInteger counter = new AtomicInteger(0);
        private final SequenceView seqView;
        private final SeqStep[] steps;
        private final SeqBox box;

        SequenceTicker(SeqBox box, SequenceView seqView, SeqStep[] steps){
            this.seqView = seqView;
            this.steps = steps;
            this.box = box;
        }

        @Override
        public void run(){
            try{
                while(!Thread.interrupted()){
                    int idx = this.counter.getAndIncrement();
                    if(idx >= this.steps.length){
                        this.counter.set(idx = 0);
                    }

                    SeqStep step = this.steps[idx];
                    System.out.println(this.box.name() + " - " + step.getClass().getSimpleName());
                    step.invoke(this.seqView, EXECUTOR, this.seqView.colors, this.seqView.visibility);

                    this.seqView.postInvalidate();
                    Thread.sleep(step.getLength());
                }
            } catch(Exception e){
                e.printStackTrace(System.err);
            }
        }
    }
}