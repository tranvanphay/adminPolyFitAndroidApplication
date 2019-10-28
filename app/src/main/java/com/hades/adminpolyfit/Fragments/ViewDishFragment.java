package com.hades.adminpolyfit.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.hades.adminpolyfit.Activity.PlayVideoActivity;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.Model.Dish;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hades on 18,October,2019
 **/
public class ViewDishFragment extends DialogFragment implements View.OnClickListener {
    private ImageView imvDish, imvBack, imvEdit, imvDelete;
    private TextView tvDelete;
    private EditText tv_Title, tv_Protein, tv_Fat, tv_Carb, tv_Calories;
    private CardView saveEditDish, cancelEditDish;
    private RelativeLayout layoutOptionDish, layoutOptionEditDish;
    private Dish dish;
    private String imageLink;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    private ProgressDialog progressDialog;
    private AdminPolyfitServices adminPolyfitServices;
    private CardView cancelDelete, acceptDelete;
    private GifImageView processDelete;
    private Dialog dialog;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    public static ViewDishFragment newInstance() {
        ViewDishFragment fragment = new ViewDishFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.layout_view_dish, container, false);
        connectView(view);
        disableFocus();
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        setData();
        return view;
    }

    private void connectView(View view) {
        imvDish = view.findViewById(R.id.imvDish);
        imvDish.setOnClickListener(this);
        tv_Title = view.findViewById(R.id.tv_titleDish);
        tv_Protein = view.findViewById(R.id.tv_Protein);
        tv_Fat = view.findViewById(R.id.tv_Fat);
        tv_Carb = view.findViewById(R.id.tv_Carb);
        tv_Calories = view.findViewById(R.id.tv_Calories);
        imvBack = view.findViewById(R.id.outViewDish);
        imvBack.setOnClickListener(this);
        imvEdit = view.findViewById(R.id.editDish);
        imvEdit.setOnClickListener(this);
        imvDelete = view.findViewById(R.id.deleteDish);
        imvDelete.setOnClickListener(this);
        layoutOptionEditDish = view.findViewById(R.id.layoutOptionEditDish);
        layoutOptionDish = view.findViewById(R.id.layoutOptionDish);
        saveEditDish = view.findViewById(R.id.saveEditDish);
        saveEditDish.setOnClickListener(this);
        cancelEditDish = view.findViewById(R.id.cancelEditDish);
        cancelEditDish.setOnClickListener(this);
    }

    private void connectViewDialog(Dialog dialog) {
        cancelDelete = dialog.findViewById(R.id.cancelDelete);
        cancelDelete.setOnClickListener(this);
        acceptDelete = dialog.findViewById(R.id.acceptDelete);
        acceptDelete.setOnClickListener(this);
        processDelete = dialog.findViewById(R.id.processDelete);
        tvDelete = dialog.findViewById(R.id.tvDelete);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editDish:
                Log.e("PhayTran", "will edit");
                enableFocus();
                break;
            case R.id.deleteDish:
                Log.e("PhayTran", "will delete");
                createDialog();
                break;
            case R.id.outViewDish:
                this.dismiss();
                break;
            case R.id.cancelEditDish:
                layoutOptionEditDish.setVisibility(View.GONE);
                layoutOptionDish.setVisibility(View.VISIBLE);
                disableFocus();
                break;
            case R.id.saveEditDish:
                Log.e("PhayTran", "Saving edit");
//                handleDeleteImage(dish.getImageUrl());
                saveImage();
                break;
            case R.id.imvDish:
                Crop.pickImage(getActivity(), ViewDishFragment.this);
                Log.e("phayTran", "Clicked");
                break;
            case R.id.acceptDelete:
                Log.e("PhayTran", "Deleting");
                tvDelete.setVisibility(View.GONE);
                processDelete.setVisibility(View.VISIBLE);
                handleDeleteDish(dish.getId(), dialog, dish.getImageUrl());
                break;
            case R.id.cancelDelete:
                Log.e("PhayTran", "Cancel");
                dialog.dismiss();
                break;
        }
    }

    private void setData() {
        assert getArguments() != null;
        dish = (Dish) getArguments().getSerializable("dish");
        System.out.println(dish);
        assert dish != null;
        Picasso.get().load(dish.getImageUrl()).into(imvDish);
        tv_Title.setText(dish.getTitle());
        tv_Protein.setText(String.valueOf(dish.getProtein()));
        tv_Fat.setText(String.valueOf(dish.getFat()));
        tv_Carb.setText(String.valueOf(dish.getCarb()));
        tv_Calories.setText(String.valueOf(dish.getCalories()));
    }

    private void disableFocus() {
        tv_Title.setFocusable(false);
        tv_Title.setBackgroundColor(Color.TRANSPARENT);
        tv_Protein.setFocusable(false);
        tv_Protein.setBackgroundColor(Color.TRANSPARENT);
        tv_Fat.setFocusable(false);
        tv_Fat.setBackgroundColor(Color.TRANSPARENT);
        tv_Carb.setFocusable(false);
        tv_Carb.setBackgroundColor(Color.TRANSPARENT);
        tv_Calories.setFocusable(false);
        tv_Calories.setBackgroundColor(Color.TRANSPARENT);
        imvDish.setClickable(false);
    }

    private void enableFocus() {
        layoutOptionDish.setVisibility(View.GONE);
        layoutOptionEditDish.setVisibility(View.VISIBLE);
        tv_Title.setFocusableInTouchMode(true);
        tv_Title.setFocusable(true);
        tv_Title.requestFocus();
        tv_Protein.setFocusableInTouchMode(true);
        tv_Protein.setFocusable(true);
        tv_Fat.setFocusableInTouchMode(true);
        tv_Fat.setFocusable(true);
        tv_Carb.setFocusableInTouchMode(true);
        tv_Carb.setFocusable(true);
        tv_Calories.setFocusableInTouchMode(true);
        tv_Calories.setFocusable(true);
        imvDish.setClickable(true);
    }

    private void handleDeleteDish(final int id, final Dialog dialog, final String imageLink) {
        adminPolyfitServices.deleteDish(id).enqueue(new Callback<Dish>() {
            @Override
            public void onResponse(Call<Dish> call, Response<Dish> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", String.valueOf(response.body()));
                    try {
                        handleDeleteImage(imageLink);
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();

                    }
                }
                if (!response.isSuccessful()) {
                    Log.e("phayTranERROR", "delete error");
                    Log.e("PhayTran", response.code() + "");
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Delete failed!!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Dish> call, Throwable t) {
                Log.e("PhayTran", "Failed");
                dialog.dismiss();
                Toast.makeText(getActivity(), "Delete failed!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdateDish(Dish dish) {
        Call<Dish> call = adminPolyfitServices.updateDish(dish.getId(), dish.getTitle(), dish.getProtein(), dish.getFat(), dish.getCarb(), dish.getCalories(), dish.getImageUrl()/*, dish.getIdMeals()*//*, dish.getId_ingredients()*/);
        call.enqueue(new Callback<Dish>() {
            @Override
            public void onResponse(Call<Dish> call, Response<Dish> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", "Successfully");
                    Log.e("Body", response.code() + "");
                    disableFocus();
                    layoutOptionDish.setVisibility(View.VISIBLE);
                    layoutOptionEditDish.setVisibility(View.GONE);
                }
                if (!response.isSuccessful()) {
                    Log.e("PhayTranERROR", response.code() + "");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Dish> call, Throwable t) {
                Log.e("ERR", t.getMessage());
                progressDialog.dismiss();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                assert data != null;
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(Objects.requireNonNull(getActivity()).getCacheDir(), "cropped"));
                Crop.of(source_uri, destination_uri).withAspect(imvDish.getWidth(), imvDish.getHeight()).start(getActivity(), ViewDishFragment.this);
                imvDish.setImageURI(Crop.getOutput(data));
            } else if (requestCode == Crop.REQUEST_CROP) {
                handle_crop(resultCode, data);
            }
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imvDish.setImageURI(Crop.getOutput(data));
        }
    }

    private void saveImage() {
        progressDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imvDish.setDrawingCacheEnabled(true);
        imvDish.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imvDish.getDrawable()).getBitmap();
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

//                            imvDish.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imageLink);
                            Dish dishEdit = new Dish();
                            dishEdit.setId(dish.getId());
                            dishEdit.setTitle(tv_Title.getText().toString());
                            dishEdit.setProtein(Double.parseDouble(tv_Protein.getText().toString()));
                            dishEdit.setFat(Double.valueOf(tv_Fat.getText().toString()));
                            dishEdit.setCarb(Double.parseDouble(tv_Carb.getText().toString()));
                            dishEdit.setCalories(Double.parseDouble(tv_Calories.getText().toString()));
                            dishEdit.setImageUrl(imageLink);
                            dishEdit.setIdMeals(dishEdit.getIdMeals());
                            Log.e("PhayTran", dishEdit.getTitle());
                            handleUpdateDish(dishEdit);
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void createDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_delete);
        dialog.setCancelable(false);
        connectViewDialog(dialog);
        dialog.show();
    }

    private void handleDeleteImage(String urlImage) {
        StorageReference storageRef = storage.getReferenceFromUrl(urlImage);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("PhayTran", "onSuccess: deleted file");
                dialog.dismiss();
                ViewDishFragment.this.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("PhayTran", "onFailure: did not delete file");
                dialog.dismiss();
            }
        });


    }
}

