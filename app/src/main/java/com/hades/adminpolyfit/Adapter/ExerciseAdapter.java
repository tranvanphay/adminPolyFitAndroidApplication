package com.hades.adminpolyfit.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hades.adminpolyfit.Fragments.ViewExerciseFragment;
import com.hades.adminpolyfit.Model.Bodyparts;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Hades on 15,October,2019
 **/
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    List<Exercise> listExercise;
    Context context;
    AdminPolyfitServices adminPolyfitServices;
    String bodyPartTitle;
    List<Bodyparts> bodypartsList = new ArrayList<>();

    public ExerciseAdapter(List<Exercise> listExercise, Context context) {
        this.listExercise = listExercise;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.one_item_exercise, parent, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        return new ViewHolder(itemView);
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        /*getAllDetailExercise(listExercise.get(position).getId(),holder);*/
        holder.title.setText(listExercise.get(position).getTitle());
//        Picasso.get().load(listExercise.get(position).getImage_url()).placeholder(R.drawable.loading).into(holder.imageExercise);
        Glide
                .with(context)
                .load(listExercise.get(position).getImage_url())
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(holder.imageExercise);
        holder.imageExercise.setClipToOutline(true);
        holder.bodyParts.setText(bodyPartTitle);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int posittion) {
                ViewExerciseFragment viewExerciseFragment = (ViewExerciseFragment) ViewExerciseFragment.newInstance();
                viewExerciseFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "tag");
                Exercise exercise = new Exercise();
                exercise.setId(listExercise.get(position).getId());
                exercise.setTitle(listExercise.get(position).getTitle());
                exercise.setIntroduction(listExercise.get(position).getIntroduction());
                exercise.setContent(listExercise.get(position).getContent());
                exercise.setTips(listExercise.get(position).getTips());
                exercise.setRest(listExercise.get(position).getRest());
                exercise.setReps(listExercise.get(position).getReps());
                exercise.setSets(listExercise.get(position).getSets());
                exercise.setImage_url(listExercise.get(position).getImage_url());
                exercise.setVideo_url(listExercise.get(position).getVideo_url());
                Bundle bundle = new Bundle();
                bundle.putSerializable("exercise", exercise);
                viewExerciseFragment.setArguments(bundle);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listExercise.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, bodyParts;
        GifImageView imageExercise;
        private ItemClickListener itemClickListener;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.titleExercise);
            bodyParts = itemView.findViewById(R.id.bodyParts);
            imageExercise = itemView.findViewById(R.id.imageExercise);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());

        }
    }

    /*private void getAllDetailExercise(Integer id, final ViewHolder viewHolder) {
        adminPolyfitServices.getAllDetailExercise(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Log.e("PhayTran", response.body());
                    JSONArray array = null;
                    try {
                        JSONObject obj = new JSONObject(response.body());
                        JSONObject bodyParts = obj.getJSONObject("data");
                        array = bodyParts.getJSONArray("bodyparts");
                    } catch (Throwable t) {
                        Log.e("PhayTV", "Error!!!");
                    }
                    Gson gson = new Gson();
                    String jsonOutput = array.toString();
                    Type listType = new TypeToken<List<Bodyparts>>() {
                    }.getType();
                    bodypartsList = gson.fromJson(jsonOutput, listType);
                    *//*Log.e("PhayTranLOGGER",bodypartsList.get(0).getIdBodyPart()+"");*//*
                    if(!bodypartsList.isEmpty()){
                       bodyPartTitle= bodypartsList.get(0).getTitle();
                    }
                    Log.e("Phaytv", *//*exercisesList.get(0).getId() +*//*":: ListBodyParts ::" + array);
                    System.out.println("Print object :::"+bodypartsList);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

/*    @RequiresApi(api = Build.VERSION_CODES.N)
    private String joinList(List<Bodyparts> bodypartsList){
        String joinedTitle = bodypartsList.stream()
                .map(Bodyparts::getTitle)
                .collect(Collectors.joining(", "));
        Log.e("Joined",joinedTitle);
        return joinedTitle;
    }*/


}