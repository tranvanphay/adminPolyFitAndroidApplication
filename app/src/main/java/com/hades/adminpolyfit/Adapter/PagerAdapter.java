package com.hades.adminpolyfit.Adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.hades.adminpolyfit.Fragments.DishFragment;
import com.hades.adminpolyfit.Fragments.ExerciseFragment;
import com.hades.adminpolyfit.Fragments.IngredientFragment;
import com.hades.adminpolyfit.Fragments.MixFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {


    int numberOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                ExerciseFragment exerciseFragment = new ExerciseFragment();
                return exerciseFragment;
            case 1:
                DishFragment dishFragment = new DishFragment();
                return dishFragment;
            case 2:
                IngredientFragment ingredientFragment = new IngredientFragment();
                return ingredientFragment;
            case 3:
                MixFragment mixFragment = new MixFragment();
                return mixFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
