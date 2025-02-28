import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ImageProfileApi {
    // 프로필 이미지 업로드 (PUT 요청)
    @Multipart  // 🚀 반드시 추가!!
    @PUT("api/v1/users/mypage/image")
    fun updateProfileImage(
        @Header("Authorization") token: String,  // Authorization 헤더에 토큰 포함
        @Part image: MultipartBody.Part // 이미지 파일을 업로드하는 파라미터
    ): Call<ResponseBody>
}
