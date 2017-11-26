package com.petaltech.flashtest.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.petaltech.flashtest.data.Frequency;
import com.petaltech.flashtest.data.SequenceModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class SequenceModelView
extends LinearLayout
implements Runnable{
    private static final Frequency MAX_FREQ = Frequency.parse("1.0kHz");
    private static final Frequency MIN_FREQ = Frequency.parse("0.0Hz");

    private static final int WIDTH = 920;
    private static final int HEIGHT = 350;
    private static final Rect DIMENSION = new Rect(0, 0, WIDTH, HEIGHT);

    private final Paint paint;
    private final AtomicInteger color = new AtomicInteger(0);
    private final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
    private final AtomicReference<SequenceModel> model = new AtomicReference<>();
    private ScheduledFuture<?> tickerTask;

    public SequenceModelView(Context ctx){
        super(ctx);
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        this.setBackgroundColor(Color.WHITE);
        this.paint = new Paint();
    }

    public SequenceModelView(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        this.setBackgroundColor(Color.WHITE);
        this.paint = new Paint();
    }

    public SequenceModelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        this.setBackgroundColor(Color.WHITE);
        this.paint = null;
    }

    public SequenceModel getModel(){
        return this.model.get();
    }

    public void setModel(SequenceModel model){
        this.model.set(model);
        this.paint.setColor(model.getColors()[0]);
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);
        this.exec.scheduleAtFixedRate(this, 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDraw(Canvas c){
        int x = (c.getWidth() - WIDTH) / 2;
        int y = (c.getHeight() - HEIGHT) / 2;

        c.translate(x, y);
        c.drawRect(DIMENSION, this.paint);
    }

    @Override
    public void run(){
        if(this.model.get() == null) return;
        int i = this.color.incrementAndGet();
        if(i >= this.model.get().getColors().length){
            this.color.set(0);
            i = 0;
        }
        this.paint.setColor(this.model.get().getColors()[i]);
        this.postInvalidate();
    }
}