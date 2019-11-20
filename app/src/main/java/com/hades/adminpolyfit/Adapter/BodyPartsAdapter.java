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
import com.hades.adminpolyfit.Utils.Constants;
import com.hades.adminpolyfit.Fragments.MixFragment;
import com.hades.adminpolyfit.Model.Bodyparts;
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

/**
 * Created by Hades on 27,October,2019
 **/
public class BodyPartsAdapter extends RecyclerView.Adapter<BodyPartsAdapter.ViewHolder> {

    List<Bodyparts> bodypartsList;
    Context context;
    Dialog dialog,dialogEdit,dialogUpdate;
    ImageView imvAddBodyParts;
    MixFragment mixFragment;
    private AdminPolyfitServices adminPolyfitServices;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    public BodyPartsAdapter(List<Bodyparts> bodypartsList, Context context,MixFragment mixFragment) {
        this.bodypartsList = bodypartsList;
        this.context = context;
        this.mixFragment=mixFragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.one_item_bodyparts, parent, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        return new ViewHolder(itemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.titleBodyParts.setText(bodypartsList.get(position).getTitle());
        Picasso.get().load(bodypartsList.get(position).getImageUrl()).into(holder.imageBodyParts);
        holder.imageBodyParts.setClipToOutline(true);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int posittion) {

            }
        });
        holder.popupBodyParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, holder.popupBodyParts);
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
                                handleDeleteBodyParts(bodypartsList.get(position).getIdBodyPart(), bodypartsList.get(position).getImageUrl());
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void handleDeleteBodyParts(Integer idBodyPart, final String imageUrl) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete);
        CardView cancelDelete = dialog.findViewById(R.id.cancelDelete);
        TextView tvAccept = dialog.findViewById(R.id.tvDelete);
        tvAccept.setVisibility(View.GONE);
        GifImageView processDelete = dialog.findViewById(R.id.processDelete);
        processDelete.setVisibility(View.VISIBLE);
        cancelDelete.setVisibility(View.GONE);
        dialog.show();

        adminPolyfitServices.deleteBodyParts(idBodyPart).enqueue(new Callback<Bodyparts>() {
            @Override
            public void onResponse(Call<Bodyparts> call, Response<Bodyparts> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", String.valueOf(response.body()));
                    try {
                        handleDeleteImage(imageUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((MixFragment) mixFragment).getAllBodyParts();
                        dialog.dismiss();

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
            public void onFailure(Call<Bodyparts> call, Throwable t) {
                Log.e("PhayTran", "Failed");
                Toast.makeText(context, "Delete failed!!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    private void createDialogEdit(final int position) {
        dialogEdit=new Dialog(context);
        dialogEdit.setContentView(R.layout.dialog_add_bodyparts);
        TextView titleDialog=dialogEdit.findViewById(R.id.dialogBodyPartsTitle);
        final EditText edtTitle =dialogEdit.findViewById(R.id.edtTitleAddBodyParts);
        edtTitle.setText(bodypartsList.get(position).getTitle());
        edtTitle.requestFocus();
        CardView cardSave=dialogEdit.findViewById(R.id.saveNewBodyParts);
        TextView stt=dialogEdit.findViewById(R.id.tvButtonActionDialogBodyParts);
        imvAddBodyParts=dialogEdit.findViewById(R.id.imvAddBodyParts);
        Picasso.get().load(bodypartsList.get(position).getImageUrl()).into(imvAddBodyParts);
        imvAddBodyParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        stt.setText("Update");
        titleDialog.setText("Update Ingredient");
        dialogEdit.show();
        cardSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUpdate=new Dialog(context);
                dialogUpdate.setContentView(R.layout.dialog_upload);
                dialogUpdate.setCancelable(false);
                dialogUpdate.show();
                Bodyparts bodyparts=new Bodyparts();
                bodyparts.setIdBodyPart(bodypartsList.get(position).getIdBodyPart());
                bodyparts.setImageUrl(bodypartsList.get(position).getImageUrl());
                bodyparts.setTitle(edtTitle.getText().toString());
                handleUpdateBodyParts(bodyparts);
            }
        });
    }

    private void handleUpdateBodyParts(Bodyparts bodyparts) {
        Call<Bodyparts> call = adminPolyfitServices.updateBodyParts(bodyparts.getIdBodyPart(), bodyparts.getTitle(),bodyparts.getImageUrl());
        call.enqueue(new Callback<Bodyparts>() {
            @Override
            public void onResponse(Call<Bodyparts> call, Response<Bodyparts> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", "Successfully");
                    Log.e("Body", response.code() + "");
                    dialogUpdate.dismiss();
                    dialogEdit.dismiss();
                    ((MixFragment) mixFragment).getAllBodyParts();


                }
                if (!response.isSuccessful()) {
                    Log.e("PhayTranERROR", response.code() + "");
                }
                dialogEdit.dismiss();
                dialogUpdate.dismiss();
            }

            @Override
            public void onFailure(Call<Bodyparts> call, Throwable t) {
                Log.e("ERR", t.getMessage());
                dialogEdit.dismiss();
                dialogUpdate.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bodypartsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titleBodyParts;
        private ImageView popupBodyParts;
        private GifImageView imageBodyParts, editBodyParts, deleteBodyParts;
        private ItemClickListener itemClickListener;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleBodyParts = itemView.findViewById(R.id.rcTitleBodyParts);
            imageBodyParts = itemView.findViewById(R.id.rcImageBodyParts);
            editBodyParts = itemView.findViewById(R.id.itemEditBodyParts);
            deleteBodyParts = itemView.findViewById(R.id.itemDeleteBodyParts);
            popupBodyParts=itemView.findViewById(R.id.popupBodyParts);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());

        }
    }

    private void handleDeleteImage(String urlImage) {
        StorageReference storageRef = storage.getReferenceFromUrl(urlImage);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("PhayTran", "onSuccess: deleted file");
                ((MixFragment) mixFragment).getAllBodyParts();
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
