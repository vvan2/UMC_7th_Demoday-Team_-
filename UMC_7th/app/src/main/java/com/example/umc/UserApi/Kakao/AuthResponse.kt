package com.example.umc.UserApi.Kakao

// Main authentication response data class
data class AuthResponse(
    val resultType: String? = null,  // Indicates the overall result of the authentication process
    val success: LoginSuccessDetails? = null,  // Contains successful login details
    val error: AuthenticationErrorResponse? = null  // Contains error information if authentication fails
) {
    // Computed properties for easy access to nested data
    val accessToken: String?
        get() = success?.accessToken

    val refreshToken: String?
        get() = success?.refreshToken

    val user: UserProfileInfo?
        get() = success?.user
}

// Detailed structure for successful login
data class LoginSuccessDetails(
    val user: UserProfileInfo?,        // User's profile information
    val accessToken: String?,          // JWT access token for authentication
    val refreshToken: String?          // JWT refresh token for obtaining new access tokens
)

// User profile information extracted from authentication
data class UserProfileInfo(
    val userId: Long? = null,  // Primary user identifier
    val id: Long? = null,      // Alternative user identifier
    val email: String? = null, // User's email address
    val name: String? = null,  // User's full name
    val nickname: String? = null,  // User's display name
    val profileImageUrl: String? = null  // URL of user's profile picture
)

// Structured error response for authentication failures
data class AuthenticationErrorResponse(
    val errorCode: String? = null,     // Machine-readable error code
    val reason: String? = null,        // Human-readable error description
    val timestamp: Long? = null,       // Timestamp of the error
    val requestId: String? = null      // Unique identifier for the failed request
)