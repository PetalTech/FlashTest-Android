package com.petaltech.flashtest.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.petaltech.flashtest.R;
import com.petaltech.flashtest.seq.SeqStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class CreateActivity
extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_create);

        ExpandableListView listView = (ExpandableListView) this.findViewById(R.id.lv_seq);

        List<String> headers = Arrays.asList(
                "Top Box",
                "Middle Box",
                "Bottom Box"
        );

        Map<String, List<SeqStep>> children = new HashMap<>();
        for(String header : headers){
            children.put(header, new LinkedList<SeqStep>());
        }

        listView.setAdapter(new ExpandableListAdapter(this, headers, children));
    }

    private static final class ExpandableListAdapter
    extends BaseExpandableListAdapter{
        private final Context context;
        private final List<String> headers;
        private final Map<String, List<SeqStep>> children;

        public ExpandableListAdapter(Context ctx, List<String> headers, Map<String, List<SeqStep>> children){
            this.context = ctx;
            this.headers = headers;
            this.children = children;
        }

        @Override
        public int getGroupCount() {
            return this.headers.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.children.get(this.headers.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.headers.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.children.get(this.headers.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
            String header = (String) this.getGroup(groupPosition);
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view, null);
            }

            TextView listHeader = (TextView) convertView.findViewById(R.id.lbl_list_header);
            listHeader.setTypeface(null, Typeface.BOLD);
            listHeader.setText(header);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            SeqStep child = (SeqStep) getChild(groupPosition, childPosition);
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);
            }

            TextView listChild = (TextView) convertView.findViewById(R.id.lbl_list_item);
            child.bind(listChild);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}