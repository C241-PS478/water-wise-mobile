package bangkit.capstone.waterwise.utils

import bangkit.capstone.waterwise.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Api {
    private val loggingInterceptor = if(BuildConfig.DEBUG) {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://story-api.dicoding.dev/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T: Any> service (o: Class<T>): T {
        if (!o.isInterface) throw IllegalArgumentException("Service must be an interface")

        return retrofit.create(o)
    }
}