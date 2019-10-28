package com.hades.adminpolyfit.Adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.hades.adminpolyfit.Fragments.AddExercisesFragment;
import com.hades.adminpolyfit.Fragments.ViewDishFragment;
import com.hades.adminpolyfit.Fragments.ViewExerciseFragment;
import com.hades.adminpolyfit.Model.Dish;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Hades on 21,October,2019
 **/
public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {

    List<Dish> dishList;
    Context context;

    public DishAdapter(List<Dish> dishList, Context context) {
        this.dishList = dishList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.one_item_view_dish, parent, false);

        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       holder.title.setText(dishList.get(position).getTitle());
       Picasso.get().load(dishList.get(position).getImageUrl()).placeholder(R.drawable.loading).into(holder.imvDish);
       holder.imvDish.setClipToOutline(true);
       holder.tvCaloriesDish.setText(String.valueOf(dishList.get(position).getCalories()));

       holder.setItemClickListener(new ItemClickListener() {
           @Override
           public void onClick(View view, int posittion) {
               ViewDishFragment viewDishFragment = (ViewDishFragment) ViewDishFragment.newInstance();
               viewDishFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "tag");
               Dish dish = new Dish();
               dish.setId(dishList.get(position).getId());
               dish.setTitle(dishList.get(position).getTitle());
               dish.setImageUrl(dishList.get(position).getImageUrl());
               dish.setProtein(dishList.get(position).getProtein());
               dish.setFat(dishList.get(position).getFat());
               dish.setCarb(dishList.get(position).getCarb());
               dish.setCalories(dishList.get(position).getCalories());
               dish.setIdMeals(dishList.get(position).getIdMeals());
               /*dish.setId_ingredients(dishList.get(position).getId_ingredients());*/
               Bundle bundle = new Bundle();
               bundle.putSerializable("dish", dish);
               viewDishFragment.setArguments(bundle);
           }
       });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title,tvCaloriesDish;
        GifImageView imvDish;
        private ItemClickListener itemClickListener;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.titleDish);
            tvCaloriesDish=itemView.findViewById(R.id.tvCaloriesDish);
            imvDish = itemView.findViewById(R.id.imageDish);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());

        }
    }


}