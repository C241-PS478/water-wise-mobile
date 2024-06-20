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
    ): Response<PredictionResponse<PredictionResultRes>>

    @Multipart
    @POST("/predict/iot")
    suspend fun predictQualityByData(
        @Header("Authorization") token: String,
        @Part("solids") solids: Float,
        @Part("turbidity") turbidity: Float,
        @Part("organic_carbon") organicCarbon: Float,
        @Part("chloramines") chloramines: Float,
        @Part("sulfate") sulfate: Float,
        @Part("ph") ph: Float
    ): Response<PredictionResponse<PredictionByDataRes>>
}