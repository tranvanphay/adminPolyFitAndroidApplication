package com.hades.adminpolyfit.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.hades.adminpolyfit.Adapter.PagerAdapter;
import com.hades.adminpolyfit.Fragments.DishFragment;
import com.hades.adminpolyfit.Fragments.ExerciseFragment;
import com.hades.adminpolyfit.Fragments.IngredientFragment;
import com.hades.adminpolyfit.Fragments.MixFragment;
import com.hades.adminpolyfit.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ExerciseFragment.OnFragmentInteractionListener, DishFragment.OnFragmentInteractionListener,
        IngredientFragment.OnFragmentInteractionListener, MixFragment.OnFragmentInteractionListener {
   NavigationView navigationView;
   ImageView layoutOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
//        getReminder();
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_exercise));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_diet));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ingredient));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.mix));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = findViewById(R.id.pager);
        final com.hades.adminpolyfit.Adapter.PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
