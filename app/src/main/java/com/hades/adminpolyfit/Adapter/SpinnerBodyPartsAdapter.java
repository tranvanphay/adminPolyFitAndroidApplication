package com.hades.adminpolyfit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hades.adminpolyfit.Model.Bodyparts;
import com.hades.adminpolyfit.R;

import java.util.List;

/**
 * Created by Hades on 25,October,2019
 **/
public class SpinnerBodyPartsAdapter extends BaseAdapter {
    private List<Bodyparts> bodypartsList;
    private Context context;
    public SpinnerBodyPartsAdapter(List<Bodyparts> bodypartsList,Context context){
        this.bodypartsList=bodypartsList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return bodypartsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inf=((Activity)context).getLayoutInflater();
        view=inf.inflate(R.layout.one_item_spinner_bodyparts,null);
        TextView titleBodyParts=view.findViewById(R.id.titleBodyparts);
        /*ImageView imvLevel=view.findViewById(R.id.imvLevel);*/
        titleBodyParts.setText(bodypartsList.get(i).getTitle());
/*
        Picasso.get().load(listLevel.get(i).getImage()).into(imvLevel);
*/
        return view;
    }
}

