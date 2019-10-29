package com.hades.adminpolyfit.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.hades.adminpolyfit.Activity.PlayVideoActivity;
import com.hades.adminpolyfit.BroadcastReceiver.ServiceReloadExercise;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.Interface.ReloadDataExercise;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.Model.Level;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
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
public class ViewExerciseFragment extends DialogFragment implements View.OnClickListener {
    ImageView imvExercise, imvBack, imvEdit, imvDelete;
    TextView tvDelete;
    EditText tv_Title, tv_Introduction, tv_Content, tv_Tips, tv_Sets, tv_Reps, tv_Rest, tv_videoUrl;
    CardView btnPlayNow, saveEditExercise, cancelEditExercise;
    RelativeLayout layoutOptionExercise, layoutOptionEditExercise;
    Exercise exercise;
    String imageLink;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    ProgressDialog progressDialog;
    AdminPolyfitServices adminPolyfitServices;
    CardView cancelDelete, acceptDelete;
    GifImageView processDelete;
    private Dialog dialog;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    public static ViewExerciseFragment newInstance() {
        ViewExerciseFragment fragment = new ViewExerciseFragment();
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
        View view = inflater.inflate(R.layout.layout_view_exercises, container, false);
        connectView(view);
        disableFocus();
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        setData();
        return view;
    }

    private void connectView(View view) {
        imvExercise = view.findViewById(R.id.imvExercise);
        imvExercise.setOnClickListener(this);
        tv_Title = view.findViewById(R.id.tv_titleExercise);
        tv_Introduction = view.findViewById(R.id.tv_introductionExercise);
        tv_Content = view.findViewById(R.id.tv_contentExercise);
        tv_Tips = view.findViewById(R.id.tv_tipExercise);
        tv_Sets = view.findViewById(R.id.tvSetsExercise);
        tv_Reps = view.findViewById(R.id.tvRepeatExercise);
        tv_Rest = view.findViewById(R.id.tvRestExercise);
        tv_videoUrl = view.findViewById(R.id.tv_videoUrl);
        btnPlayNow = view.findViewById(R.id.btnPlayNow);
        btnPlayNow.setOnClickListener(this);
        imvBack = view.findViewById(R.id.outViewExercise);
        imvBack.setOnClickListener(this);
        imvEdit = view.findViewById(R.id.editExercise);
        imvEdit.setOnClickListener(this);
        imvDelete = view.findViewById(R.id.deleteExercise);
        imvDelete.setOnClickListener(this);
        layoutOptionEditExercise = view.findViewById(R.id.layoutOptionEditExercise);
        layoutOptionExercise = view.findViewById(R.id.layoutOptionExercise);
        saveEditExercise = view.findViewById(R.id.saveEditExercise);
        saveEditExercise.setOnClickListener(this);
        cancelEditExercise = view.findViewById(R.id.cancelEditExercise);
        cancelEditExercise.setOnClickListener(this);
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
            case R.id.btnPlayNow:
                startActivity(new Intent(getActivity(), PlayVideoActivity.class).putExtra("videoUrl",exercise.getVideo_url()));
                break;
            case R.id.editExercise:
                Log.e("PhayTran", "will edit");
                enableFocus();
                break;
            case R.id.deleteExercise:
                Log.e("PhayTran", "will delete");
                createDialog();
                break;
            case R.id.outViewExercise:
                this.dismiss();
                break;
            case R.id.cancelEditExercise:
                layoutOptionEditExercise.setVisibility(View.GONE);
                layoutOptionExercise.setVisibility(View.VISIBLE);
                disableFocus();
                break;
            case R.id.saveEditExercise:
                Log.e("PhayTran", "Saving edit");
//                handleDeleteImage(exercise.getImage_url());
                saveImage();
                break;
            case R.id.imvExercise:
                Crop.pickImage(getActivity(), ViewExerciseFragment.this);
                Log.e("phayTran", "Clicked");
                break;
            case R.id.acceptDelete:
                Log.e("PhayTran", "Deleting");
                tvDelete.setVisibility(View.GONE);
                processDelete.setVisibility(View.VISIBLE);
                handleDeleteExercise(exercise.getId(), dialog, exercise.getImage_url());
                break;
            case R.id.cancelDelete:
                Log.e("PhayTran", "Cancel");
                dialog.dismiss();
                break;
        }
    }

    private void setData() {
        assert getArguments() != null;
        exercise = (Exercise) getArguments().getSerializable("exercise");
        System.out.println(exercise);
        assert exercise != null;
        Picasso.get().load(exercise.getImage_url()).into(imvExercise);
        tv_Title.setText(exercise.getTitle());
        tv_Introduction.setText(exercise.getIntroduction());
        tv_Content.setText(exercise.getContent());
        tv_Tips.setText(exercise.getTips());
        tv_Sets.setText(String.valueOf(exercise.getSets()));
        tv_Reps.setText(String.valueOf(exercise.getReps()));
        tv_Rest.setText(String.valueOf(exercise.getRest()));
        tv_videoUrl.setText(exercise.getVideo_url());
    }

    private void disableFocus() {
        tv_Title.setFocusable(false);
        tv_Title.setBackgroundColor(Color.TRANSPARENT);
        tv_Introduction.setFocusable(false);
        tv_Introduction.setBackgroundColor(Color.TRANSPARENT);
        tv_Content.setFocusable(false);
        tv_Content.setBackgroundColor(Color.TRANSPARENT);
        tv_Tips.setFocusable(false);
        tv_Tips.setBackgroundColor(Color.TRANSPARENT);
        tv_Sets.setFocusable(false);
        tv_Sets.setBackgroundColor(Color.TRANSPARENT);
        tv_Reps.setFocusable(false);
        tv_Reps.setBackgroundColor(Color.TRANSPARENT);
        tv_Rest.setFocusable(false);
        tv_Rest.setBackgroundColor(Color.TRANSPARENT);
        tv_videoUrl.setVisibility(View.GONE);
        btnPlayNow.setVisibility(View.VISIBLE);
        imvExercise.setClickable(false);
    }

    private void enableFocus() {
        layoutOptionExercise.setVisibility(View.GONE);
        layoutOptionEditExercise.setVisibility(View.VISIBLE);
        tv_Title.setFocusableInTouchMode(true);
        tv_Title.setFocusable(true);
        tv_Title.requestFocus();
        tv_Introduction.setFocusableInTouchMode(true);
        tv_Introduction.setFocusable(true);
        tv_Content.setFocusableInTouchMode(true);
        tv_Content.setFocusable(true);
        tv_Tips.setFocusableInTouchMode(true);
        tv_Tips.setFocusable(true);
        tv_Sets.setFocusableInTouchMode(true);
        tv_Sets.setFocusable(true);
        tv_Reps.setFocusableInTouchMode(true);
        tv_Reps.setFocusable(true);
        tv_Rest.setFocusableInTouchMode(true);
        tv_Rest.setFocusable(true);
        tv_videoUrl.setVisibility(View.VISIBLE);
        tv_videoUrl.setBackgroundColor(Color.TRANSPARENT);
        btnPlayNow.setVisibility(View.GONE);
        imvExercise.setClickable(true);
    }

    private void handleDeleteExercise(final int id, final Dialog dialog, final String imageLink) {
        adminPolyfitServices.deleteExercise(id).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", String.valueOf(response.body()));
                    try {
                        handleDeleteImage(imageLink);
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        ViewExerciseFragment.this.dismiss();

                    }
                    getActivity().registerReceiver(reloadData, new IntentFilter("reloadExercise"));
                }
                if (!response.isSuccessful()) {
                    Log.e("phayTranERROR", "delete error");
                    Log.e("PhayTran", response.code() + "");
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Delete failed!!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                Log.e("PhayTran", "Failed");
                dialog.dismiss();
                Toast.makeText(getActivity(), "Delete failed!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdateExercise(Exercise exercise) {
        Call<Exercise> call = adminPolyfitServices.updateExercise(exercise.getId(), exercise.getTitle(), exercise.getIntroduction(), exercise.getContent(), exercise.getTips(), exercise.getSets(), exercise.getReps(), exercise.getRest(), exercise.getImage_url(), exercise.getVideo_url(), exercise.getId_level());
        call.enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", "Successfully");
                    Log.e("Body", response.code() + "");
                    disableFocus();
                    layoutOptionEditExercise.setVisibility(View.GONE);
                    layoutOptionExercise.setVisibility(View.VISIBLE);
                    getActivity().registerReceiver(reloadData, new IntentFilter("reloadExercise"));
                }
                if (!response.isSuccessful()) {
                    Log.e("PhayTranERROR", response.code() + "");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                Log.e("ERR", t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void updateImage() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                assert data != null;
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(Objects.requireNonNull(getActivity()).getCacheDir(), "cropped"));
                Crop.of(source_uri, destination_uri).withAspect(imvExercise.getWidth(), imvExercise.getHeight()).start(getActivity(), ViewExerciseFragment.this);
                imvExercise.setImageURI(Crop.getOutput(data));
            } else if (requestCode == Crop.REQUEST_CROP) {
                handle_crop(resultCode, data);
            }
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imvExercise.setImageURI(Crop.getOutput(data));
        }
    }

    private void saveImage() {
        progressDialog.show();
        final StorageReference mountainsRef = storageRef.child(new Date() + ".png");
        imvExercise.setDrawingCacheEnabled(true);
        imvExercise.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imvExercise.getDrawable()).getBitmap();
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

