package com.example.umc.model.service

import com.example.umc.model.request.DeleteManualMealsRequest
import com.example.umc.model.request.DeleteMaterialMarkRequest
import com.example.umc.model.request.PostMaterialMarkRequest
import com.example.umc.model.response.DeleteMaterialMarkResponse
import com.example.umc.model.response.GetMaterialAllResponse
import com.example.umc.model.response.GetMaterialMarkResponse
import com.example.umc.model.response.GetMaterialRankAllResponse
import com.example.umc.model.response.GetMaterialRankVarietyResponse
import com.example.umc.model.response.GetMaterialVarietyResponse
import com.example.umc.model.response.PostMaterialMarkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MaterialApiService {
    @GET("api/v1/materials/rank/variety")
    suspend fun getMaterialRankVariety(
        @Query("name")variety : String
    ): Response<GetMaterialRankVarietyResponse>

    @GET("api/v1/materials/variety")
    suspend fun getMaterialsVariety(
        @Query("name")variety: String
    ): Response<GetMaterialVarietyResponse>

    @GET("api/v1/materials/rank/all")
    suspend fun getMaterialRankAll(
    ): Response<GetMaterialRankAllResponse>

    @GET("api/v1/materials/all")
    suspend fun getMaterialAll(
    ): Response<GetMaterialAllResponse>

    @POST("api/v1/materials/mark")
    suspend fun postMaterialMark(
        @Body requestBody: PostMaterialMarkRequest,
        @Header("Authorization") token: String
    ): Response<PostMaterialMarkResponse>

    @GET("api/v1/materials/mark")
    suspend fun getMaterialMark(
        @Header("Authorization") token: String
    ): Response<GetMaterialMarkResponse>

    @HTTP(method = "DELETE", path = "api/v1/materials/mark", hasBody = true)
    suspend fun deleteMaterialMark(
        @Body requestBody: DeleteMaterialMarkRequest,
        @Header("Authorization") token: String
    ): Response<DeleteMaterialMarkResponse>
}