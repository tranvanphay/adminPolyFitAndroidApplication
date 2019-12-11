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

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hades.adminpolyfit.Adapter.IngredientAdapter;
import com.hades.adminpolyfit.Model.Ingredients;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IngredientFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ImageView reloadIngredient;
    private CardView btnCreateIngredient;
    private RecyclerView viewIngredient;
    private Animation animation;
    private AdminPolyfitServices adminPolyfitServices;
    private OnFragmentInteractionListener mListener;
    private IngredientAdapter ingredientAdapter;

    public IngredientFragment() {
    }

    public static IngredientFragment newInstance(String param1, String param2) {
        IngredientFragment fragment = new IngredientFragment();
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
    }

    private void connectView(View view){
        reloadIngredient=view.findViewById(R.id.reloadIngredient);
        reloadIngredient.setOnClickListener(this);
        btnCreateIngredient=view.findViewById(R.id.btnCreateIngredient);
        btnCreateIngredient.setOnClickListener(this);
        viewIngredient=view.findViewById(R.id.viewIngredient);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ingredient, container, false);
        connectView(view);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        handleGetAllIngredient();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reloadIngredient:
                Log.e("PhayTran","Reload ingredient");
                handleGetAllIngredient();
                break;
            case R.id.btnCreateIngredient:
                Log.e("PhayTran","Create Ingredient button");
                AddIngredientFragment addIngredientFragment = (AddIngredientFragment) AddIngredientFragment.newInstance();
                addIngredientFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "tag");
                break;
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    public void  handleGetAllIngredient() {
        animation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.rotate);
        reloadIngredient.startAnimation(animation);
        adminPolyfitServices.getAllIngredient().enqueue(new Callback<String>() {
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
                    Type listType = new TypeToken<List<Ingredients>>() {
                    }.getType();
                    List<Ingredients> ingredientsList = gson.fromJson(jsonOutput, listType);
                    Collections.reverse(ingredientsList);
                    setData(ingredientsList);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: List ingredient ::" + array);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setData(List<Ingredients> ingredientsList){
        viewIngredient.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        viewIngredient.setLayoutManager(mLayoutManager);
        ingredientAdapter = new IngredientAdapter(ingredientsList, getContext(),IngredientFragment.this);
        viewIngredient.setAdapter(ingredientAdapter);
        reloadIngredient.clearAnimation();
    }


}
