package bangkit.capstone.waterwise.data.remote.api

import bangkit.capstone.waterwise.data.remote.response.LoginResponse
import bangkit.capstone.waterwise.data.remote.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    // Register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phoneNumber") phone: String,
        @Field("password") password: String
    ): RegisterResponse

    // Login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // Login with Google
    @FormUrlEncoded
    @POST("loginWithGoogle")
    suspend fun loginWithGoogle(
        @Field("firebaseId") firebaseId: String,
        @Field("email") email: String
    ): LoginResponse
}
