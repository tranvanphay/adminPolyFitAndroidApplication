package com.hades.adminpolyfit.Fragments;

import android.app.Dialog;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
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
import com.hades.adminpolyfit.Adapter.ExerciseAdapter;
import com.hades.adminpolyfit.Adapter.QuotesAdapter;
import com.hades.adminpolyfit.Adapter.SpinnerMealsAdapter;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.Model.Meals;
import com.hades.adminpolyfit.Model.Quotes;
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
 * Created by Hades on 23,October,2019
 **/
public class QuotesFragment extends DialogFragment implements View.OnClickListener {

    private ImageView imvBackQuotes, actionButtonAddQuotes, imvAddQuotes;
    private ShimmerRecyclerView viewQuotes;
    private EditText edtTitleAddQuotes;
    AdminPolyfitServices adminPolyfitServices;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    List<Quotes> listQuotes;
    private CardView saveNewQuotes;
    private Dialog dialog;
    private Dialog processDialog;
    private String imageLink;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    QuotesAdapter quotesAdapter;

    public static QuotesFragment newInstance() {
        QuotesFragment fragment = new QuotesFragment();
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
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        connectView(view);
        getAllQuotes();
        return view;
    }

    private void connectView(View view) {
        imvBackQuotes = view.findViewById(R.id.imvBackQuotes);
        imvBackQuotes.setOnClickListener(this);
        viewQuotes = view.findViewById(R.id.viewQuotes);
        actionButtonAddQuotes = view.findViewById(R.id.actionButtonAddQuotes);
        actionButtonAddQuotes.setOnClickListener(this);
        viewQuotes = view.findViewById(R.id.viewQuotes);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imvBackQuotes:
                this.dismiss();
                break;
            case R.id.actionButtonAddQuotes:
                connectViewDialog();
                break;
            case R.id.saveNewQuotes:
                Log.e("PhayTran:::", "button");
                saveData();
                break;
            case R.id.imvAddQuotes:
                Log.e("PhayTran:::", "imv");
                Crop.pickImage(getActivity(), QuotesFragment.this);
                break;
        }
    }

    private void connectViewDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_quotes);
        imvAddQuotes = dialog.findViewById(R.id.imvAddQuotes);
        imvAddQuotes.setOnClickListener(this);
        edtTitleAddQuotes = dialog.findViewById(R.id.edtTitleAddQuotes);
        saveNewQuotes = dialog.findViewById(R.id.saveNewQuotes);
        saveNewQuotes.setOnClickListener(this);
        dialog.show();
    }

    //Handle crop image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                assert data != null;
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(Objects.requireNonNull(getActivity()).getCacheDir(), "cropped"));
                Crop.of(source_uri, destination_uri).withAspect(1280, 720).start(getActivity(), QuotesFragment.this);
                imvAddQuotes.setImageURI(Crop.getOutput(data));
            } else if (requestCode == Crop.REQUEST_CROP) {
                handle_crop(resultCode, data);
            }
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imvAddQuotes.setImageURI(Crop.getOutput(data));
        }
    }

    private void saveData() {
        processDialog = new Dialog(getActivity());
        processDialog.setCancelable(false);
        processDialog.setContentView(R.layout.dialog_upload);
        processDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imvAddQuotes.setDrawingCacheEnabled(true);
        imvAddQuotes.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imvAddQuotes.getDrawable()).getBitmap();
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

                            imvAddQuotes.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imageLink);
                            handleAddQuotes();
                        } else {
                            processDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void handleAddQuotes() {
        String title = edtTitleAddQuotes.getText().toString();
        Log.e("phaytv", imageLink);
        mSubscriptions.add(adminPolyfitServices.addQuotes(title, imageLink)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("PhayTran", s);
                        if (s.contains("Create success!")) {
                            edtTitleAddQuotes.setText("");
                            processDialog.dismiss();
                            dialog.dismiss();
                            getAllQuotes();
                        } else {
                            Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_SHORT).show();
                            processDialog.dismiss();

                        }
                    }
                }));
    }

    public void getAllQuotes() {
        adminPolyfitServices.getAllQuotes().enqueue(new Callback<String>() {
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
                    Type listType = new TypeToken<List<Quotes>>() {
                    }.getType();
                    List<Quotes> quotesList = gson.fromJson(jsonOutput, listType);
                    setData(quotesList);
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData(List<Quotes> listQuotes) {
        viewQuotes.setDemoChildCount(listQuotes.size());
        viewQuotes.showShimmerAdapter();
        viewQuotes.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        viewQuotes.setLayoutManager(layoutManager);
        quotesAdapter = new QuotesAdapter(listQuotes, getContext(),QuotesFragment.this);
        viewQuotes.setAdapter(quotesAdapter);

    }

}

