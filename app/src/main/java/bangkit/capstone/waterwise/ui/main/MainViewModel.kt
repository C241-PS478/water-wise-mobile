package bangkit.capstone.waterwise.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bangkit.capstone.waterwise.data.remote.api.ApiConfig
import bangkit.capstone.waterwise.data.remote.api.ApiService
import bangkit.capstone.waterwise.news.Service
import bangkit.capstone.waterwise.remote.response.MainResponse
import bangkit.capstone.waterwise.utils.Api
import bangkit.capstone.waterwise.water_detection.ui.CameraActivity.Companion.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel {
    val newsService = Api.service(Service::class.java)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _user = MutableLiveData<MainResponse?>()
    val user: LiveData<MainResponse?> = _user

    fun getName(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getName(username)
        client.enqueue(object : Callback<MainResponse> {
            override fun onResponse(
                call: Call<MainResponse>,
                response: Response<MainResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    Log.e(TAG, response.message())
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, t.message ?: "Unknown error")
            }
        })
    }

    fun findSomeNews(){
//        val apiKey = BuildConfig.NEWS_API_KEY
        val apiKey = ""
        val client = newsService.findSome(apiKey)

    }
}