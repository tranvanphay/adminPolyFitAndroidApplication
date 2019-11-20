package com.hades.adminpolyfit.Fragments;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hades.adminpolyfit.Model.Dish;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.Model.Quotes;
import com.hades.adminpolyfit.Model.User;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Hades on 24,October,2019
 **/
public class StatisticalFragment extends DialogFragment implements View.OnClickListener {
    private AdminPolyfitServices adminPolyfitServices;
    private ImageView imvBackStatistical;
    List<Exercise> exercisesList = new ArrayList<>();
    List<User> lisAllUser = new ArrayList<>();
    List<User> listOnline = new ArrayList<>();
    List<Dish> listDish = new ArrayList<>();
    List<Quotes> quotesList = new ArrayList<>();
    TextView statisticalExercise, statisticalDishes;
    GifImageView exerciseAnim, dishAnim;
    ImageView imvPrevius, imvNext;
    private PieChart mChart;
    private BarChart mBarChart;
    LinearLayout pieChart, barChart;

    public static StatisticalFragment newInstance() {
        StatisticalFragment fragment = new StatisticalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.fragment_statistical, container, false);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        getAllExercise();
        getAllDish();
        getAllQuotes();
        getAllUser();
        getAllUserOnline();
        connectView(view);
        moveOffScreen();
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawHoleEnabled(true);
        mChart.setMaxAngle(180);
        mChart.setRotationAngle(180);
        mChart.setCenterTextOffset(0, -20);
        /*setData(100);*/
        mChart.animateY(1000, Easing.EaseInOutCubic);
        Legend legend = mChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextColor(Color.WHITE);
        legend.setDrawInside(false);
        legend.setYOffset(50f);
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);


        return view;
    }
    String[] barData = new String[]{"Exercises", "Dishes","Quotes"};
    private void setBarChart() {
        ArrayList<BarEntry> yVals = new ArrayList<>();
        float value = (float) (Math.random() * 100);
        yVals.add(new BarEntry(0, (int) exercisesList.size()));
        yVals.add(new BarEntry(1, (int) listDish.size()));
        yVals.add(new BarEntry(2, (int) quotesList.size()));
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Exercises");
        labels.add("Dishes");
        labels.add("Quotes");
        BarDataSet set = new BarDataSet(yVals, "Data Set");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(20f);

   /*     Legend legend = mBarChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextColor(Color.WHITE);
        legend.setDrawInside(false);
        legend.setTextSize(20f);
        legend.setYOffset(50f);*/
        mBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        mBarChart.getXAxis().setTextColor(Color.WHITE);
        BarData data = new BarData(set);
        mBarChart.setData(data);
        mBarChart.invalidate();
        mBarChart.animateY(500);


    }

    private void connectView(View view) {
        imvBackStatistical = view.findViewById(R.id.imvBackStatistical);
        imvBackStatistical.setOnClickListener(this);
        statisticalDishes = view.findViewById(R.id.staticalDishes);
        statisticalExercise = view.findViewById(R.id.staticalExercises);
        exerciseAnim = view.findViewById(R.id.loadingStatisticalExercise);
        dishAnim = view.findViewById(R.id.loadingStatisticalDish);
        mChart = view.findViewById(R.id.pieChart);
        mChart.setBackgroundColor(Color.BLACK);
        mBarChart = view.findViewById(R.id.barChart);
        mBarChart.getDescription().setEnabled(false);
        imvNext = view.findViewById(R.id.imvNext);
        imvNext.setOnClickListener(this);
        imvPrevius = view.findViewById(R.id.imvPrevius);
        imvPrevius.setOnClickListener(this);
        pieChart = view.findViewById(R.id.viewPieChart);
        barChart = view.findViewById(R.id.viewBarChart);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imvBackStatistical:
                this.dismiss();
                break;
            case R.id.imvPrevius:
                previus();
                break;
            case R.id.imvNext:
                next();
                break;
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void getAllExercise() {
        adminPolyfitServices.getAllExercise().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", response.body());
                    JSONArray array = null;
                    try {
                        JSONObject obj = new JSONObject(response.body());
                        array = obj.getJSONArray("Response");
                    } catch (Throwable t) {
                        Log.e("PhayTV", "Error!!!");
                    }
                    Gson gson = new Gson();
                    String jsonOutput = array.toString();
                    Type listType = new TypeToken<List<Exercise>>() {
                    }.getType();
                    exerciseAnim.setVisibility(View.GONE);
                    statisticalExercise.setVisibility(View.VISIBLE);
                    exercisesList = gson.fromJson(jsonOutput, listType);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                    statisticalExercise.setText(String.valueOf(exercisesList.size()));
                    setBarChart();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllDish() {
        adminPolyfitServices.getAllDish().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", response.body());
                    JSONArray array = null;
                    try {
                        JSONObject obj = new JSONObject(response.body());
                        array = obj.getJSONArray("Response");
                    } catch (Throwable t) {
                        Log.e("PhayTV", "Error!!!");
                    }
                    Gson gson = new Gson();
                    String jsonOutput = array.toString();
                    Type listType = new TypeToken<List<Dish>>() {
                    }.getType();
                    listDish = gson.fromJson(jsonOutput, listType);
                    dishAnim.setVisibility(View.GONE);
                    statisticalDishes.setVisibility(View.VISIBLE);
                    statisticalDishes.setText(String.valueOf(listDish.size()));
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                    setBarChart();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void moveOffScreen() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int offset = (int) (height * 0.5);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChart.getLayoutParams();
        params.setMargins(0, 0, 0, -offset);
        mChart.setLayoutParams(params);
    }

    String[] data = new String[]{"Is Online", "Is Offline"/*,"Quotes"*/};

    private void setData() {
        ArrayList<PieEntry> values = new ArrayList<>();
        /* for(int i=0;i<3;i++){
         *//*float val=(float)((Math.random()*range)+range/3);*//*
            float val=(float)((listDish.size())+range/3);
            values.add(new PieEntry(val,data[i]));
        }*/
        float val = (float) ((listOnline.size()));
        values.add(new PieEntry(val, data[0]));
        float val1 = (float) ((lisAllUser.size() - listOnline.size()));
        values.add(new PieEntry(val1, data[1]));
        /*float val2 = (float) ((quotesList.size()));
        values.add(new PieEntry(val2, data[2]));*/

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSelectionShift(5f);
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }

    public void getAllQuotes() {
        adminPolyfitServices.getAllQuotes().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", response.body());
                    JSONArray array = null;
                    try {
                        JSONObject obj = new JSONObject(response.body());
                        array = obj.getJSONArray("Response");
                    } catch (Throwable t) {
                        Log.e("PhayTV", "Error!!!");
                    }
                    Gson gson = new Gson();
                    String jsonOutput = array.toString();
                    Type listType = new TypeToken<List<Quotes>>() {
                    }.getType();
                    quotesList = gson.fromJson(jsonOutput, listType);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                    setBarChart();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllUser() {
        adminPolyfitServices.getAllUsers().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", response.body());
                    JSONArray array = null;
                    try {
                        JSONObject obj = new JSONObject(response.body());
                        array = obj.getJSONArray("Response");
                    } catch (Throwable t) {
                        Log.e("PhayTV", "Error!!!");
                    }
                    Gson gson = new Gson();
                    String jsonOutput = array.toString();
                    Type listType = new TypeToken<List<User>>() {
                    }.getType();
                    lisAllUser = gson.fromJson(jsonOutput, listType);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                    setData();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllUserOnline() {
        adminPolyfitServices.getOnlineUser().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", response.body());
                    JSONArray array = null;
                    try {
                        JSONObject obj = new JSONObject(response.body());
                        array = obj.getJSONArray("Response");
                    } catch (Throwable t) {
                        Log.e("PhayTV", "Error!!!");
                    }
                    Gson gson = new Gson();
                    String jsonOutput = array.toString();
                    Type listType = new TypeToken<List<User>>() {
                    }.getType();
                    listOnline = gson.fromJson(jsonOutput, listType);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                    setData();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void next() {
        pieChart.setVisibility(View.GONE);
        barChart.setVisibility(View.VISIBLE);
    }

    private void previus() {
        pieChart.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.GONE);

    }



}
