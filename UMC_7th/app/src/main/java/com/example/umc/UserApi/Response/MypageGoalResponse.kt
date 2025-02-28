package com.example.umc.UserApi.Response

data class MypageGoalResponse(
    val user: UserGoal
)

data class UserGoal(
    val goal: String
)
