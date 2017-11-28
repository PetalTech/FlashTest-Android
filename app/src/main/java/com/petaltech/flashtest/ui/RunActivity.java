package com.petaltech.flashtest.ui;

import android.app.Activity;
import android.os.Bundle;

import com.petaltech.flashtest.FlashTest;
import com.petaltech.flashtest.seq.Sequence;
import com.petaltech.flashtest.ui.view.SequenceView;

public final class RunActivity
extends Activity{
    private SequenceView seqView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.seqView = new SequenceView(this));
        String name = this.getIntent().getStringExtra("seq");
        for(Sequence seq : FlashTest.SEQUENCES){
            if(seq.getName().equals(name)){
                this.seqView.setSequence(seq);
                break;
            }
        }
    }
}
