package com.example.umc.model.service

import com.example.umc.model.response.KamisPriceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KamisApiService {
    @GET("service/price/xml.do")
    fun getDailySalesList(
        @Query("action") aaction: String = "recentlyPriceTrendList",
        @Query("p_productno") productNo: String,
        @Query("p_regday") regDay: String,
        @Query("p_cert_key") certKey: String,
        @Query("p_cert_id") certId: String,
        @Query("p_returntype") returnType: String = "json"
    ): Call<KamisPriceResponse>
}
