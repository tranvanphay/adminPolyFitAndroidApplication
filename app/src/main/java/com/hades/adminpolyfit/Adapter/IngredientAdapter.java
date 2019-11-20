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
import com.hades.adminpolyfit.Fragments.IngredientFragment;
import com.hades.adminpolyfit.Model.Ingredients;
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
 * Created by Hades on 26,October,2019
 **/
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    List<Ingredients> ingredientsList;
    Context context;
    Dialog dialog,dialogEdit,dialogUpdate;
    ImageView imvIngredient;
    IngredientFragment ingredientsFragment;
    private AdminPolyfitServices adminPolyfitServices;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(Constants.STORAGE_IMAGE);
    public IngredientAdapter(List<Ingredients> ingredientsList, Context context,IngredientFragment ingredientsFragment) {
        this.ingredientsList = ingredientsList;
        this.context = context;
        this.ingredientsFragment=ingredientsFragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.one_item_ingredients, parent, false);
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        return new ViewHolder(itemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Picasso.get().load(ingredientsList.get(position).getImageUrl()).into(holder.imageIngredient);
        holder.imageIngredient.setClipToOutline(true);
        holder.titleIngredient.setText(ingredientsList.get(position).getTitle());
       /* holder.priceIngredient.setText(String.valueOf(ingredientsList.get(position).getPrice()));
        holder.unitIngredient.setText(ingredientsList.get(position).getUnit());*/
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int posittion) {

            }
        });
        holder.popupIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.popupIngredient);
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
                                handleDeleteIngredient(ingredientsList.get(position).getIdIngredients(), ingredientsList.get(position).getImageUrl());
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void handleDeleteIngredient(Integer idIngredients,final String imageUrl) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete);
        CardView cancelDelete = dialog.findViewById(R.id.cancelDelete);
        TextView tvAccept = dialog.findViewById(R.id.tvDelete);
        tvAccept.setVisibility(View.GONE);
        GifImageView processDelete = dialog.findViewById(R.id.processDelete);
        processDelete.setVisibility(View.VISIBLE);
        cancelDelete.setVisibility(View.GONE);
        dialog.show();

        adminPolyfitServices.deleteIngredient(idIngredients).enqueue(new Callback<Ingredients>() {
            @Override
            public void onResponse(Call<Ingredients> call, Response<Ingredients> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", String.valueOf(response.body()));
                    try {
                        handleDeleteImage(imageUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((IngredientFragment) ingredientsFragment).handleGetAllIngredient();
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
            public void onFailure(Call<Ingredients> call, Throwable t) {
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
        edtTitle.setText(ingredientsList.get(position).getTitle());
        edtTitle.requestFocus();
        CardView cardSave=dialogEdit.findViewById(R.id.saveNewBodyParts);
        TextView stt=dialogEdit.findViewById(R.id.tvButtonActionDialogBodyParts);
        imvIngredient=dialogEdit.findViewById(R.id.imvAddBodyParts);
        Picasso.get().load(ingredientsList.get(position).getImageUrl()).into(imvIngredient);
        imvIngredient.setOnClickListener(new View.OnClickListener() {
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
             if(edtTitle.getText().toString().length()<1){
                 Toast.makeText(context, "Please enter title", Toast.LENGTH_SHORT).show();
             }else {
                 dialogUpdate=new Dialog(context);
                 dialogUpdate.setContentView(R.layout.dialog_upload);
                 dialogUpdate.setCancelable(false);
                 dialogUpdate.show();
                 Ingredients ingredients=new Ingredients();
                 ingredients.setIdIngredients(ingredientsList.get(position).getIdIngredients());
                 ingredients.setTitle(edtTitle.getText().toString());
                 ingredients.setImageUrl(ingredientsList.get(position).getImageUrl());
                 handleUpdateIngredient(ingredients);
             }
            }
        });
    }

    private void handleUpdateIngredient(final Ingredients ingredients) {
        Call<Ingredients> call = adminPolyfitServices.updateIngredients(ingredients.getIdIngredients(), ingredients.getTitle(),ingredients.getImageUrl());
        call.enqueue(new Callback<Ingredients>() {
            @Override
            public void onResponse(Call<Ingredients> call, Response<Ingredients> response) {
                if (response.isSuccessful()) {
                    Log.e("PhayTran", "Successfully");
                    Log.e("Body", response.code() + "");
                    dialogUpdate.dismiss();
                    dialogEdit.dismiss();
                    ((IngredientFragment) ingredientsFragment).handleGetAllIngredient();


                }
                if (!response.isSuccessful()) {
                    Log.e("PhayTranERROR", response.code() + "");
                }
                dialogEdit.dismiss();
                dialogUpdate.dismiss();
            }

            @Override
            public void onFailure(Call<Ingredients> call, Throwable t) {
                Log.e("ERR", t.getMessage());
                dialogEdit.dismiss();
                dialogUpdate.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private GifImageView imageIngredient;
        private TextView titleIngredient, priceIngredient, unitIngredient;
        private ItemClickListener itemClickListener;
        private ImageView popupIngredient;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageIngredient = itemView.findViewById(R.id.imageIngredient);
            titleIngredient = itemView.findViewById(R.id.titleIngredient);
            priceIngredient = itemView.findViewById(R.id.priceIngredient);
            unitIngredient = itemView.findViewById(R.id.unitIngredient);
            popupIngredient = itemView.findViewById(R.id.popupIngredient);

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
                ((IngredientFragment) ingredientsFragment).handleGetAllIngredient();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("PhayTran", "onFailure: did not delete file");
                ((IngredientFragment) ingredientsFragment).handleGetAllIngredient();
                dialog.dismiss();
            }
        });


    }

}
