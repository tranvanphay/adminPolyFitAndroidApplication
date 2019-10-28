package com.hades.adminpolyfit.Services;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.hades.adminpolyfit.Model.Bodyparts;
import com.hades.adminpolyfit.Model.Dish;
import com.hades.adminpolyfit.Model.Exercise;
import com.hades.adminpolyfit.Model.Ingredients;
import com.hades.adminpolyfit.Model.Level;
import com.hades.adminpolyfit.Model.Meals;
import com.hades.adminpolyfit.Model.Quotes;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Hades on 16,October,2019
 **/
public interface AdminPolyfitServices {

    @POST("exercises/create")
    @FormUrlEncoded
    Observable<String> addExercise(@Field("title") String title,
                                   @Field("introduction") String introduction,
                                   @Field("content") String content,
                                   @Field("tips") String tips,
                                   @Field("sets") Integer sets,
                                   @Field("reps") Integer reps,
                                   @Field("rest") Integer rest,
                                   @Field("video_url") String videoUrl,
                                   @Field("image_url") String imageUrl,
                                   @Field("id_level") Integer idLevel,
                                   @Field("bodypartsArr[]") int idBodyParts);

    @GET("exercises/getAll")
    Call<String> getAllExercise();

    @GET("exercises/exDetail/{id}")
    Call<String> getAllDetailExercise(@Path("id") Integer id);

    @PUT("exercises/update")
    @FormUrlEncoded
    Call<Exercise> updateExercise(@Field("id") int id,
                                  @Field("title") String title,
                                  @Field("introduction") String introduction,
                                  @Field("content") String content,
                                  @Field("tips") String tips,
                                  @Field("sets") Integer sets,
                                  @Field("reps") Integer reps,
                                  @Field("rest") Integer rest,
                                  @Field("image_url") String imageUrl,
                                  @Field("video_url") String videoUrl,
                                  @Field("id_level") Integer idLevel);

    @DELETE("exercises/delete/{id}")
    Call<Exercise> deleteExercise(@Path("id") int id);

    @POST("dishes/create")
    @FormUrlEncoded
    Observable<String> addDish(@Field("title") String title,
                               @Field("image_url") String imageUrl,
                               @Field("protein") Double protein,
                               @Field("fat") Double fat,
                               @Field("carb") Double carb,
                               @Field("calories") Double calories,
                               @Field("id_meals") Integer idMeals,
                               @Field("description") String desDish,
                               @Field("ingredientsArr[]") Integer idIngredients);
    @DELETE("dishes/delete/{id}")
    Call<Dish> deleteDish(@Path("id") int id);


    @PUT("dishes/update")
    @FormUrlEncoded
    Call<Dish> updateDish(@Field("id") int id,
                          @Field("title") String title,
                          @Field("protein") double protein,
                          @Field("fat") double fat,
                          @Field("carb") double carb,
                          @Field("calories") double calories,
                          @Field("image_url") String imageUrl/*,
                          @Field("polyfitMealId") Integer id_meals*/
                          /*@Field("id_ingredients") Integer id_ingredients*/);


    //Dishes
    @GET("dishes/getAll")
    Call<String> getAllDish();
    //Meals
    @GET("meals/getAll")
    Call<String> getAllMeals();

    @POST("meals/create")
    @FormUrlEncoded
    Observable<String> addMeals(@Field("title") String title,
                                @Field("image_url") String imageUrl,
                                @Field("id_diets")Integer idDiets);

    @PUT("meals/update")
    @FormUrlEncoded
    Call<Meals> updateMeals(@Field("id") int id,
                            @Field("title") String title,
                            @Field("image_url") String imageUrl);

    @DELETE("meals/delete/{id}")
    Call<Meals> deleteMeals(@Path("id") int id);


    //BodyParts
    @GET("bodyparts/getAll")
    Call<String> getAllBodyParts();
    @POST("bodyparts/create")
    @FormUrlEncoded
    Observable<String> addBodyParts(@Field("title") String title,
                                @Field("image_url") String imageUrl);

    @PUT("bodyparts/update")
    @FormUrlEncoded
    Call<Bodyparts> updateBodyParts(@Field("id") int id,
                                    @Field("title") String title,
                                    @Field("image_url") String imageUrl);

    @DELETE("bodyparts/delete/{id}")
    Call<Bodyparts> deleteBodyParts(@Path("id") int id);


    //Quotes
    @GET("qoutes/getAll")
    Call<String> getAllQuotes();

    @POST("qoutes/create")
    @FormUrlEncoded
    Observable<String> addQuotes(@Field("title") String title,
                                 @Field("image_url") String imageUrl);
    @DELETE("qoutes/delete/{id}")
    Call<Quotes> deleteQuotes(@Path("id") int id);

    @PUT("qoutes/update")
    @FormUrlEncoded
    Call<Quotes> updateQuotes(@Field("id") int id,
                            @Field("title") String title,
                            @Field("image_url") String description);


    //Ingredient
    @GET("ingredients/getAll")
    Call<String> getAllIngredient();

    @POST("ingredients/create")
    @FormUrlEncoded
    Observable<String> addIngredient(@Field("title") String title,
                                     @Field("image_url")  String imageUrl);
    @DELETE("ingredients/delete/{id}")
    Call<Ingredients> deleteIngredient(@Path("id") int id);
    @PUT("ingredients/update")
    @FormUrlEncoded
    Call<Ingredients> updateIngredients(@Field("id") int id,
                                        @Field("title") String title,
                                        @Field("image_url") String imageUrl);
    //Level
    @GET("level/getAll")
    Call<String> getAllLevel();
    @POST("level/create")
    @FormUrlEncoded
    Observable<String> addLevel(@Field("title") String title,
                                     @Field("image") String imageUrl,
                                     @Field("description") String description);
    @DELETE("level/delete/{id}")
    Call<Level> deleteLevel(@Path("id") int id);

    @PUT("level/update")
    @FormUrlEncoded
    Call<Level> updateLevel(@Field("id") int id,
                            @Field("title") String title,
                            @Field("description") String description);


}
