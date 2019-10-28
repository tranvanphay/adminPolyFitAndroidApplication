package com.hades.adminpolyfit.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hades.adminpolyfit.Constants.Constants;
import com.hades.adminpolyfit.Fragments.MixFragment;
import com.hades.adminpolyfit.Model.Bodyparts;
import com.hades.adminpolyfit.Model.Level;
import com.hades.adminpolyfit.Model.Meals;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.exceptions.OnErrorThrowable;

/**
 * Created by Hades on 27,October,2019
 **/
public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.ViewHolder> {

    List<Meals> mealsList;
    Context context;
    private AdminPolyfitServices adminPolyfitServices;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    MixFragment mixFragment;
    Dialog dialog, dialogEdit, dialogUpdate;
    ImageView imvAddMeals;
    StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);

    public MealsAdapter(List<Meals> mealsList, Context context, MixFragment mixFragment) {
        this.mealsList = mealsList;
        this.context = context;
        this.mixFragment = mixFragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.one_item_meals, parent, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        return new ViewHolder(itemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.titleMeals.setText(mealsList.get(position).getTitle());
        Picasso.get().load(mealsList.get(position).getImageUrl()).into(holder.imageMeals);
        holder.imageMeals.setClipToOutline(true);

        holder.deleteMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.editMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.popupMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.popupMeals);
                popupMenu.getMenuInflater().inflate(R.menu.popup_meu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:
                                Log.e("PhayTran", "Edit");
                                createDialogEdit(position);
                                break;
                            case R.id.delete:
                                Log.e("PhayTran", "Delete");
                                handleDeleteLevel(mealsList.get(position).getId(), mealsList.get(position).getImageUrl());
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void handleDeleteLevel(int id, final String imageUrl) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete);
        CardView cancelDelete = dialog.findViewById(R.id.cancelDelete);
        TextView tvAccept = dialog.findViewById(R.id.tvDelete);
        tvAccept.setVisibility(View.GONE);
        GifImageView processDelete = dialog.findViewById(R.id.processDelete);
        processDelete.setVisibility(View.VISIBLE);
        cancelDelete.setVisibility(View.GONE);
        dialog.show();
        adminPolyfitServices.deleteMeals(id).enqueue(new Callback<Meals>() {
            @Override
            public void onResponse(Call<Meals> call, Response<Meals> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", String.valueOf(response.body()));
                    try {
                        handleDeleteImage(imageUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((MixFragment)mixFragment).handleGetAllMeals();
                        dialog.cancel();
                    }

                }
                if (!response.isSuccessful()) {
                    Log.e("phayTranERROR", "delete error");
                    Log.e("PhayTran", response.code() + "");
                    Toast.makeText(context, "Delete failed!!!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<Meals> call, Throwable t) {
                Log.e("PhayTran", "Failed");
                Toast.makeText(context, "Delete failed!!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void createDialogEdit(final int position) {
        dialogEdit = new Dialog(context);
        dialogEdit.setContentView(R.layout.dialog_add_meals);
        TextView titleDialog = dialogEdit.findViewById(R.id.dialogMealsTitle);
        final EditText edtTitle = dialogEdit.findViewById(R.id.edtTitleAddMeals);
        edtTitle.setText(mealsList.get(position).getTitle());
        edtTitle.requestFocus();
        CardView cardSave = dialogEdit.findViewById(R.id.saveNewMeals);
        TextView stt = dialogEdit.findViewById(R.id.tvButtonActionDialogMeals);
        imvAddMeals = dialogEdit.findViewById(R.id.imvAddMeals);
        Picasso.get().load(mealsList.get(position).getImageUrl()).into(imvAddMeals);
        imvAddMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        stt.setText("Update");
        titleDialog.setText("Update meals");
        dialogEdit.show();
        cardSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUpdate = new Dialog(context);
                dialogUpdate.setContentView(R.layout.dialog_upload);
                dialogUpdate.setCancelable(false);
                dialogUpdate.show();
                Meals meals = new Meals();
                meals.setId(mealsList.get(position).getId());
                meals.setImageUrl(mealsList.get(position).getImageUrl());
                meals.setTitle(edtTitle.getText().toString());
                handleUpdateBodyParts(meals);
            }
        });

    }

    private void handleUpdateBodyParts(Meals meals) {
        Call<Meals> call = adminPolyfitServices.updateMeals(meals.getId(), meals.getTitle(), meals.getImageUrl());
        call.enqueue(new Callback<Meals>() {
            @Override
            public void onResponse(Call<Meals> call, Response<Meals> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", "Successfully");
                    Log.e("Body", response.code() + "");
                    dialogUpdate.dismiss();
                    dialogEdit.dismiss();
                    ((MixFragment) mixFragment).handleGetAllMeals();


                }
                if (!response.isSuccessful()) {
                    Log.e("PhayTranERROR", response.code() + "");
                }
                dialogEdit.dismiss();
                dialogUpdate.dismiss();
            }

            @Override
            public void onFailure(Call<Meals> call, Throwable t) {
                Log.e("ERR", t.getMessage());
                dialogEdit.dismiss();
                dialogUpdate.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titleMeals;
        private GifImageView imageMeals, editMeals, deleteMeals;
        private ItemClickListener itemClickListener;
        private ImageView popupMeals;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleMeals = itemView.findViewById(R.id.rcTitleMeals);
            imageMeals = itemView.findViewById(R.id.rcImageMeals);
            editMeals = itemView.findViewById(R.id.itemEditMeals);
            deleteMeals = itemView.findViewById(R.id.itemDeleteMeals);
            popupMeals = itemView.findViewById(R.id.popupMeals);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());

        }
    }


    private void handleDeleteImage(String urlImage) throws OnErrorThrowable {
        StorageReference storageRef = storage.getReferenceFromUrl(urlImage);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("PhayTran", "onSuccess: deleted file");
                ((MixFragment) mixFragment).handleGetAllMeals();
                dialog.dismiss();
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
