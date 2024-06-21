package bangkit.capstone.waterwise.data.remote.api

import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.data.remote.response.RegisterResponse
import bangkit.capstone.waterwise.ui.main.ListPostResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Register
    @FormUrlEncoded
    @POST("/auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // Login
    @FormUrlEncoded
    @POST("/auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    // Login with Google
    @FormUrlEncoded
    @POST("/auth/login/google")
    suspend fun loginWithGoogle(
        @Field("firebaseId") firebaseId: String,
        @Field("email") email: String
    ): Response<LoginResponse>

    @GET("sources")
    suspend fun getPostWithLocation(
        @Query("location") location: Int = 1,
    ): ListPostResponse
}
