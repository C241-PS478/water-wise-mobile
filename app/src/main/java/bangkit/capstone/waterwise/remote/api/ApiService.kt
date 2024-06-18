package bangkit.capstone.waterwise.data.remote.api

import bangkit.capstone.waterwise.remote.response.LoginResponse
import bangkit.capstone.waterwise.remote.response.RegisterResponse
import bangkit.capstone.waterwise.remote.response.MainResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    //register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    //login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("data/user/name")
    fun getName(
        @Field("name") name: String,
    ): Call<MainResponse>


}
