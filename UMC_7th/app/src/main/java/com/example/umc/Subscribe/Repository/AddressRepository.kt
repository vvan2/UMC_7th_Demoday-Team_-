package com.example.umc.Subscribe.Repository

import android.content.Context
import com.example.umc.Subscribe.SubscribeResponse.Get.DeliveryGetResponse
import com.example.umc.Subscribe.SubscribeResponse.Post.SuccessAddressResponse
import com.google.gson.Gson

object AddressRepository {
    private const val PREFS_NAME = "address_prefs"
    private const val KEY_ADDRESS_ID = "address_id"
    private const val KEY_DEFAULT_ADDRESS = "default_address"

    // ✅ 배송지 ID 저장
    fun saveAddressId(context: Context, addressId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ADDRESS_ID, addressId).apply()
    }

    // ✅ 저장된 기본 배송지 ID 가져오기
    fun getAddressId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ADDRESS_ID, null)
    }

    // ✅ 기본 배송지 정보 저장
    fun saveDefaultAddress(context: Context, address: DeliveryGetResponse) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(address)
        editor.putString(KEY_DEFAULT_ADDRESS, json)
        editor.apply()
    }

    // ✅ 기본 배송지 정보 가져오기
    fun getDefaultAddress(context: Context): DeliveryGetResponse? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_DEFAULT_ADDRESS, null)
        return if (json != null) {
            Gson().fromJson(json, DeliveryGetResponse::class.java)
        } else {
            null
        }
    }
}
