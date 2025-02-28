package com.example.umc.UserApi.Response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("reslutType")val status: String,
    @SerializedName("error")val message: String,
    @SerializedName("success") val data: UserProfileData

)

data class UserProfileData(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("name") val name: String,
    @SerializedName("birth") val birth: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNum") val phone: String  // ✅ JSON의 "phoneNum"을 phone으로 매핑
)