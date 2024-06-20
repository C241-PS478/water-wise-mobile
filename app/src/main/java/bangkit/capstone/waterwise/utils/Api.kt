package bangkit.capstone.waterwise.utils

import bangkit.capstone.waterwise.BuildConfig
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS


object Api {
    private val baseUrl = BuildConfig.BASE_API_URL

    private val loggingInterceptor = if(BuildConfig.DEBUG) {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .readTimeout(20, SECONDS)
        .connectTimeout(20, SECONDS)
        .connectionPool(ConnectionPool(0, 5, MINUTES))
        .protocols(listOf(Protocol.HTTP_1_1))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T: Any> service (o: Class<T>): T {
        if (!o.isInterface) throw IllegalArgumentException("Service must be an interface")

        return retrofit.create(o)
    }
}