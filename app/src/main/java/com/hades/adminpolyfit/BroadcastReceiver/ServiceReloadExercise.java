package com.hades.adminpolyfit.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hades.adminpolyfit.Interface.ReloadDataExercise;

/**
 * Created by Hades on 26,October,2019
 **/
public class ServiceReloadExercise extends BroadcastReceiver {

    private ReloadDataExercise reloadDataExercise;

    public ServiceReloadExercise() {
    }

    public ServiceReloadExercise(ReloadDataExercise reloadDataExercise ){

        this.reloadDataExercise = reloadDataExercise;

    }
    @Override
    public void onReceive(Context context, Intent intent) {
            reloadDataExercise.reloadDataExercise();
            Intent intent1 = new Intent("reloadExercise");
            context.sendBroadcast(intent1);
    }
}
