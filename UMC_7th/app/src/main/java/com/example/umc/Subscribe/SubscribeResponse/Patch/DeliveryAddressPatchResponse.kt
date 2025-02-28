package com.example.umc.Subscribe.SubscribeResponse.Patch

data class DeliveryAddressPatchResponse(
    val resultType: String,  // 결과 타입 (SUCCESS, FAILURE 등)
    val error: String?,  // 에러 메시지 (없으면 null)
    val success: SuccessAddressPatchResponse?  // 성공 데이터
)

data class SuccessAddressPatchResponse(
    val isSuccess: Boolean,  // 성공 여부
    val deliveryAddress: DeliveryAddressDetail?  // 배송지 정보
)

// 배송지 상세 데이터 모델
data class DeliveryAddressDetail(
    val addressId: Int,  // 배송지 ID
    val userId: Int,  // 사용자 ID
    val name: String,  // 배송지 이름
    val address: String,  // 주소
    val postNum: Int,  // 우편번호
    val phoneNum: String,  // 전화번호
    val memo: String?,  // 배송 메모 (nullable)
    val isDefault: Boolean  // 기본 배송지 여부
)
