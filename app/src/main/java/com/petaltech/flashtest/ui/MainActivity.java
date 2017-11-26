package com.petaltech.flashtest.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.petaltech.flashtest.data.SequenceModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class MainActivity
extends Activity
implements AdapterView.OnItemClickListener{
    private static final Gson gson = new Gson();

    private SequenceModel[] models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.loadModels();

        String[] items;
        int i = 0;
        if(this.models != null){
            items = new String[this.models.length + 1];
            for (i = 0; i < this.models.length; i++){
                items[i] = this.models[i].getName();
            }
        } else{
            this.models = new SequenceModel[1];
            items = new String[2];
            items[i] = (this.models[i++] = new SequenceModel("Default", Color.CYAN, Color.RED, Color.GREEN)).getName();
        }
        items[i] = "Create Test Sequence";

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        this.setContentView(listView);
    }

    private void loadModels(){
        InputStream in = null;
        try{
            in = this.openFileInput("/sequences.json");
            this.models = gson.fromJson(new InputStreamReader(in), SequenceModel[].class);
        } catch(Exception e){
            //Ignore
        } finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(this.models == null) return;
        if(position > this.models.length){
            //TODO: Write Add sequence
            return;
        }
        SequenceModel model = this.models[position];
        Intent i = new Intent(this, RunActivity.class);
        i.putExtra("model", model);
        this.startActivity(i);
    }
}