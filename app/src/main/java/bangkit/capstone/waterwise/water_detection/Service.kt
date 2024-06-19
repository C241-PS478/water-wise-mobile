package bangkit.capstone.waterwise.water_detection

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Service {
    @Multipart
    @POST("/predict")
    suspend fun predictQuality(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<PredictionResponse>

    @POST("/predictions")
    suspend fun predictQualityByData(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<PredictionResponse>
}