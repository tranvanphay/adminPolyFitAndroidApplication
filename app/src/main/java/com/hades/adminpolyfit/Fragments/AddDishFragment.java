package com.hades.adminpolyfit.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hades.adminpolyfit.Adapter.SpinnerLevelAdapter;
import com.hades.adminpolyfit.Adapter.SpinnerMealsAdapter;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.Model.Level;
import com.hades.adminpolyfit.Model.Meals;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hades on 17,October,2019
 **/
public class AddDishFragment extends DialogFragment implements View.OnClickListener {

    private ImageView imvAddDish, imvBackAddDish;
    private EditText edtTitleDish, edtProteinDish, edtFatDish, edtCarbDish, edtCaloriesDish,desDish;
    private CardView btnSaveDish;
    Dialog progressDialog;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    String imageLink;
    AdminPolyfitServices adminPolyfitServices;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    List<Meals> listMeals;
    Spinner spinnerMeals;

    public static AddDishFragment newInstance() {
        AddDishFragment fragment = new AddDishFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_dish, container, false);
        connectView(view);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        handleGetAllMeals();
        progressDialog = new Dialog(getActivity());
        progressDialog.setContentView(R.layout.dialog_upload);
        progressDialog.setCancelable(false);
        return view;
    }

    private void connectView(View view) {
        imvAddDish = view.findViewById(R.id.imvAddDish);
        imvAddDish.setOnClickListener(this);
        imvBackAddDish = view.findViewById(R.id.imvBackAddDish);
        imvBackAddDish.setOnClickListener(this);
        edtTitleDish = view.findViewById(R.id.titleDish);
        edtProteinDish = view.findViewById(R.id.proteinDish);
        edtFatDish = view.findViewById(R.id.fatDish);
        edtCarbDish = view.findViewById(R.id.carbDish);
        edtCaloriesDish = view.findViewById(R.id.caloriesDish);
        desDish=view.findViewById(R.id.desDish);
        btnSaveDish = view.findViewById(R.id.btnAddDish);
        btnSaveDish.setOnClickListener(this);
        spinnerMeals=view.findViewById(R.id.spnMeals);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                assert data != null;
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(Objects.requireNonNull(getActivity()).getCacheDir(), "cropped"));
                Crop.of(source_uri, destination_uri).withAspect(imvAddDish.getWidth(), imvAddDish.getHeight()).start(getActivity(), AddDishFragment.this);
                imvAddDish.setImageURI(Crop.getOutput(data));
            } else if (requestCode == Crop.REQUEST_CROP) {
                handle_crop(resultCode, data);
            }
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imvAddDish.setImageURI(Crop.getOutput(data));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imvAddDish:
                Crop.pickImage(getActivity(), AddDishFragment.this);
                break;
            case R.id.imvBackAddDish:
                this.dismiss();
                break;
            case R.id.btnAddDish:
                saveData();
                break;
        }
    }

    private void saveData() {
        progressDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imvAddDish.setDrawingCacheEnabled(true);
        imvAddDish.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imvAddDish.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return mountainsRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            imageLink = downloadUri.toString();

                            imvAddDish.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imageLink);
                            handleAddDish();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void handleAddDish() {
        String title = edtTitleDish.getText().toString();
        Double protein = Double.valueOf(edtProteinDish.getText().toString());
        Double fat = Double.valueOf(edtFatDish.getText().toString());
        Double carb = Double.valueOf(edtCarbDish.getText().toString());
        Double calories = Double.valueOf(edtCaloriesDish.getText().toString());
        final String des=desDish.getText().toString();
        Log.e("phaytv", imageLink);
        int position=spinnerMeals.getSelectedItemPosition();
        int idMeals=listMeals.get(position).getId();
        Log.e("PhayTran:::ID Meals",idMeals+"");
        mSubscriptions.add(adminPolyfitServices.addDish(title, imageLink, protein, fat, carb, calories, idMeals,des,51)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("PhayTran", s);
                        if (s.contains("Create success!")) {
                            edtTitleDish.setText("");
                            edtProteinDish.setText("");
                            edtFatDish.setText("");
                            edtCarbDish.setText("");
                            edtCaloriesDish.setText("");
                            desDish.setText("");
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }));
    }

    private List<Meals> handleGetAllMeals() {
        adminPolyfitServices.getAllMeals().enqueue(new Callback<String>() {
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
                    Type listType = new TypeToken<List<Meals>>() {
                    }.getType();
                    listMeals = gson.fromJson(jsonOutput, listType);
                    setDataSpinner(listMeals);
                    Log.e("PhayTV:::ListMeals", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
        return listMeals;
    }
    private void setDataSpinner(List<Meals> listMeals){
        SpinnerMealsAdapter spinnerMealsAdapter=new SpinnerMealsAdapter(listMeals,getActivity());
        spinnerMeals.setAdapter(spinnerMealsAdapter);

    }

}
