package com.hades.adminpolyfit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hades.adminpolyfit.Model.Meals;
import com.hades.adminpolyfit.R;

import java.util.List;

/**
 * Created by Hades on 23,October,2019
 **/
public class SpinnerMealsAdapter extends BaseAdapter {
    private List<Meals> listMeals;
    private Context context;

    public SpinnerMealsAdapter(List<Meals> listMeals, Context context) {
        this.listMeals = listMeals;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listMeals.size();
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
        LayoutInflater inf = ((Activity) context).getLayoutInflater();
        view = inf.inflate(R.layout.one_item_spinner_meals, null);
        TextView titleMeals = view.findViewById(R.id.titleMealsSPN);
        titleMeals.setText(listMeals.get(i).getTitle());
        return view;
    }
}