package bangkit.capstone.waterwise.news

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Service {
    @GET("https://newsapi.org/v2/everything")
    suspend fun findAll(
        @Header("Authorization") token: String,
        @Query("q") query: String = "water",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsResponse
}