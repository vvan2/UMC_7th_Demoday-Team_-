// Response 클래스
package com.example.umc.UserApi.Response

data class UserImageResponse(
    val user: UserData
)

data class UserData(
    val image: String // 이미지 URL
)
