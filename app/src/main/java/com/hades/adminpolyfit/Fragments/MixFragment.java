package com.hades.adminpolyfit.Fragments;


import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.hades.adminpolyfit.Adapter.BodyPartsAdapter;
import com.hades.adminpolyfit.Adapter.DishAdapter;
import com.hades.adminpolyfit.Adapter.LevelAdapter;
import com.hades.adminpolyfit.Adapter.MealsAdapter;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.Model.Bodyparts;
import com.hades.adminpolyfit.Model.Dish;
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

public class MixFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Dialog processDialog;
    AdminPolyfitServices adminPolyfitServices;
    List<Level> listLevel;
    List<Bodyparts> bodypartsList;
    List<Meals> mealsList;
    LevelAdapter levelAdapter;
    BodyPartsAdapter bodyPartsAdapter;
    MealsAdapter mealsAdapter;
    private String mParam1;
    private String mParam2;
    private RecyclerView viewLevel, viewBodyParts, viewMeals;
    private ImageView createLevel, createBodyParts, createMeals;
    private OnFragmentInteractionListener mListener;
    Dialog dialogCreateLevel, dialogCreateBodyparts, dialogCreateMeals;
    EditText titleLevel, descriptionLevel, titleBodyParts,titleMeals;
    CardView saveNewLevel, saveNewbodyParts,saveNewMeals;
    ImageView imvAddLevel, imvAddBodyParts,imvAddMeals;
    String imageLink;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    public MixFragment() {
    }


    public static MixFragment newInstance(String param1, String param2) {
        MixFragment fragment = new MixFragment();
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
        View view = inflater.inflate(R.layout.fragment_mix, container, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        connectView(view);
        getAllLevel();
        getAllBodyParts();
        handleGetAllMeals();
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
            case R.id.createLevel:
                setupDialogCreateLevel();
                break;
            case R.id.createBodyParts:
                setupDialogBodyParts();
                break;
            case R.id.createMeals:
                setupDialogMeals();
                break;
            case R.id.saveNewLevel:
                saveDataLevel();
                break;
            case R.id.imvAddLevel:
                Log.e("PhayTran:::", "imv");
                Crop.pickImage(getActivity(), MixFragment.this);
                break;
            case R.id.imvAddBodyParts:
                Log.e("PhayTran:::", "imvAdd bodyParts");
                Crop.pickImage(getActivity(), MixFragment.this);
                break;
            case R.id.saveNewBodyParts:
                saveDataBodyParts();
                break;

            case R.id.imvAddMeals:
                Log.e("PhayTran:::", "imvAdd Meals");
                Crop.pickImage(getActivity(), MixFragment.this);
                break;
            case R.id.saveNewMeals:
                saveDataMeals();
                break;
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //connectView
    private void connectView(View view) {
        viewLevel = view.findViewById(R.id.viewLevel);
        viewBodyParts = view.findViewById(R.id.viewBodyParts);
        viewMeals = view.findViewById(R.id.viewMeals);
        createLevel = view.findViewById(R.id.createLevel);
        createLevel.setOnClickListener(this);
        createBodyParts = view.findViewById(R.id.createBodyParts);
        createBodyParts.setOnClickListener(this);
        createMeals = view.findViewById(R.id.createMeals);
        createMeals.setOnClickListener(this);
    }


    //Handle getAllLevel
    public void getAllLevel() {
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
                    setDataLevel(listLevel);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataLevel(List<Level> listLevel) {
        viewLevel.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        viewLevel.setLayoutManager(layoutManager);
        levelAdapter = new LevelAdapter(listLevel, getContext(),MixFragment.this);
        viewLevel.setAdapter(levelAdapter);

    }

    public void getAllBodyParts() {
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
                    setDataBodyParts(bodypartsList);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataBodyParts(List<Bodyparts> bodypartsList) {
        viewBodyParts.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        viewBodyParts.setLayoutManager(layoutManager);
        bodyPartsAdapter = new BodyPartsAdapter(bodypartsList, getContext(),MixFragment.this);
        viewBodyParts.setAdapter(bodyPartsAdapter);
    }

    public void handleGetAllMeals() {
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
                    mealsList = gson.fromJson(jsonOutput, listType);
                    setDataMeals(mealsList);
                    Log.e("PhayTV:::ListMeals", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataMeals(List<Meals> mealsList) {
        viewMeals.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        viewMeals.setLayoutManager(layoutManager);
        mealsAdapter = new MealsAdapter(mealsList, getContext(),MixFragment.this);
        viewMeals.setAdapter(mealsAdapter);
    }


    private void saveDataLevel() {
        processDialog = new Dialog(getActivity());
        processDialog.setCancelable(false);
        processDialog.setContentView(R.layout.dialog_upload);
        processDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imvAddLevel.setDrawingCacheEnabled(true);
        imvAddLevel.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imvAddLevel.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                processDialog.dismiss();
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

                            imvAddLevel.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imageLink);
                            handleCreateLevel();
                        } else {
                            processDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void saveDataBodyParts() {
        processDialog = new Dialog(getActivity());
        processDialog.setCancelable(false);
        processDialog.setContentView(R.layout.dialog_upload);
        processDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imvAddBodyParts.setDrawingCacheEnabled(true);
        imvAddBodyParts.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imvAddBodyParts.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                processDialog.dismiss();
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

                            imvAddBodyParts.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imageLink);
                            handleCreateBodyParts();
                        } else {
                            processDialog.dismiss();
                        }
                    }
                });
            }
        });


    }

    private void saveDataMeals() {
        processDialog = new Dialog(getActivity());
        processDialog.setCancelable(false);
        processDialog.setContentView(R.layout.dialog_upload);
        processDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imvAddMeals.setDrawingCacheEnabled(true);
        imvAddMeals.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imvAddMeals.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                processDialog.dismiss();
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

                            imvAddMeals.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imageLink);
                            handleCreateMeals();
                        } else {
                            processDialog.dismiss();
                        }
                    }
                });
            }
        });


    }



    private void handleCreateLevel() {
        String title = titleLevel.getText().toString();
        final String description = descriptionLevel.getText().toString();
        Log.e("phaytv", imageLink);
        mSubscriptions.add(adminPolyfitServices.addLevel(title, imageLink, description)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("PhayTran", s);
                        if (s.contains("Create success!")) {
                            titleLevel.setText("");
                            descriptionLevel.setText("");
                            processDialog.dismiss();
                            dialogCreateLevel.dismiss();
                            getAllLevel();

                        } else {
                            Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_SHORT).show();
                            processDialog.dismiss();

                        }
                    }
                }));


    }

    private void handleCreateBodyParts() {
        String title = titleBodyParts.getText().toString();
        Log.e("phaytv", imageLink);
        mSubscriptions.add(adminPolyfitServices.addBodyParts(title, imageLink)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("PhayTran", s);
                        if (s.contains("Create success!")) {
                            titleBodyParts.setText("");
                            processDialog.dismiss();
                            dialogCreateBodyparts.dismiss();
                            getAllBodyParts();

                        } else {
                            Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_SHORT).show();
                            processDialog.dismiss();

                        }
                    }
                }));
    }

    private void handleCreateMeals() {
        String title = titleMeals.getText().toString();
        Log.e("phaytv", imageLink);
        mSubscriptions.add(adminPolyfitServices.addMeals(title, imageLink,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("PhayTran", s);
                        if (s.contains("Create success!")) {
                            titleMeals.setText("");
                            processDialog.dismiss();
                            dialogCreateMeals.dismiss();
                            handleGetAllMeals();

                        } else {
                            Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_SHORT).show();
                            processDialog.dismiss();

                        }
                    }
                }));

    }

    private void setupDialogCreateLevel() {
        dialogCreateLevel = new Dialog(getActivity());
        dialogCreateLevel.setContentView(R.layout.dialog_add_level);
        imvAddLevel = dialogCreateLevel.findViewById(R.id.imvAddLevel);
        imvAddLevel.setOnClickListener(this);
        titleLevel = dialogCreateLevel.findViewById(R.id.edtTitleAddLevel);
        descriptionLevel = dialogCreateLevel.findViewById(R.id.edtDescriptionAddLevel);
        saveNewLevel = dialogCreateLevel.findViewById(R.id.saveNewLevel);
        saveNewLevel.setOnClickListener(this);
        dialogCreateLevel.show();


    }

    private void setupDialogBodyParts() {
        dialogCreateBodyparts = new Dialog(getActivity());
        dialogCreateBodyparts.setContentView(R.layout.dialog_add_bodyparts);
        imvAddBodyParts = dialogCreateBodyparts.findViewById(R.id.imvAddBodyParts);
        imvAddBodyParts.setOnClickListener(this);
        titleBodyParts = dialogCreateBodyparts.findViewById(R.id.edtTitleAddBodyParts);
        saveNewbodyParts = dialogCreateBodyparts.findViewById(R.id.saveNewBodyParts);
        saveNewbodyParts.setOnClickListener(this);
        dialogCreateBodyparts.show();
    }

    private void setupDialogMeals() {
        dialogCreateMeals = new Dialog(getActivity());
        dialogCreateMeals.setContentView(R.layout.dialog_add_meals);
        imvAddMeals = dialogCreateMeals.findViewById(R.id.imvAddMeals);
        imvAddMeals.setOnClickListener(this);
        titleMeals = dialogCreateMeals.findViewById(R.id.edtTitleAddMeals);
        saveNewMeals = dialogCreateMeals.findViewById(R.id.saveNewMeals);
        saveNewMeals.setOnClickListener(this);
        dialogCreateMeals.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                assert data != null;
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(Objects.requireNonNull(getActivity()).getCacheDir(), "cropped"));
                Crop.of(source_uri, destination_uri).withAspect(1280, 720).start(getActivity(), MixFragment.this);
