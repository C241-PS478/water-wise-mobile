package bangkit.capstone.waterwise.data.remote.api

import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.data.remote.response.RegisterResponse
import bangkit.capstone.waterwise.remote.response.MainResponse
import retrofit2.Call
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
        @Field("email") email: String,
        @Field("phoneNumber") phone: String,
        @Field("password") password: String
    ): RegisterResponse

    // Login
    @FormUrlEncoded
    @POST("/auth/login/dev")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // Login with Google
    @FormUrlEncoded
    @POST("/auth/login/dev")
    suspend fun loginWithGoogle(
        @Field("firebaseId") firebaseId: String,
        @Field("email") email: String
    ): LoginResponse

    @GET("/auth/login/dev") // Replace with your actual endpoint
    fun getName(
        @Query("name") name: String
    ): Call<MainResponse>
}
