package com.example.umc.Subscribe.SubscribeResponse.Get

data class CalendarResponse(
    val resultType: String,  // "SUCCESS" 또는 "FAILURE" 결과 타입
    val error: String?,      // 오류 메시지, null일 경우 없음을 의미
    val success: Success     // 성공시 반환되는 데이터
)

data class Success(
    val subDateList: List<String>  // "2025-01-01" 형태의 날짜 리스트
)
