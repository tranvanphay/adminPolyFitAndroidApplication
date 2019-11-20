package com.hades.adminpolyfit.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hades.adminpolyfit.Adapter.SpinnerBodyPartsAdapter;
import com.hades.adminpolyfit.Adapter.SpinnerLevelAdapter;
import com.hades.adminpolyfit.Utils.Constants;
import com.hades.adminpolyfit.Model.Bodyparts;
import com.hades.adminpolyfit.Model.Level;
import com.hades.adminpolyfit.Model.User;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

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
    List<User> userList=new ArrayList<>();
    List<String> listToken=new ArrayList<>();


    public static AddExercisesFragment newInstance() {
        return new AddExercisesFragment();
    }

    private Dialog progressDialog;


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
        progressDialog = new Dialog(getActivity());
        progressDialog.setContentView(R.layout.dialog_upload);
       /* progressDialog.setMessage(getString(R.string.processing));*/
        progressDialog.setCancelable(false);
        getAllLevel();
        getAllBodyParts();
        getAllUser();

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
                } else if (edt_titleExercise.getText().toString().length() < 1) {
                    Toast.makeText(getActivity(), "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (edt_introductionExercise.getText().toString().length() < 1) {
                    Toast.makeText(getActivity(), "Please enter introduction", Toast.LENGTH_SHORT).show();
                } else if (edt_contentExercise.getText().toString().length() < 1) {
                    Toast.makeText(getActivity(), "Please enter content", Toast.LENGTH_SHORT).show();
                } else if (edt_tipsExercise.getText().toString().length() < 1) {
                    Toast.makeText(getActivity(), "Please enter tips", Toast.LENGTH_SHORT).show();
                } else if (edt_setsExercise.getText().toString().length() < 1) {
                    Toast.makeText(getActivity(), "Please enter set", Toast.LENGTH_SHORT).show();
                } else if (edt_repsExercise.getText().toString().length() < 1) {
                    Toast.makeText(getActivity(), "Please enter reps", Toast.LENGTH_SHORT).show();
                } else if (edt_restExercise.getText().toString().length() < 1) {
                    Toast.makeText(getActivity(), "Please enter rest", Toast.LENGTH_SHORT).show();
                } else if (imv_imageExercise.getDrawable().getConstantState() ==
                        ContextCompat.getDrawable(getActivity(), R.drawable.null_image).getConstantState()) {
                    Toast.makeText(getActivity(), "Please insert picture", Toast.LENGTH_SHORT).show();
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
        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i < bodypartsList.size(); i++) {
            Log.e("PhayTRan", "list:::" + bodypartsList.size());
            Log.e("PhayTRan", "bodyparts ::: " + bodypartsList.get(i).isChecked());
            if (bodypartsList.get(i).isChecked()) {
                idList.add(bodypartsList.get(i).getIdBodyPart());
            }
        }
        if (idList.isEmpty()) {
            Toast.makeText(getActivity(), "Please select bodyparts", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
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
                                sendWithOtherThread();

                            } else {
                                Log.e("phayTran", s);
                                Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }));
        }
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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    private void getAllUser() {
        adminPolyfitServices.getAllUsers().enqueue(new Callback<String>() {
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
                    Type listType = new TypeToken<List<User>>() {
                    }.getType();
                    userList = gson.fromJson(jsonOutput, listType);
                    for (int i = 0; i < userList.size(); i++) {
                        listToken.add(userList.get(i).getToken());
                    }
                    Log.e("Phaytv", /*exercisesList.get(0).getId() +*/":: Success ::" + array);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }
    private void sendWithOtherThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification();
            }
        }).start();
    }


    private void pushNotification() {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "PolyFit");
            jNotification.put("body", "New exercise for you");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "logo");
            jData.put("picture", imgExercis);
            JSONArray ja = new JSONArray();
            ja.put(Constants.TOKEN);
            for (int i = 0; i < listToken.size(); i++) {
                ja.put(listToken.get(i));
            }
            jPayload.put("registration_ids", ja);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", Constants.AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}

