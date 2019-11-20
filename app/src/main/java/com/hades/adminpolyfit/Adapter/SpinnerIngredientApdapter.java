package com.hades.adminpolyfit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hades.adminpolyfit.Model.Bodyparts;
import com.hades.adminpolyfit.Model.Ingredients;
import com.hades.adminpolyfit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hades on 29,October,2019
 **/
public class SpinnerIngredientApdapter extends BaseAdapter {
    private List<Ingredients> ingredientsList;
    private Context context;

    public SpinnerIngredientApdapter(List<Ingredients> ingredientsList, Context context) {
        this.ingredientsList = ingredientsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ingredientsList.size();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inf = ((Activity) context).getLayoutInflater();
        view = inf.inflate(R.layout.one_item_spinner_ingredient, null);
        final CheckBox selectIngredient = view.findViewById(R.id.selectIngredient);
        TextView titleIngredient = view.findViewById(R.id.titleIngredientSpn);
        titleIngredient.setText(ingredientsList.get(i).getTitle());
        selectIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectIngredient.isChecked()) {
                    Log.e("PhayTran", "Check on ::: " + ingredientsList.get(i).getTitle());
                    ingredientsList.get(i).setChecked(true);
                } else {
                    Log.e("PhayTran", "uncheck" + ingredientsList.get(i).getTitle());
                    ingredientsList.get(i).setChecked(false);
                }
            }
        });
        if (ingredientsList.get(i).isChecked()) {
            selectIngredient.setChecked(true);
        }
        return view;
    }
    public ArrayList<Ingredients> getSelectIngredientList() {
        ArrayList<Ingredients> list = new ArrayList<>();
        for (int i = 0; i < ingredientsList.size(); i++) {
            if (ingredientsList.get(i).isChecked())
                list.add(ingredientsList.get(i));
        }
        return list;
    }

}
