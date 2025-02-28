package com.example.umc.model.service

import com.example.umc.model.CartRequest
import com.example.umc.model.CartResponse
import com.example.umc.model.request.DeleteManualMealsRequest
import com.example.umc.model.request.MealRefreshRequest
import com.example.umc.model.request.PatchFavoriteDeleteRequest
import com.example.umc.model.request.PatchFavoriteRequest
import com.example.umc.model.request.PatchMealsDislikeDeleteRequest
import com.example.umc.model.request.PatchMealsDislikeRequest
import com.example.umc.model.request.PatchPreferenceRequest
import com.example.umc.model.request.PostCompleteMealRequest
import com.example.umc.model.request.PostDailyMealRequest
import com.example.umc.model.request.PostManualMealsRequest
import com.example.umc.model.request.PostRefreshMealRequest
import com.example.umc.model.response.DeleteManualMealsResponse
import com.example.umc.model.response.GetFavoriteMealsCalorieResponse
import com.example.umc.model.response.GetFavoriteMealsResponse
import com.example.umc.model.response.GetManualMealsResponse
import com.example.umc.model.response.GetMealsDetailResponse
import com.example.umc.model.response.MealRefreshResponse
import com.example.umc.model.response.PatchFavoriteDeleteResponse
import com.example.umc.model.response.PostManualMealsResponse
import com.example.umc.model.response.PatchFavoriteResponse
import com.example.umc.model.response.PatchMealsDislikeDeleteResponse
import com.example.umc.model.response.PatchMealsDislikeResponse
import com.example.umc.model.response.PatchPreferenceResponse
import com.example.umc.model.response.PostCompleteMealResponse
import com.example.umc.model.response.PostDailyMealResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MealApiService {
    @POST("api/v1/meals/manual")
    suspend fun postManualMeals(
        @Body request: PostManualMealsRequest,
        @Header("Authorization") token: String
    ): Response<PostManualMealsResponse>

    @GET("api/v1/meals/manual/list")
    suspend fun getManualMeals(
        @Header("Authorization") token: String
    ): Response<GetManualMealsResponse>

    @HTTP(method = "DELETE", path = "api/v1/meals/manual/delete", hasBody = true)
    suspend fun deleteManualMeals(
        @Body requestBody: DeleteManualMealsRequest,
        @Header("Authorization") token: String
    ): Response<Void>

    @GET("api/v1/meals/favorite/list/latest")
    suspend fun getFavoriteLatest(
        @Header("Authorization") token: String
    ): Response<GetFavoriteMealsResponse>

    @GET("api/v1/meals/favorite/list/calorie")
    suspend fun getFavoriteCalorie(
        @Header("Authorization") token: String
    ): Response<GetFavoriteMealsCalorieResponse>

    @GET("api/v1/meals/detail/list")
    suspend fun getMealsDetail(
        @Query("mealId") mealId : Int,
        @Header("Authorization") token: String
    ): Response<GetMealsDetailResponse>

    @POST("api/v1/meals/complete")
    suspend fun postCompleteMeal(
        @Body body: PostCompleteMealRequest,
        @Header("Authorization") token: String
    ): Response<PostCompleteMealResponse>

    @PATCH("api/v1/meals/favorite")
    suspend fun patchFavoriteMeal(
        @Body favoriteRequest: PatchFavoriteRequest,
        @Header("Authorization") token: String
    ): Response<PatchFavoriteResponse>

    @PATCH("api/v1/meals/favorite/delete")
    suspend fun patchFavoriteDelete(
        @Body preferenceRequest: PatchFavoriteDeleteRequest,
        @Header("Authorization") token: String
    ): Response<PatchFavoriteDeleteResponse>

    @PATCH("api/v1/meals/preference")
    suspend fun patchPreferenceMeal(
        @Body preferenceRequest: PatchPreferenceRequest,
        @Header("Authorization") token: String
    ): Response<PatchPreferenceResponse>

    @PATCH("api/v1/meals/dislike")
    suspend fun patchMealsDislike(
        @Body preferenceRequest: PatchMealsDislikeRequest,
        @Header("Authorization") token: String
    ): Response<PatchMealsDislikeResponse>

    @PATCH("api/v1/meals/dislike/delete")
    suspend fun patchMealsDislikeDelete(
        @Body preferenceRequest: PatchMealsDislikeDeleteRequest,
        @Header("Authorization") token: String
    ): Response<PatchMealsDislikeDeleteResponse>

    @POST("/api/v1/meals/refresh")
    suspend fun refreshMeal(
        @Body request: MealRefreshRequest
    ): MealRefreshResponse

    @POST("api/v1/meals/daily")
    suspend fun getDailyMeal(
        @Body dailyMealRequest: PostDailyMealRequest,
        @Header("Authorization") token: String
    ): Response<PostDailyMealResponse>

    @POST("/api/v1/subscribes/meals/cart")
    suspend fun addToCart(@Body request: CartRequest): Response<CartResponse>
}