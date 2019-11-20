package com.hades.adminpolyfit.Fragments;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hades.adminpolyfit.Utils.Constants;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Objects;

import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hades on 27,October,2019
 **/
public class AddIngredientFragment extends DialogFragment implements View.OnClickListener {

    private ImageView imvAddIngredient, imvBackAddIngredient;
    private EditText edtTitleIngredient, edtPriceIngredient, edtUnitIngredient;
    private CardView btnAddIngredient;
    ProgressDialog progressDialog;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    String imageLink;
    AdminPolyfitServices adminPolyfitServices;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();


    public static AddIngredientFragment newInstance() {
        AddIngredientFragment fragment = new AddIngredientFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_ingredient, container, false);
        connectView(view);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Processing");
        return view;
    }

    private void connectView(View view) {
        imvAddIngredient = view.findViewById(R.id.imvAddIngredient);
        imvAddIngredient.setOnClickListener(this);
        imvBackAddIngredient = view.findViewById(R.id.imvBackIngredient);
        imvBackAddIngredient.setOnClickListener(this);
        edtTitleIngredient = view.findViewById(R.id.edtTitleIngredient);
        edtPriceIngredient = view.findViewById(R.id.edtPriceIngredient);
        edtUnitIngredient = view.findViewById(R.id.edtUnitIngredient);
        btnAddIngredient = view.findViewById(R.id.btnAddIngredient);
        btnAddIngredient.setOnClickListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                assert data != null;
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(Objects.requireNonNull(getActivity()).getCacheDir(), "cropped"));
                Crop.of(source_uri, destination_uri).withAspect(imvAddIngredient.getWidth(), imvAddIngredient.getHeight()).start(getActivity(), AddIngredientFragment.this);
                imvAddIngredient.setImageURI(Crop.getOutput(data));
            } else if (requestCode == Crop.REQUEST_CROP) {
                handle_crop(resultCode, data);
            }
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imvAddIngredient.setImageURI(Crop.getOutput(data));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imvAddIngredient:
                Crop.pickImage(getActivity(), AddIngredientFragment.this);
                break;
            case R.id.imvBackIngredient:
                this.dismiss();
                break;
            case R.id.btnAddIngredient:
                if (edtTitleIngredient.getText().toString().length() < 1) {
                    Toast.makeText(getActivity(), "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (imvAddIngredient.getDrawable().getConstantState() ==
                        ContextCompat.getDrawable(getActivity(), R.drawable.null_image).getConstantState()) {
                    Toast.makeText(getActivity(), "Please insert picture", Toast.LENGTH_SHORT).show();
                } else {
                    saveData();
                }

                break;
        }
    }

    private void saveData() {
        progressDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imvAddIngredient.setDrawingCacheEnabled(true);
        imvAddIngredient.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imvAddIngredient.getDrawable()).getBitmap();
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

                            imvAddIngredient.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imageLink);
                            handleAddIngredient();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void handleAddIngredient() {
        String title = edtTitleIngredient.getText().toString();
       /* float prices = Float.parseFloat(edtPriceIngredient.getText().toString());
        String unit = edtUnitIngredient.getText().toString();*/
        mSubscriptions.add(adminPolyfitServices.addIngredient(title,/* prices, unit,*/imageLink)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("PhayTran", s);
                        if (s.contains("Create success!")) {
                            edtTitleIngredient.setText("");
                            edtPriceIngredient.setText("");
                            edtUnitIngredient.setText("");
                            imvAddIngredient.setImageResource(R.drawable.ic_launcher_foreground);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }));
    }


}