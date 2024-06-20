package bangkit.capstone.waterwise.review

import bangkit.capstone.waterwise.review.types.CreateReviewResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface Service {
    @POST("/sources")
    @FormUrlEncoded
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Field("predictionId") predictionId: String,
        @Field("lat") lat: Double,
        @Field("long") long: Double,
        @Field("description") description: String?
    ): Response<CreateReviewResponse>
}