//                imvAddLevel.setImageURI(Crop.getOutput(data));
                if (dialogCreateLevel != null) {
                    if (dialogCreateLevel.isShowing() == true) {
                        imvAddLevel.setImageURI(Crop.getOutput(data));
                    }
                }
                if (dialogCreateBodyparts != null) {
                    if (dialogCreateBodyparts.isShowing() == true) {
                        imvAddBodyParts.setImageURI(Crop.getOutput(data));
                    }
                }
                if (dialogCreateMeals != null) {
                    if (dialogCreateMeals.isShowing() == true) {
                        imvAddMeals.setImageURI(Crop.getOutput(data));
                    }
                }
            } else if (requestCode == Crop.REQUEST_CROP) {
                handle_crop(resultCode, data);
            }
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (dialogCreateLevel != null) {
                if (dialogCreateLevel.isShowing()) {
                    imvAddLevel.setImageURI(Crop.getOutput(data));
                }
            }
            if (dialogCreateBodyparts != null) {
                if (dialogCreateBodyparts.isShowing()) {
                    imvAddBodyParts.setImageURI(Crop.getOutput(data));
                }
            }
            if (dialogCreateMeals != null) {
                if (dialogCreateMeals.isShowing()) {
                    imvAddMeals.setImageURI(Crop.getOutput(data));
                }
            }
        }
    }

}
