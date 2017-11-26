package com.petaltech.flashtest.ui;

import android.app.Activity;
import android.os.Bundle;

import com.petaltech.flashtest.data.SequenceModel;
import com.petaltech.flashtest.ui.views.SequenceModelView;

public final class RunActivity
extends Activity{
    private SequenceModelView modelView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(this.modelView = new SequenceModelView(this));
        SequenceModel model = (SequenceModel) this.getIntent().getSerializableExtra("model");
        this.setSequenceModel(model);
    }

    public void setSequenceModel(SequenceModel model){
        if(this.modelView != null){
            this.modelView.setModel(model);
            this.modelView.invalidate();
        }
    }
}