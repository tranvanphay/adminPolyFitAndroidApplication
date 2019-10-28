package com.hades.adminpolyfit.Fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hades.adminpolyfit.Adapter.DishAdapter;
import com.hades.adminpolyfit.Adapter.ExerciseAdapter;
import com.hades.adminpolyfit.Model.Dish;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DishFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    CardView btnAddDish;
    ImageView reloadDish;
    AdminPolyfitServices adminPolyfitServices;
    RecyclerView viewDish;
    DishAdapter dishAdapter;
    Animation animation;

    public DishFragment() {
    }


    public static DishFragment newInstance(String param1, String param2) {
        DishFragment fragment = new DishFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.e("PhayTran","OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("PhayTran","OnCreateView");
        View view = inflater.inflate(R.layout.fragment_dish, container, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        connectView(view);
        getAllDish();
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Log.e("PhayTran","Onattack");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.e("PhayTran","Detatck");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateDish:
                AddDishFragment addDishFragment = (AddDishFragment) AddDishFragment.newInstance();
                addDishFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "tag");
                break;
            case R.id.reloadDish:

                getAllDish();
                break;
        }
    }

    private void connectView(View view) {
        btnAddDish = view.findViewById(R.id.btnCreateDish);
        btnAddDish.setOnClickListener(this);
        viewDish=view.findViewById(R.id.viewDish);
        reloadDish=view.findViewById(R.id.reloadDish);
        reloadDish.setOnClickListener(this);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void getAllDish() {
        animation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.rotate);
        reloadDish.startAnimation(animation);
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
                    List<Dish> listDish = gson.fromJson(jsonOutput, listType);
                    setData(listDish);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setData(List<Dish> dishList){
        viewDish.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        viewDish.setLayoutManager(mLayoutManager);
        dishAdapter = new DishAdapter(dishList, getContext());
        viewDish.setAdapter(dishAdapter);
        reloadDish.clearAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
    Log.e("PhayTran","OnResume DishFragment");
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.e("PhayTRan","On attack Dish Fragment");
    }
}
