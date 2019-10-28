package com.hades.adminpolyfit.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hades.adminpolyfit.Fragments.QuotesFragment;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.Model.Ingredients;
import com.hades.adminpolyfit.Model.Level;
import com.hades.adminpolyfit.Model.Quotes;
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
public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {

    List<Level> levelList;
    Context context;
    private AdminPolyfitServices adminPolyfitServices;
    MixFragment mixFragment;
    Dialog dialog, dialogEdit,dialogUpdate;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    String imageLink;
    ImageView imvAddLevel;

    public LevelAdapter(List<Level> levelList, Context context, MixFragment mixFragment) {
        this.levelList = levelList;
        this.context = context;
        this.mixFragment = mixFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.one_item_level, parent, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        return new ViewHolder(itemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.titleLevel.setText(levelList.get(position).getTitle());
        Picasso.get().load(levelList.get(position).getImage()).into(holder.imageLevel);
        holder.imageLevel.setClipToOutline(true);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int posittion) {
//                if (holder.layoutOptionLevel.getVisibility() == View.GONE) {
//                    holder.layoutOptionLevel.setVisibility(View.VISIBLE);
//                } else {
//                    holder.layoutOptionLevel.setVisibility(View.GONE);
//                }
            }
        });

      /*  holder.deleteLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDeleteLevel(levelList.get(position).getId(), levelList.get(position).getImage());
            }
        });
        holder.editLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        holder.ic_popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.ic_popupMenu);
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
                                handleDeleteLevel(levelList.get(position).getId(), levelList.get(position).getImage());
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titleLevel;
        private ImageView ic_popupMenu;
        private GifImageView imageLevel, editLevel, deleteLevel;
        private ItemClickListener itemClickListener;
        LinearLayout layoutOptionLevel;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleLevel = itemView.findViewById(R.id.rcTitleLevel);
            imageLevel = itemView.findViewById(R.id.rcImageLevel);
            editLevel = itemView.findViewById(R.id.itemEditLevel);
            deleteLevel = itemView.findViewById(R.id.itemDeleteLevel);
            layoutOptionLevel = itemView.findViewById(R.id.layoutOptionLevel);
            ic_popupMenu = itemView.findViewById(R.id.ic_popupMenu);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());

        }
    }

    private void handleDeleteLevel(final int id, final String imageLink) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete);
        CardView cancelDelete = dialog.findViewById(R.id.cancelDelete);
        TextView tvAccept = dialog.findViewById(R.id.tvDelete);
        tvAccept.setVisibility(View.GONE);
        GifImageView processDelete = dialog.findViewById(R.id.processDelete);
        processDelete.setVisibility(View.VISIBLE);
        cancelDelete.setVisibility(View.GONE);
        dialog.show();

        adminPolyfitServices.deleteLevel(id).enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", String.valueOf(response.body()));
                    try {
                        handleDeleteImage(imageLink);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((MixFragment)mixFragment).getAllLevel();

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
            public void onFailure(Call<Level> call, Throwable t) {
                Log.e("PhayTran", "Failed");
                Toast.makeText(context, "Delete failed!!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


    }

    private void handleDeleteImage(String urlImage) {
        StorageReference storageRef = storage.getReferenceFromUrl(urlImage);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("PhayTran", "onSuccess: deleted file");
                ((MixFragment) mixFragment).getAllLevel();
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

    private void handleUpdateLevel(Level level) {
        Call<Level> call = adminPolyfitServices.updateLevel(level.getId(), level.getTitle(), level.getDescription());
        call.enqueue(new Callback<Level>() {
            @Override
            public void onResponse(Call<Level> call, Response<Level> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", "Successfully");
                    Log.e("Body", response.code() + "");
                    dialogUpdate.dismiss();
                    dialogEdit.dismiss();
                    ((MixFragment) mixFragment).getAllLevel();


                }
                if (!response.isSuccessful()) {
                    Log.e("PhayTranERROR", response.code() + "");
                }
                dialogEdit.dismiss();
                dialogUpdate.dismiss();
            }

            @Override
            public void onFailure(Call<Level> call, Throwable t) {
                Log.e("ERR", t.getMessage());
                dialogEdit.dismiss();
                dialogUpdate.dismiss();
            }
        });

    }
    private void createDialogEdit(final int position){
        dialogEdit=new Dialog(context);
        dialogEdit.setContentView(R.layout.dialog_add_level);
        TextView titleDialog=dialogEdit.findViewById(R.id.dialogLevelTitle);
        final EditText edtTitle =dialogEdit.findViewById(R.id.edtTitleAddLevel);
        edtTitle.setText(levelList.get(position).getTitle());
        edtTitle.requestFocus();
        final EditText edtDes =dialogEdit.findViewById(R.id.edtDescriptionAddLevel);
        edtDes.setText(levelList.get(position).getDescription());
        CardView cardSave=dialogEdit.findViewById(R.id.saveNewLevel);
        TextView stt=dialogEdit.findViewById(R.id.tvButtonActionDialogLevel);
        imvAddLevel=dialogEdit.findViewById(R.id.imvAddLevel);
        Picasso.get().load(levelList.get(position).getImage()).into(imvAddLevel);
        imvAddLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        stt.setText("Update");
        titleDialog.setText("Update level");
        dialogEdit.show();
        cardSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUpdate=new Dialog(context);
                dialogUpdate.setContentView(R.layout.dialog_upload);
                dialogUpdate.setCancelable(false);
                dialogUpdate.show();
                Level level=new Level();
                level.setId(levelList.get(position).getId());
                level.setImage(levelList.get(position).getImage());
                level.setTitle(edtTitle.getText().toString());
                level.setDescription(edtDes.getText().toString());
                handleUpdateLevel(level);
            }
        });
    }




}
