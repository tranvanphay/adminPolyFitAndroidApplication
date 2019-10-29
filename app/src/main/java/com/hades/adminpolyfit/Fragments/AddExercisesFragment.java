package com.hades.adminpolyfit.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;


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
import com.hades.adminpolyfit.Adapter.SpinnerBodyPartsAdapter;
import com.hades.adminpolyfit.Adapter.SpinnerLevelAdapter;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.Model.Bodyparts;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.Model.Level;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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

public class AddExercisesFragment extends DialogFragment implements View.OnClickListener {
    ImageView imvBack, imv_imageExercise;
    CardView btnSaveNewExercise;
    EditText edt_titleExercise, edt_introductionExercise, edt_contentExercise, edt_tipsExercise, edt_setsExercise, edt_repsExercise, edt_restExercise, edt_urlVideoExercise;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    String imgExercis = "";
    AdminPolyfitServices adminPolyfitServices;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private AppCompatSpinner spinnerLevel/*, spinnerBodyParts*/;
    private AppCompatSpinner spinnerBodyParts;
    List<Level> listLevel;
    List<Bodyparts> bodypartsList;
    Button btnCheck;


    public static AddExercisesFragment newInstance() {
        return new AddExercisesFragment();
    }

    private ProgressDialog progressDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.fragment_add_exercises_fragment, container, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        connectView(view);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.processing));
        progressDialog.setCancelable(false);
        getAllLevel();
        getAllBodyParts();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imvBack:
                this.dismiss();
                break;
            case R.id.saveNewExercise:
                Log.e("PhayTran", "Save exercise");
                if (edt_urlVideoExercise.getText().toString().length() < 25) {
                    Toast.makeText(getActivity(), "Please enter video link", Toast.LENGTH_SHORT).show();
                } else {
                    saveImage();
                }
                break;
            case R.id.imv_imageExercise:
                Log.e("PhayTran", "get image");
                Crop.pickImage(getActivity(), AddExercisesFragment.this);
                break;
        }

    }

    private void saveImage() {
        progressDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imv_imageExercise.setDrawingCacheEnabled(true);
        imv_imageExercise.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imv_imageExercise.getDrawable()).getBitmap();
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
                            imgExercis = downloadUri.toString();

                            imv_imageExercise.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imgExercis);
                            handleAddExercise();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void connectView(View view) {
        imvBack = view.findViewById(R.id.imvBack);
        imvBack.setOnClickListener(this);
        btnSaveNewExercise = view.findViewById(R.id.saveNewExercise);
        btnSaveNewExercise.setOnClickListener(this);
        imv_imageExercise = view.findViewById(R.id.imv_imageExercise);
        imv_imageExercise.setOnClickListener(this);
        edt_titleExercise = view.findViewById(R.id.edt_titleExercise);
        edt_introductionExercise = view.findViewById(R.id.edt_introductionExercise);
        edt_contentExercise = view.findViewById(R.id.edt_contentExercise);
        edt_tipsExercise = view.findViewById(R.id.edt_tipsExercise);
        edt_setsExercise = view.findViewById(R.id.edt_setsExercise);
        edt_repsExercise = view.findViewById(R.id.edt_repsExercise);
        edt_restExercise = view.findViewById(R.id.edt_restExercise);
        edt_urlVideoExercise = view.findViewById(R.id.edt_urlVideoExercise);
        spinnerLevel = view.findViewById(R.id.spinnerLevel);
        spinnerBodyParts = view.findViewById(R.id.spinnerBodyParts);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                assert data != null;
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(Objects.requireNonNull(getActivity()).getCacheDir(), "cropped"));
                Crop.of(source_uri, destination_uri).withAspect(imv_imageExercise.getWidth(), imv_imageExercise.getHeight()).start(getActivity(), AddExercisesFragment.this);
                imv_imageExercise.setImageURI(Crop.getOutput(data));
            } else if (requestCode == Crop.REQUEST_CROP) {
                handle_crop(resultCode, data);
            }
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imv_imageExercise.setImageURI(Crop.getOutput(data));
        }
    }

    //Handle add exercise
    private void handleAddExercise() {
//        Exercise exercise=new Exercise();
//        saveImage();
        String title = edt_titleExercise.getText().toString();
        String introduction = edt_introductionExercise.getText().toString();
        String content = edt_contentExercise.getText().toString();
        String tips = edt_tipsExercise.getText().toString();
        int sets = Integer.parseInt(edt_setsExercise.getText().toString());
        int reps = Integer.parseInt(edt_repsExercise.getText().toString());
        int rest = Integer.parseInt(edt_restExercise.getText().toString());
        int positionLevel = spinnerLevel.getSelectedItemPosition();
        int idLevel = listLevel.get(positionLevel).getId();
        int positionBodyParts = spinnerBodyParts.getSelectedItemPosition();
        int idBodyParts = bodypartsList.get(positionBodyParts).getIdBodyPart();
        String videoUrl = edt_urlVideoExercise.getText().toString();
        List<Integer> idList=new ArrayList<>();
        for (int i=0;i<bodypartsList.size();i++){
            Log.e("PhayTRan","list:::"+bodypartsList.size());
            Log.e("PhayTRan","bodyparts ::: "+bodypartsList.get(i).isChecked());
            if(bodypartsList.get(i).isChecked()){
                idList.add(bodypartsList.get(i).getIdBodyPart());
            }
        }
        Log.e("phaytv", idBodyParts + "");
        mSubscriptions.add(adminPolyfitServices.addExercise(title, introduction, content, tips, sets, reps, rest, videoUrl, imgExercis, idLevel, idList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("PhayTran", s);
                        if (s.contains("Create success!")) {
                            edt_titleExercise.setText("");
                            edt_introductionExercise.setText("");
                            edt_contentExercise.setText("");
                            edt_tipsExercise.setText("");
                            edt_setsExercise.setText("");
                            edt_repsExercise.setText("");
                            edt_restExercise.setText("");
                            edt_urlVideoExercise.setText("");
                            progressDialog.dismiss();
                        } else {
                            Log.e("phayTran", s);
                            Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }));
    }

    //Handle getAllLevel
    private void getAllLevel() {
        adminPolyfitServices.getAllLevel().enqueue(new Callback<String>() {
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
                    Type listType = new TypeToken<List<Level>>() {
                    }.getType();
                    listLevel = gson.fromJson(jsonOutput, listType);
                    setDataLevelSpinner(listLevel);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllBodyParts() {
        adminPolyfitServices.getAllBodyParts().enqueue(new Callback<String>() {
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
                    Type listType = new TypeToken<List<Bodyparts>>() {
                    }.getType();
                    bodypartsList = gson.fromJson(jsonOutput, listType);
                    setDataBodyPartsSpinner(bodypartsList);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataLevelSpinner(List<Level> levelList) {
        SpinnerLevelAdapter spinnerLevelAdapter = new SpinnerLevelAdapter(levelList, getActivity());
        spinnerLevel.setAdapter(spinnerLevelAdapter);

    }

    private void setDataBodyPartsSpinner(List<Bodyparts> bodypartsList) {
        SpinnerBodyPartsAdapter spinnerBodyPartsAdapter = new SpinnerBodyPartsAdapter(bodypartsList, getActivity());
        spinnerBodyParts.setAdapter(spinnerBodyPartsAdapter);

    }


}

