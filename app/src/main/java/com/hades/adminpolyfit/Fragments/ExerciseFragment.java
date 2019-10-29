package com.hades.adminpolyfit.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hades.adminpolyfit.Activity.SplashScreenActivity;
import com.hades.adminpolyfit.Adapter.ExerciseAdapter;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.Interface.ReloadDataExercise;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.subscriptions.CompositeSubscription;

public class ExerciseFragment extends Fragment implements View.OnClickListener, ReloadDataExercise {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    List<Exercise> exerciseList;
    ExerciseAdapter exerciseAdapter;
    ShimmerRecyclerView viewExercise;
    AdminPolyfitServices adminPolyfitServices;
    NavigationView navigationView;
    Animation animation;
    ImageView layoutOption, icColapse,reloadExercise;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    public ExerciseFragment() {
    }

    public static ExerciseFragment newInstance(String param1, String param2) {
        ExerciseFragment fragment = new ExerciseFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        connectView(view);
        connectViewNav();
        getAllExercise();
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
        switch (view.getId()) {
            case R.id.btnCreateExercise:
                AddExercisesFragment addExercisesFragment = (AddExercisesFragment) AddExercisesFragment.newInstance();
                addExercisesFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "tag");
                break;
            case R.id.layoutOption:
                navigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.reloadExercise:
                animation = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.rotate);
                reloadExercise.startAnimation(animation);
                getAllExercise();
                Log.e("PhayTran","Reload exercise");

                break;
          /*  case R.id.icCollapseNav:
                navigationView.setVisibility(View.GONE);
                break;*/
        }
    }

    @Override
    public void reloadDataExercise() {
        getAllExercise();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void connectView(View view) {
        CardView btnAddExercise = view.findViewById(R.id.btnCreateExercise);
        btnAddExercise.setOnClickListener(this);
        viewExercise = view.findViewById(R.id.viewExercise);
        navigationView = view.findViewById(R.id.nvView);
        layoutOption = view.findViewById(R.id.layoutOption);
        layoutOption.setOnClickListener(this);
        reloadExercise=view.findViewById(R.id.reloadExercise);
        reloadExercise.setOnClickListener(this);

    }

    private void connectViewNav() {
        View headerview = navigationView.getHeaderView(0);
        icColapse = headerview.findViewById(R.id.icCollapseNav);
        icColapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationView.setVisibility(View.GONE);
            }
        });
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                handleSelectItem(menuItem);
                return false;
            }
        });


    }

    private void handleSelectItem(MenuItem menuItem) {
        int id = menuItem.getItemId();
        Fragment fragment = null;
        Class classfragment = null;
        if (id == R.id.quotesFragment) {
            QuotesFragment quotesFragment = (QuotesFragment) QuotesFragment.newInstance();
            quotesFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "tag");
            menuItem.setChecked(true);
        }
        if (id == R.id.staticalFragment) {
            StatisticalFragment statisticalFragment = (StatisticalFragment) StatisticalFragment.newInstance();
            statisticalFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "tag");
            menuItem.setChecked(true);
        }
        if (id == R.id.logout) {
            SharedPreferences.Editor sharedPreferences=getActivity().getSharedPreferences(Constants.LOGIN,Context.MODE_PRIVATE).edit();
            sharedPreferences.putString("username","");
            sharedPreferences.putString("password","");
            sharedPreferences.apply();
            startActivity(new Intent(getActivity(), SplashScreenActivity.class));
            getActivity().finish();
            menuItem.setChecked(true);
        }

    }

/*
    private List<Exercise> addExercise() {
        exerciseList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Exercise exercise = new Exercise();
            exercise.setId(i);
            exercise.setTitle("tapTay");
            exercise.setIntroduction("abc");
            exerciseList.add(exercise);
        }
        return exerciseList;
    }*/

    private void setData(List<Exercise> exerciseList) {
        viewExercise.setDemoChildCount(exerciseList.size());
        viewExercise.showShimmerAdapter();
        viewExercise.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        viewExercise.setLayoutManager(layoutManager);
        exerciseAdapter = new ExerciseAdapter(exerciseList, getContext());
        viewExercise.setAdapter(exerciseAdapter);
        reloadExercise.clearAnimation();

    }


    private void  getAllExercise() {
        animation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.rotate);
        reloadExercise.startAnimation(animation);
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
                    List<Exercise> exercisesList = gson.fromJson(jsonOutput, listType);
                    setData(exercisesList);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
