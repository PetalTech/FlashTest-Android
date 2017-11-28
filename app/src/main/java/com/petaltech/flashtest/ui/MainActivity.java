package com.petaltech.flashtest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.petaltech.flashtest.FlashTest;
import com.petaltech.flashtest.R;
import com.petaltech.flashtest.seq.Sequence;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class MainActivity
extends Activity
implements AdapterView.OnItemClickListener{
    private Sequence[] sequences;

    private void closeQuietly(InputStream in){
        if(in != null){
            try{
                in.close();
            } catch(IOException e){ /* Ignore */}
        }
    }

    @SuppressWarnings("unchecked")
    private void loadModels(){
        List<Sequence> seqs = new LinkedList<>();

        InputStream in = null;
        try{
            in = getResources().openRawResource(R.raw.sequences);
            seqs.addAll((List<Sequence>) FlashTest.GSON.fromJson(new InputStreamReader(in), SEQUENCE_TYPETOKEN.getType()));
        } catch(Exception e){
            e.printStackTrace(System.err);
            // Ignore
        } finally{
            this.closeQuietly(in);
        }

        try{
            in = this.openFileInput("/sequences.json");
            seqs.addAll((List<Sequence>) FlashTest.GSON.fromJson(new InputStreamReader(in), SEQUENCE_TYPETOKEN.getType()));
        } catch(Exception e){
            e.printStackTrace(System.err);
            //Ignore
        } finally{
            this.closeQuietly(in);
        }

        this.sequences = seqs.toArray(new Sequence[0]);
        FlashTest.SEQUENCES.addAll(Arrays.asList(this.sequences));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.loadModels();

        String[] items;
        int i = 0;
        if(this.sequences != null){
            items = new String[this.sequences.length + 1];
            for(i = 0; i < this.sequences.length; i++){
                items[i] = this.sequences[i].getName();
            }
        } else{
            items = new String[1];
        }
        items[i] = "Create Sequence";

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        this.setContentView(listView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(this.sequences == null) return;
        if(position == this.sequences.length){
            return;
        }
        Sequence seq = this.sequences[position];
        Intent i = new Intent(this, RunActivity.class);
        i.putExtra("seq", seq.getName());
        this.startActivity(i);
    }

    private static final TypeToken<List<Sequence>> SEQUENCE_TYPETOKEN = new TypeToken<List<Sequence>>(){};
}