//                            imvExercise.setImageResource(R.drawable.ic_launcher_foreground);
                            Log.d("Link", imageLink);
                            Exercise exerciseEdit = new Exercise();
                            exerciseEdit.setId(exercise.getId());
                            exerciseEdit.setTitle(tv_Title.getText().toString());
                            exerciseEdit.setIntroduction(tv_Introduction.getText().toString());
                            exerciseEdit.setContent(tv_Content.getText().toString());
                            exerciseEdit.setTips(tv_Tips.getText().toString());
                            exerciseEdit.setSets(Integer.parseInt(tv_Sets.getText().toString()));
                            exerciseEdit.setReps(Integer.parseInt(tv_Reps.getText().toString()));
                            exerciseEdit.setRest(Integer.parseInt(tv_Rest.getText().toString()));
                            exerciseEdit.setImage_url(imageLink);
                            exerciseEdit.setVideo_url(tv_videoUrl.getText().toString());
                            exerciseEdit.setId_level(exercise.getId_level());
                            Log.e("PhayTran", exerciseEdit.getTitle());
                            handleUpdateExercise(exerciseEdit);
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
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                ViewExerciseFragment.this.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("PhayTran", "onFailure: did not delete file");
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        });


    }

    private ServiceReloadExercise reloadData = new ServiceReloadExercise() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("PhayTran", "Finish");
        }
    };


}
